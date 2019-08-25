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

	// Copied from https://no.wikipedia.org/wiki/Ordbok, Aug 25th, 2019.
	private static String exampleText = "En ordbok er en samling av ord fra ett eller flere språk. "
			+ "Den angir hvordan ordene staves, og gir gjerne definisjoner, bøyninger, uttalehjelp, "
			+ "eksempler på bruk og ordenes etymologi. Hvis det er en ordbok mellom ulike språk "
			+ "(flerspråklig ordbok), gir ordboken tilsvarende ord eller et forklarende uttrykk på "
			+ "det andre språket. Ordene i ordbøker er vanligvis ordnet alfabetisk.";

	@UiField
	Button send;

	@UiField
	Button example;

	@UiField
	TextArea textArea;

	public InputWidget() {
		this(null);
	}

	public InputWidget(String text) {
		initWidget(uiBinder.createAndBindUi(this));
		textArea.getElement().setPropertyString("placeholder", "Skriv inn teksten din.");
		send.setEnabled(false);
	}

	@UiHandler("send")
	void onSendClick(ClickEvent e) {
		((HeaderWidget) RootPanel.get("headerContainer").getWidget(0)).setHeader(true);
		String text = textArea.getText().trim();
		RootPanel.get("widgetContainer").clear();
		RootPanel.get("widgetContainer").add(new OutputWidget(text));
	}
	
	@UiHandler("example")
	void onExampleClick(ClickEvent e) {
		textArea.setText(exampleText);
		send.setEnabled(true);
	}

	@UiHandler("textArea")
	void onKeyPress(KeyUpEvent e) {
		// Only enable clicking on the button if there's an actual query.
		if (textArea.getText().trim().length() > 0) {
			send.setEnabled(true);
		} else {
			send.setEnabled(false);
		}
	}

}
