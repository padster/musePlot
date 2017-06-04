package today.useit.eegraph;

import com.github.padster.guiceserver.handlers.Handler;
import com.github.padster.guiceserver.handlers.RouteHandlerResponses.MustacheResponse;
import com.github.padster.guiceserver.handlers.RouteHandlerResponses.StreamResponse;

import com.google.common.base.Preconditions;
import com.sun.net.httpserver.HttpExchange;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Serve static files
 */
@Singleton
public class StaticHandler implements Handler {
  /** Path of files within the JAR */
  public static final String ROOT_FS_PATH = "target/static/";
  /** HTTP Path of root to serve them from. */
  public static final String STATIC_DIRECTORY = "/static/";

  private final File rootFolder;

  @Inject StaticHandler() {
    rootFolder = new File(ROOT_FS_PATH);
  }

  @Override public Object handle(Map<String, String> pathDetails, HttpExchange exchange) throws Exception {
    if (!"GET".equals(exchange.getRequestMethod())) {
      throw new UnsupportedOperationException("Can only GET from StaticContent");
    }

    String path = exchange.getRequestURI().toString();
    Preconditions.checkState(path.startsWith(STATIC_DIRECTORY));

    String localPath = path.substring(STATIC_DIRECTORY.length());
    File asFile = new File(ROOT_FS_PATH + localPath);
    if (!validFile(asFile)) {
      throw new FileNotFoundException();
    }

    return new StreamResponse(asFile.length(), new FileInputStream(asFile));
  }

  /** @return Whether the file exists, and whether it is in the allowed directory. */
  private boolean validFile(File f) {
    if (!f.exists()) {
      return false;
    }
    try {
      return f.getCanonicalPath().startsWith(rootFolder.getCanonicalPath());
    } catch (IOException e) {
      return false;
    }
  }
}
