package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class OutputWidget extends Composite {

	private static OutputWidgetUiBinder uiBinder = GWT.create(OutputWidgetUiBinder.class);

	interface OutputWidgetUiBinder extends UiBinder<Widget, OutputWidget> {
	}

	@UiField
	Button button;

	@UiField
	HTMLPanel table;

	@UiField
	HTML info;

	public OutputWidget(String[] words) {
		initWidget(uiBinder.createAndBindUi(this));
		table.setStyleName("row");
		for (String word : words) {
			table.add(new WordWidget(word));
			// TODO if there's a line with only a couple of words, can we make
			// it look nicer?? margin-right:auto trade-off
		}
		// TODO can I move this into the xml file?
		// TODO add icon?
		button.addStyleName("btn btn-primary");
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		RootPanel.get("infoContainer").clear();
		RootPanel.get("widgetContainer").clear();
		RootPanel.get("widgetContainer").add(new InputWidget());
	}

}
