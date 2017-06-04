package today.useit.eegraph.handlers;

import today.useit.eegraph.DataBuffer;
import today.useit.eegraph.model.EEGBundleIn;

import com.github.padster.guiceserver.handlers.Handler;
import com.github.padster.guiceserver.handlers.RouteHandlerResponses.TextResponse;
import com.github.padster.guiceserver.json.JsonParser;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Serve the base HTML
 */
public class DataInHandler implements Handler {
  private final DataBuffer buffer;
  private final JsonParser<List<EEGBundleIn>> parser;

  @Inject
  DataInHandler(DataBuffer buffer, JsonParser<List<EEGBundleIn>> parser) {
    this.buffer = buffer;
    this.parser = parser;
  }

  @Override public TextResponse handle(Map<String, String> pathDetails, HttpExchange exchange) {
    if (!"POST".equals(exchange.getRequestMethod())) {
      throw new UnsupportedOperationException("Can only POST to DataInHandler");
    }

    try {
      String postData = IOUtils.toString(exchange.getRequestBody(), "utf-8");
      this.buffer.addAll(parser.fromJson(postData));
      return new TextResponse("OK");
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
