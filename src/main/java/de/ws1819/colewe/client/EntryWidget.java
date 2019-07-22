package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.WordForm;

public class EntryWidget extends Composite {

	private static EntryWidgetUiBinder uiBinder = GWT.create(EntryWidgetUiBinder.class);

	interface EntryWidgetUiBinder extends UiBinder<Widget, EntryWidget> {
	}

	@UiField
	HorizontalPanel wordPanel;

	@UiField
	Label translation;

	@UiField
	HTML grammarDEBadge;

	@UiField
	HTML usageDEBadge;

	@UiField
	HTML abbrDEBadge;

	public EntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntryWidget(WordForm headword, String translation, String grammarNO, String usageNO, String abbrNO,
			String grammarDE, String usageDE, String abbrDE) {
		this();
		setWord(headword);
		setTranslation(translation);
		setBadgesNO(grammarNO, usageNO, abbrNO);
		setGrammarDE(grammarDE);
		setUsageDE(usageDE);
		setAbbrDE(abbrDE);
	}

	public EntryWidget(WordForm headword, String translation) {
		this(headword, translation, null, null, null, null, null, null);
	}

	public void setWord(WordForm wordform) {
		wordPanel.add(new WordFormWidget(wordform, true));
	}

	public void setTranslation(String translation) {
		this.translation.setText(translation);
	}

	public void setBadgesNO(String grammarNO, String usageNO, String abbrNO) {
		wordPanel.add(new BadgeWidget(grammarNO, usageNO, abbrNO));
	}

	public void setGrammarDE(String text) {
		setBadge(grammarDEBadge, text);
	}

	public void setUsageDE(String text) {
		setBadge(usageDEBadge, text);
	}

	public void setAbbrDE(String text) {
		setBadge(abbrDEBadge, text);
	}

	private void setBadge(HTML badge, String text) {
		if (text == null || text.isEmpty()) {
			badge.setVisible(false);
		} else {
			badge.setVisible(true);
			badge.setText(text);
		}
	}

}
