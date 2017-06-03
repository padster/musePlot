package today.useit.eegraph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.sun.net.httpserver.HttpServer;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * The actual server entrypoint - parses flags, and runs an HttpServer obtained from Guice.
 */
public class Server {
  private static final Logger logger = Logger.getLogger("DEBUG");

  public static void main(String[] args) {
    // Set up logging to file...
    try {
      logger.addHandler(new FileHandler("debug.log", true));
    } catch (java.io.IOException e) {
      System.err.println("Can't log, quitting.");
      return;
    }

    // Parse flags... (--port=)
    String portFlag = maybeGetFlag(args, "port");
    int port = portFlag != null ? Integer.parseInt(portFlag) : 80;

    try {
      // Run Guice, and start the server it provides.
      Injector injector = Guice.createInjector(
          new ParserModule(),
          new BindingModule(),
          new ServerModule(port)
      );

      HttpServer server = injector.getInstance(HttpServer.class);
      System.out.println("\n*** Running server on :" + port + "...\n");
      server.start();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  // If a --flagName is given, return the next string, otherwise null.
  private static String maybeGetFlag(String[] args, String flagName) {
    String toMatch = "--" + flagName;
    for (int i = 0; i + 1 < args.length; i++) {
      if (toMatch.equals(args[i])) {
        return args[i + 1];
      }
    }
    return null;
  }
}
