package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.WordForm;

public class WordFormWidget extends Composite {

	private static WordFormWidgetUiBinder uiBinder = GWT.create(WordFormWidgetUiBinder.class);

	interface WordFormWidgetUiBinder extends UiBinder<Widget, WordFormWidget> {
	}

	@UiField
	Label word;

	@UiField
	Label pron;

	public WordFormWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public WordFormWidget(WordForm wordform, boolean lemma) {
		this(wordform);
		setLemmaStatus(lemma);
	}
	
	public WordFormWidget(WordForm wordform) {
		this();
		setWord(wordform.getForm());
		setPron(wordform.getPronunciation());
	}

	public void setWord(String word) {
		this.word.setText(word);
	}

	public void setPron(String pron) {
		if (pron == null || pron.isEmpty()) {
			this.pron.setVisible(false);
		} else {
			this.pron.setVisible(true);
			this.pron.setText(pron);
		}
	}
	
	public void setLemmaStatus(boolean lemma){
		if (lemma){			
			word.addStyleDependentName("headword");
		} else {
			word.removeStyleDependentName("headword");
		}
	}

}
