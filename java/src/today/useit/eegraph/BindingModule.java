package today.useit.eegraph;

import com.github.padster.guiceserver.BaseBindingModule;
import today.useit.eegraph.handlers.*;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import javax.inject.Provider;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides all the path -> handler bindings, and a way for other modules to configure them.
 */
public class BindingModule extends BaseBindingModule {
  @Override protected void bindPageHandlers() {
    // HACK
    bindPageHandler("/", PageHandler.class);
  }

  protected void bindDataHandlers() {
    // TODO
    // bindDataHandler("/foo", FooHandler.class);
  }
}
