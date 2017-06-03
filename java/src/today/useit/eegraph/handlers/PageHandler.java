package today.useit.eegraph;

import com.github.padster.guiceserver.handlers.Handler;
import com.github.padster.guiceserver.handlers.RouteHandlerResponses.MustacheResponse;

import com.sun.net.httpserver.HttpExchange;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Serve the base HTML
 */
public class PageHandler implements Handler {
  @Override public Object handle(Map<String, String> pathDetails, HttpExchange exchange) {
    if (!"GET".equals(exchange.getRequestMethod())) {
      throw new UnsupportedOperationException("Can only GET from Page");
    }


    // Execute the template.
    Map<String, Object> params = new HashMap<>();
    return new MustacheResponse("main.template", params);
  }
}
