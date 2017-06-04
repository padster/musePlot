package today.useit.eegraph;

import today.useit.eegraph.model.EEGBundleIn;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class DataBuffer {
  public static final int MAX_AGE_SECONDS = 30;
  public static final int MAX_AGE_MS = MAX_AGE_SECONDS * 60 * 1000;

  private final Queue<EEGBundleIn> buffer = new ArrayDeque<>();

  // Returns all buffers that arrived after a certain time. Removes any that might
  public List<EEGBundleIn> getAfterAndPrune(long ms) {
    List<EEGBundleIn> result = new ArrayList<>();
    synchronized (buffer) {
      prune();
      for (EEGBundleIn bundle : buffer) {
        if (bundle.timestamp > ms) { // ms is last timestamp already received, hence >
          result.add(bundle);
          if (ms == 0) { // shortcut only one entry on first call.
            break;
          }
        }
      }
    }
    return result;
  }

  // Adds a new bundle of data at the end of the buffer.
  public void addAll(Collection<? extends EEGBundleIn> bundle) {
    synchronized (buffer) {
      prune();
      buffer.addAll(bundle);
    }
  }

  // Prune by removing stale bundles. Should be performed before other actions.
  private void prune() {
    long killBefore = System.currentTimeMillis() - MAX_AGE_MS;
    while (!buffer.isEmpty() && buffer.peek().timestamp < killBefore) {
      buffer.remove();
    }
  }
}
