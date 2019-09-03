package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Language;

public class OutputWidget extends Composite {

	private static OutputWidgetUiBinder uiBinder = GWT.create(OutputWidgetUiBinder.class);
	private boolean ctrl = false;

	interface OutputWidgetUiBinder extends UiBinder<Widget, OutputWidget> {
	}

	@UiField
	Button againButton;

	@UiField
	Button downloadButton;

	@UiField
	FocusPanel table;

	@UiField
	FlowPanel flowPanel;

	@UiField
	HTML info;

	private String content;
	private Language lang;

	public OutputWidget(String content, Language lang) {
		this.content = content;
		initWidget(uiBinder.createAndBindUi(this));
		for (String word : content.split("\\s+")) {
			flowPanel.add(new WordWidget(word, lang));
		}
		this.lang = lang;
		
		String againButtonText = "<i class=\"fa fa-undo\"></i> ";
		switch (lang) {
		case DE:
			againButtonText += "Neu";
			break;
		case EN:
			againButtonText += "Again";
			break;
		case NO:
		default:
			againButtonText += "Igjen";
		}
		againButton.setHTML(againButtonText);
		
		String downloadButtonText = "<i class=\"fas fa-download\"></i> ";
		switch (lang) {
		case DE:
			downloadButtonText += "Bisherige Suchergebnisse herunterladen";
			break;
		case EN:
			downloadButtonText += "Download query results up to now";
			break;
		case NO:
		default:
			downloadButtonText += "Last ned søkresultater hittil"; // TODO
		}
		downloadButton.setHTML(downloadButtonText);
	}

	@UiHandler("againButton")
	void onAgainClick(ClickEvent e) {
		RootPanel.get("infoContainer").clear();
		((HeaderWidget) RootPanel.get("headerContainer").getWidget(0)).setHeader(false);
		RootPanel.get("widgetContainer").clear();
		RootPanel.get("widgetContainer").add(new InputWidget(content, lang));
	}

	// Handle CTRL-Click events. Has to be added before the click event handler.
	@UiHandler("table")
	void onMouseDown(MouseDownEvent e) {
		ctrl = e.isControlKeyDown();
	}

	@UiHandler("table")
	void onWordClick(ClickEvent e) {
		if (!ctrl) {
			for (int i = 0; i < flowPanel.getWidgetCount(); i++) {
				((WordWidget) flowPanel.getWidget(i)).setInactive();
			}
		}
	}

	@UiHandler("downloadButton")
	void onDownloadClick(ClickEvent e) {
		String query = "";
		try {
			query = ((Label) RootPanel.get("historyContainer").getWidget(0)).getText();
		} catch (Exception exc) {
			// No query yet.
		}
		if (query.startsWith("&")) {
			query = query.substring(1);
		}
		Window.Location.replace("Ordbok/downloadService?query=" + URL.encodeQueryString(query));
	}

}
