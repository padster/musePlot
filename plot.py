import argparse
import math
import matplotlib.pyplot as plt

# pip3 install python-osc
from pythonosc import dispatcher
from pythonosc import osc_server
# See here for example:
# http://developer.choosemuse.com/research-tools-example/grabbing-data-from-museio-a-few-simple-examples-of-muse-osc-servers#python

import livegraph
import livehist

def cleanSubplots(r=2, c=4, pad=0.05):
    f = plt.figure()
    ax = []
    at = 1
    for i in range(r):
        row = []
        for j in range(c):
            axHere = f.add_subplot(r, c, at)
            # axHere.get_xaxis().set_visible(False)
            # axHere.get_yaxis().set_visible(False)
            row.append(axHere)
            at = at + 1
        ax.append(row)
    f.subplots_adjust(left=pad, right=1.0-pad, top=1.0-pad, bottom=pad, hspace=pad*4)
    try:
        plt.get_current_fig_manager().window.showMaximized()
    except AttributeError:
        pass # Can't maximize, sorry :(
    return ax

plots = []
lastG = None
lastB = None
lastT = None

def process(g, t, b):
    for i in range(4):
        r = i // 2
        for c in range(2 * (i % 2), 2 * (i % 2 + 1)):
            plots[r][c].setGood(g[i] == 1)
            plots[r][c].add(t[i]/b[i])
    plt.pause(0.001)
    # TODO: Histogram mode: plot same but histogram of non-buffered version.

def tryProcess():
    global lastG, lastB, lastT
    if lastG is None or lastB is None or lastT is None:
        return
    process(lastG, lastB, lastT)
    lastG = None
    lastB = None
    lastT = None

def gHandler(unused_addr, ch1, ch2, ch3, ch4):
    global lastG
    lastG = [ch1, ch2, ch3, ch4]
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

    # Listen to OSC channels we care about:
    dispatcher = dispatcher.Dispatcher()
    dispatcher.map("/debug", print)
    dispatcher.map("/muse/elements/is_good", gHandler)
    dispatcher.map("/muse/elements/beta_relative", bHandler)
    dispatcher.map("/muse/elements/theta_relative", tHandler)

    # Build live graphs
    axes = cleanSubplots()
    for i in range(len(axes)):
        plotRow = []
        for j in range(len(axes[i])):
            # TODO - titles for locations.
            if j % 2 == 0:
                plotRow.append(livegraph.LiveGraph(axes[i][j]))
            else:
                plotRow.append(livehist.LiveHist(axes[i][j]))
        plots.append(plotRow)

    # server = osc_server.ThreadingOSCUDPServer(
    server = osc_server.BlockingOSCUDPServer(
        (args.ip, args.port), dispatcher)
    print("Serving on {}".format(server.server_address))
    server.serve_forever()
