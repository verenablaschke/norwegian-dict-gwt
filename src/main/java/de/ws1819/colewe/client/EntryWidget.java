package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.TranslationalEquivalent;
import de.ws1819.colewe.shared.WordForm;

public class EntryWidget extends Composite {

	private static EntryWidgetUiBinder uiBinder = GWT.create(EntryWidgetUiBinder.class);

	interface EntryWidgetUiBinder extends UiBinder<Widget, EntryWidget> {
	}

	@UiField
	HorizontalPanel wordPanel;

	@UiField
	VerticalPanel translationPanel;

	public EntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntryWidget(Entry entry) {
		this();
		setWord(entry.getLemma(), true);
		String pos = entry.getPos().toString();
		wordPanel.add(new BadgeWidget((pos.isEmpty() ? "" : pos + " ") + entry.getGrammarNO(), entry.getUsageNO(),
				entry.getAbbrNO()));
		for (WordForm wf : entry.getInflections().values()) {
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
