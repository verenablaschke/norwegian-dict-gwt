package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Language;
import de.ws1819.colewe.shared.SampleSentence;
import de.ws1819.colewe.shared.TranslationalEquivalent;
import de.ws1819.colewe.shared.WordForm;

public class SimpleEntryWidget extends Composite {

	private static SimpleEntryWidgetUiBinder uiBinder = GWT.create(SimpleEntryWidgetUiBinder.class);

	interface SimpleEntryWidgetUiBinder extends UiBinder<Widget, SimpleEntryWidget> {
	}

	@UiField
	HorizontalPanel wordPanel;

	@UiField
	VerticalPanel translationPanel;

	public SimpleEntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public SimpleEntryWidget(SampleSentence sample, Language lang) {
		this();
		wordPanel.add(new WordFormWidget(sample.getNo(), true));
		translationPanel.add(new TranslationWidget(sample.getDe()));
	}

	public SimpleEntryWidget(Entry entry, Language lang) {
		this();
		setWord(entry.getLemma(), true);
		wordPanel.add(new BadgeWidget(entry.getGrammarString(lang), entry.getUsageString(), entry.getAbbrString()));
		for (WordForm wf : entry.getDisplayableInflections()) {
			setWord(wf, false);
		}
		for (TranslationalEquivalent transl : entry.getTranslations()) {
			translationPanel.add(new TranslationWidget(transl));
		}
	}

	private void setWord(WordForm wordform, boolean isLemma) {
		wordPanel.add(new WordFormWidget(wordform, isLemma));
	}

}
