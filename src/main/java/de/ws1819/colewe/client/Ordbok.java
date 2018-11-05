package de.ws1819.colewe.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Ordbok implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
		RootPanel.get("widgetContainer").add(new InputWidget());
  }
}
