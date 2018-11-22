package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class OutputWidget extends Composite {

	private static OutputWidgetUiBinder uiBinder = GWT.create(OutputWidgetUiBinder.class);
	private boolean ctrl = false;

	interface OutputWidgetUiBinder extends UiBinder<Widget, OutputWidget> {
	}

	@UiField
	Button button;

	@UiField
	FocusPanel table;

	@UiField
	FlowPanel flowPanel;

	@UiField
	HTML info;

	public OutputWidget(String[] words) {
		initWidget(uiBinder.createAndBindUi(this));
		flowPanel.setStyleName("row");
		for (String word : words) {
			flowPanel.add(new WordWidget(word));
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

}
