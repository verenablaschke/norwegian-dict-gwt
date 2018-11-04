package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class WordWidget extends Composite implements HasText {

	private static WordWidgetUiBinder uiBinder = GWT.create(WordWidgetUiBinder.class);

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
		return div.getHTML();
	}
	
	@UiHandler("div")
	void onClick(ClickEvent e){
		RootPanel.get("infoContainer").clear();
		RootPanel.get("infoContainer").add(new Label(getText()));
	}

}
