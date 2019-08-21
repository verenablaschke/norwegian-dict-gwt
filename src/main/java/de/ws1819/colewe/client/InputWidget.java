package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class InputWidget extends Composite {

	private static InputWidgetUiBinder uiBinder = GWT.create(InputWidgetUiBinder.class);

	interface InputWidgetUiBinder extends UiBinder<Widget, InputWidget> {
	}

	@UiField
	Button button;

	@UiField
	TextArea textArea;

	public InputWidget() {
		this(null);
	}

	public InputWidget(String text) {
		initWidget(uiBinder.createAndBindUi(this));
		textArea.getElement().setPropertyString("placeholder", "Skriv inn teksten din.");
		button.setEnabled(false);
//		if (text == null || text.isEmpty()) {
//			textArea.getElement().setPropertyString("placeholder", "Skriv inn teksten din.");
//			button.setEnabled(false);
//		} else {
//			textArea.setText(text);
//			button.setEnabled(true);
//			// Doesn't work :( TODO
//			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
//				@Override
//				public void execute() {
//					textArea.selectAll();
//				}
//			});
//		}
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		String text = textArea.getText().trim();
		RootPanel.get("widgetContainer").clear();
		RootPanel.get("widgetContainer").add(new OutputWidget(text));
	}

	@UiHandler("textArea")
	void onKeyPress(KeyUpEvent e) {
		// Only enable clicking on the button if there's an actual query.
		if (textArea.getText().trim().length() > 0) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}
	}

}
