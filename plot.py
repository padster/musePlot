import argparse
import math

# pip3 install python-osc
from pythonosc import dispatcher
from pythonosc import osc_server
# See here for example:
# http://developer.choosemuse.com/research-tools-example/grabbing-data-from-museio-a-few-simple-examples-of-muse-osc-servers#python

lastQ = None
lastT = None
lastB = None

def process(q, t, b):
    # TODO: 2x2 subplots, draw live connection strength to each.
    # TODO: add t/b to numpy rolling buffer
    # TODO: Scrolling mode: plot t/b buffer to animated matplotlib
    # TODO: Histogram mode: plot same but histogram of non-buffered version.

def tryProcess():
    global lastQ, lastT, lastB
    if lastQ is None or lastT is None or lastB is None:
        return
    process(lastQ, lastT, lastB)
    lastQ = None
    lastT = None
    lastB = None

def qHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastQ
    lastQ = [ch1, ch2, ch3, ch4]
    tryProcess()

def bHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastB
    lastB = [ch1, ch2, ch3, ch4]
    tryProcess()

def tHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastT
    lastT = [ch1, ch2, ch3, ch4]
    tryProcess()

if __name__ == "__main__":
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
    args = parser.parse_args()

    dispatcher = dispatcher.Dispatcher()
    dispatcher.map("/debug", print)
    dispatcher.map("/muse/eeg/quantization", qHandler)
    dispatcher.map("/muse/elements/theta_relative", tHandler)
    dispatcher.map("/muse/elements/beta_relative", bHandler)

    # server = osc_server.ThreadingOSCUDPServer(
    server = osc_server.BlockingOSCUDPServer(
        (args.ip, args.port), dispatcher)
    print("Serving on {}".format(server.server_address))
    server.serve_forever()
