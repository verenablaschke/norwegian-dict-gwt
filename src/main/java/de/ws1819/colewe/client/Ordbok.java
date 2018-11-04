package de.ws1819.colewe.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ordbok implements EntryPoint {

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);


  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
		RootPanel.get("widgetContainer").add(new InputWidget());
  }
}
