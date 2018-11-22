package de.ws1819.colewe.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Entry;

public class WordWidget extends Composite implements HasText {

	private static WordWidgetUiBinder uiBinder = GWT.create(WordWidgetUiBinder.class);
	private static DictionaryServiceAsync dictionaryService = DictionaryService.App.getInstance();
	private boolean ctrl = false;
	private static final String HIGHLIGHT = "bg-info";

	interface WordWidgetUiBinder extends UiBinder<Widget, WordWidget> {
	}

	public WordWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTML div;

	public WordWidget(String text) {
		initWidget(uiBinder.createAndBindUi(this));
		div.addStyleName("col-xs");
		setText(text);
	}

	public void setText(String text) {
		div.setHTML(SafeHtmlUtils.htmlEscape(text));
	}

	public String getText() {
		// TODO improve this!!
		return div.getHTML().trim().replaceAll("[\\.,;\"]$", "");
	}

	public void setInactive() {
		div.removeStyleName(HIGHLIGHT);
	}

	private Timer timer = new Timer() {
		public void run() {
			div.addStyleName(HIGHLIGHT);
		}
	};

	// Handle CTRL-Click events. Has to be added before the click event handler.
	@UiHandler("div")
	void onMouseDown(MouseDownEvent e) {
		ctrl = e.isControlKeyDown();
	}

	@UiHandler("div")
	void onClick(ClickEvent e) {
		RootPanel.get("infoContainer").clear();
		if (!ctrl) {
			RootPanel.get("queryContainer").clear();
		}

		Label query = new Label();
		try {
			query = (Label) RootPanel.get("queryContainer").getWidget(0);
		} catch (Exception exc) { // TODO more specific
			// No query yet.
			RootPanel.get("queryContainer").add(query);
		}

		String fullQuery = getText();
		if (ctrl) {
			fullQuery = (query.getText() + " " + fullQuery).trim();
		}
		query.setText(fullQuery);

		dictionaryService.query(fullQuery, new AsyncCallback<ArrayList<Entry>>() {
			@Override
			public void onSuccess(ArrayList<Entry> results) {
				if (results == null || results.isEmpty()) {
					RootPanel.get("infoContainer").add(new Label("No results found."));
				}
				for (Entry entry : results) {
					// TODO extra info (comments) via badge?
					// RootPanel.get("infoContainer").add(new
					// Label(entry.toString()));
					try {
						RootPanel.get("infoContainer")
								.add(new EntryWidget(entry.getLemma(), entry.getTranslation(), ":-)", "test"));
					} catch (Exception exc) {
						// TODO del
						RootPanel.get("infoContainer").add(new Label(exc.getMessage()));
					}
				}
				timer.schedule(10);

			}

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("infoContainer").add(new Label("RPC error: " + caught.getMessage()));
			}
		});
	}

}
