package today.useit.eegraph.model;

import java.util.ArrayList;
import java.util.List;

public class PingPayload {
  public final int version;
  public final List<Double> values;

  public PingPayload(int version) {
    this.version = version;
    this.values = new ArrayList<>();
  }

  public void append(double... values) {
    for (double v : values) {
      this.values.add(v);
    }
  }
}
