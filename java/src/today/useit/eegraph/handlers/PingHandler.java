package today.useit.eegraph.handlers;

import today.useit.eegraph.model.PingPayload;

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
  private final JsonParser<PingPayload> parser;

  @Inject
  PingHandler(JsonParser<PingPayload> parser) {
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
    int v = -1;
    if (query.containsKey("v")) {
      v = Integer.parseInt(query.get("v"));
    }
    System.out.println("Version = " + v);

    PingPayload payload = new PingPayload(v + 10);
    payload.append(-1.1);
    payload.append(1.3);
    return new JsonResponse(parser.toJson(payload));
  }
}
