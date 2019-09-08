package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Language;

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
	RadioButton radioNO;

	@UiField
	RadioButton radioDE;

	@UiField
	RadioButton radioEN;

	@UiField
	Button send;

	@UiField
	Button example;

	@UiField
	TextArea textArea;

	private Language lang;

	public InputWidget() {
		this(null);
	}

	public InputWidget(String text) {
		this(text, Language.NO);
	}

	public InputWidget(String text, Language lang) {
		initWidget(uiBinder.createAndBindUi(this));
		send.setEnabled(false);
		switch (lang) {
		case DE:
			radioDE.setValue(true);
			break;
		case EN:
			radioEN.setValue(true);
			break;
		case NO:
		default:
			radioNO.setValue(true);
		}
		radioNO.setValue(true);
		setLang(lang);
	}

	@UiHandler("send")
	void onSendClick(ClickEvent e) {
		((HeaderWidget) RootPanel.get("headerContainer").getWidget(0)).setHeader(true);
		String text = textArea.getText().trim();
		RootPanel.get("widgetContainer").clear();
		RootPanel.get("widgetContainer").add(new OutputWidget(text, lang));
	}

	@UiHandler("example")
	void onExampleClick(ClickEvent e) {
		textArea.setText(exampleText);
		send.setEnabled(true);
	}

	@UiHandler("radioNO")
	void onRadioNOClick(ClickEvent e) {
		setLang(Language.NO);
	}

	@UiHandler("radioDE")
	void onRadioDEClick(ClickEvent e) {
		setLang(Language.DE);
	}

	@UiHandler("radioEN")
	void onRadioENClick(ClickEvent e) {
		setLang(Language.EN);
	}

	private void setLang(Language lang) {
		this.lang = lang;
		String placeholder, exampleString;
		String sendString = "<i class=\"fa fa-arrow-circle-right\"></i> ";
		switch (lang) {
		case DE:
			placeholder = "Hier könnte Ihr Text stehen.";
			sendString += "Abschicken";
			exampleString = "Beispieltext";
			break;
		case EN:
			placeholder = "Enter your text here.";
			sendString += "Send";
			exampleString = "Example text";
			break;
		case NO:
		default:
			placeholder = "Skriv inn teksten din.";
			sendString += "Send";
			exampleString = "Eksempeltekst";

		}
		if (textArea.getText().trim().isEmpty()) {
			textArea.getElement().setPropertyString("placeholder", placeholder);
		}
		send.setHTML(sendString);
		example.setText(exampleString);
	}

	@UiHandler("textArea")
	void onKeyPress(KeyUpEvent e) {
		// Only enable clicking on the againButton if there's an actual query.
		if (textArea.getText().trim().length() > 0) {
			send.setEnabled(true);
		} else {
			send.setEnabled(false);
		}
	}

}
