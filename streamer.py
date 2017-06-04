import argparse
import json
import requests
import time

from pythonosc import dispatcher
from pythonosc import osc_server

def currentTimeMillis():
    return int(time.time() * 1000)

SERVER_PATH = "http://localhost:8080/_/recv"

LAST_SEND_MS = currentTimeMillis()
BUFFER = []
BUFFER_TIME_SEC = 2
BUFFER_TIME_MS = BUFFER_TIME_SEC * 1000

lastG = None
lastB = None
lastT = None

def sendIfNeeded():
    global BUFFER
    global LAST_SEND_MS

    nowMs = currentTimeMillis()
    if nowMs > LAST_SEND_MS + BUFFER_TIME_MS:
        LAST_SEND_MS = nowMs
        print("Sending " + str(len(BUFFER)) + " records!")
        r = requests.post(SERVER_PATH, data=json.dumps(BUFFER))
        BUFFER = []
        if not r.text == 'OK':
            print("ERROR! ", r.text)

def queueData(payload):
    BUFFER.append(payload)
    sendIfNeeded()

# Process the latest batch of isGood, theta, beta for each channel.
def process(g, b, t):
    payload = {
        'timestamp': currentTimeMillis(),
        'values': {
            'g': g,
            'b': b,
            't': t,
        }
    }
    queueData(payload)

# Process the status only once all the values are available
def tryProcess():
    global lastG, lastB, lastT
    if lastG is None or lastB is None or lastT is None:
        return
    process(lastG, lastB, lastT)
    lastG, lastB, lastT = None, None, None # Clear status, ready for next ones.

# isGood returned, process if it's the last to show up
def gHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastG
    lastG = [ch1, ch2, ch3, ch4]
    tryProcess()

# relative beta returned, process if it's the last to show up
def bHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastB
    lastB = [ch1, ch2, ch3, ch4]
    tryProcess()

# relative theta, process if it's the last to show up
def tHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastT
    lastT = [ch1, ch2, ch3, ch4]
    tryProcess()


def main():
    global SERVER_PATH
    global LAST_SEND_MS

    # Note: Serve Muse by running:
    #   ./muse-io --osc osc.udp://localhost:5000 --device <device ID>
    parser = argparse.ArgumentParser()
    parser.add_argument("--ip",
                        default="127.0.0.1",
                        help="The ip to listen on")
    parser.add_argument("--port",
                        type=int,
                        default=5000,
                        help="The port to listen on")
    parser.add_argument("--sendTo",
                        default=SERVER_PATH,
                        help="URL to stream results to")
    args = parser.parse_args()

    SERVER_PATH = args.sendTo
    LAST_SEND_MS = currentTimeMillis()

    # Listen to OSC channels we care about:
    disp = dispatcher.Dispatcher()
    disp.map("/muse/elements/is_good", gHandler)
    disp.map("/muse/elements/beta_relative", bHandler)
    disp.map("/muse/elements/theta_relative", tHandler)

    # server = osc_server.ThreadingOSCUDPServer(
    server = osc_server.BlockingOSCUDPServer((args.ip, args.port), disp)
    print("Serving on {}".format(server.server_address))
    server.serve_forever()

if __name__ == "__main__": main()

# oath =
