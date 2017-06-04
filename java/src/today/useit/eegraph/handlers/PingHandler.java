package today.useit.eegraph.handlers;

import today.useit.eegraph.DataBuffer;
import today.useit.eegraph.model.EEGBundleIn;

import com.github.padster.guiceserver.handlers.Handler;
import com.github.padster.guiceserver.handlers.RouteHandlerResponses.JsonResponse;
import com.github.padster.guiceserver.json.JsonParser;

import com.sun.net.httpserver.HttpExchange;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Serve the base HTML
 */
public class PingHandler implements Handler {
  private final DataBuffer buffer;
  private final JsonParser<List<EEGBundleIn>> parser;

  @Inject
  PingHandler(DataBuffer buffer, JsonParser<List<EEGBundleIn>> parser) {
    this.buffer = buffer;
    this.parser = parser;
  }

  @Override public Object handle(Map<String, String> pathDetails, HttpExchange exchange) {
    if (!"GET".equals(exchange.getRequestMethod())) {
      throw new UnsupportedOperationException("Can only GET from Page");
    }

    Map<String, String> query = new HashMap<>();
    String q = exchange.getRequestURI().getQuery();
    for (String param : q.split("&")) {
        String pair[] = param.split("=");
        if (pair.length > 1) {
          query.put(pair[0], pair[1]);
        } else{
          query.put(pair[0], "");
        }
    }

    // HACK - actually look up and manage in local state.
    if (!query.containsKey("msAt")) {
      throw new IllegalArgumentException("Need a timestamp");
    }
    long msAt = Long.parseLong(query.get("msAt"));
    System.out.println("Version = " + msAt);
    List<EEGBundleIn> result = this.buffer.getAfterAndPrune(msAt);
    return new JsonResponse(parser.toJson(result));
  }
}
