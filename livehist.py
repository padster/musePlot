# Draws histogram of all tbr, rather than graph of last N

import matplotlib.pyplot as plt
import numpy as np
from collections import deque

# Scrolling graph of values, with additional connected status
class LiveHist:
    def __init__(self, ax, segments = 20, minX = 0.0, maxX = 4.0):
        self.ax = ax
        self.segments = segments
        self.minX = minX
        self.maxX = maxX
        self.isGood = False

        segWidth = (maxX - minX) / segments
        self.xs = minX + (np.arange(0, segments) + 0.5) * segWidth
        self.counts = np.zeros(segments)

        # Set up graph
        self.lineplot, = ax.plot(self.xs, self.counts, "b+-")
        self.ax.set_xlim(minX, maxX)
        self.ax.set_autoscaley_on(True)

    def setGood(self, isGood):
        """
        Update the 'good' status of the graph, changing visual appearance if so
        """
        self.isGood = isGood
        if self.isGood:
            self.ax.set_title('Connected')
            self.ax.title.set_color('black')
        else:
            self.ax.set_title('Disconnected')
            self.ax.title.set_color('red')

    def add(self, value):
        """
        Adds the most recent y value onto the graph, adding it to the right bucket
        """
        if not self.isGood:
            return

        if value < self.minX:
            bucket = 0
        elif value >= self.maxX:
            bucket = self.segments - 1
        else:
            bucket = int(self.segments * (value - self.minX) / (self.maxX - self.minX))
        self.counts[bucket] += 1

        self.lineplot.set_data(self.xs, self.counts)
        self.ax.relim()
        self.ax.autoscale_view() # rescale the y-axis
