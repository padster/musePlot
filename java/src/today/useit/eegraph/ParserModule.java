package today.useit.eegraph;

import com.github.padster.guiceserver.json.JsonParser;
import com.github.padster.guiceserver.json.JsonParserImpl;
import today.useit.eegraph.model.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Key;

import java.util.List;

/** Binds JSON parsers for all the models required. */
public class ParserModule extends AbstractModule {
  @Override protected void configure() {
    bind(new Key<JsonParser<PingPayload>>(){})
      .toInstance(new JsonParserImpl<PingPayload>(
        getProvider(Gson.class), new TypeToken<PingPayload>(){}.getType()));
  }
}
