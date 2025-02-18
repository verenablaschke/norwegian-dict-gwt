package de.ws1819.colewe.client;

import java.util.ArrayList;
import java.util.logging.Logger;

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
import de.ws1819.colewe.shared.Language;

/**
 * Displays a word. Clicking on this widget triggers a dictionary search. See
 * section 3.1.
 * 
 * @author Verena Blaschke
 */
public class WordWidget extends Composite implements HasText {

	private static final Logger logger = Logger.getLogger(WordWidget.class.getSimpleName());

	private static WordWidgetUiBinder uiBinder = GWT.create(WordWidgetUiBinder.class);
	private static DictionaryServiceAsync dictionaryService = DictionaryService.App.getInstance();
	private boolean ctrl = false;
	private static final String HIGHLIGHT = "bg-info";
	// Need to be instance variables so we can refer to it in the
	// AsyncCallback:
	private String fullQuery;
	private Language lang;

	interface WordWidgetUiBinder extends UiBinder<Widget, WordWidget> {
	}

	public WordWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HTML div;

	public WordWidget(String text, Language lang) {
		initWidget(uiBinder.createAndBindUi(this));
		div.addStyleName("col-xs");
		setText(text);
		this.lang = lang;
	}

	public void setText(String text) {
		div.setHTML(SafeHtmlUtils.htmlEscape(text));
	}

	public String getText() {
		return div.getHTML().replaceAll("[®&:§–@\"\\(\\)\\[\\]\\{\\}\\!\\?\\.,%]+", "").trim();
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
	// See section 3.4.
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
		} catch (Exception exc) {
			// No query yet.
			RootPanel.get("queryContainer").add(query);
		}

		fullQuery = getText();
		if (ctrl) {
			fullQuery = (query.getText() + " " + fullQuery).trim();
		}
		fullQuery = fullQuery.toLowerCase();
		query.setText(fullQuery);
		logger.info("QUERY: " + fullQuery);

		dictionaryService.query(fullQuery, new AsyncCallback<ArrayList<Entry>>() {
			@Override
			public void onSuccess(ArrayList<Entry> results) {
				if (results == null || results.isEmpty()) {
					RootPanel.get("infoContainer").add(new Label("No results found for \"" + fullQuery + "\"."));
				}
				Label history = new Label();
				try {
					// The history container is explained in section 3.6.
					history = (Label) RootPanel.get("historyContainer").getWidget(0);
				} catch (Exception exc) {
					// No query yet.
					RootPanel.get("historyContainer").add(history);
				}
				history.setText(history.getText() + "&" + fullQuery);
				for (Entry entry : results) {
					logger.info("-- " + entry.toString());
					try {
						RootPanel.get("infoContainer").add(new EntryWidget(entry, lang));
					} catch (Exception exc) {
						logger.warning(exc.getMessage());
						logger.warning(exc.getStackTrace().toString());
						RootPanel.get("infoContainer").clear();
						RootPanel.get("infoContainer")
								.add(new Label("Something went wrong. Please try another query or try again later."));
						RootPanel.get("infoContainer").add(new Label(exc.getMessage()));
					}
				}
				timer.schedule(10);

			}

			@Override
			public void onFailure(Throwable caught) {
				logger.warning(caught.getMessage());
				logger.warning(caught.getStackTrace().toString());
				RootPanel.get("infoContainer").clear();
				RootPanel.get("infoContainer")
						.add(new Label("Something went wrong. Please try another query or try again later."));
				RootPanel.get("infoContainer")
						.add(new Label("RPC error: " + caught.getMessage() + caught.getStackTrace()));
			}
		});
	}

}
