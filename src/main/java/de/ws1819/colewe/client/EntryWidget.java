package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.WordForm;

public class EntryWidget extends Composite {

	private static EntryWidgetUiBinder uiBinder = GWT.create(EntryWidgetUiBinder.class);

	interface EntryWidgetUiBinder extends UiBinder<Widget, EntryWidget> {
	}

	@UiField
	Label headword;

	@UiField
	Label pron;

	@UiField
	HTML grammarNOBadge;

	@UiField
	HTML usageNOBadge;

	@UiField
	HTML abbrNOBadge;

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
		setHeadword(headword.getForm());
		setTranslation(translation);
		setGrammarNO(grammarNO);
		setUsageNO(usageNO);
		setAbbrNO(abbrNO);
		setGrammarDE(grammarDE);
		setUsageDE(usageDE);
		setAbbrDE(abbrDE);
		setPron(headword.getPronunciation());
	}

	public EntryWidget(WordForm headword, String translation) {
		this(headword, translation, null, null, null, null, null, null);
	}

	public void setHeadword(String headword) {
		this.headword.setText(headword);
	}

	public void setPron(String pron) {
		this.pron.setText(pron);
	}

	public void setTranslation(String translation) {
		this.translation.setText(translation);
	}

	public void setGrammarNO(String text) {
		setBadge(grammarNOBadge, text);
	}

	public void setUsageNO(String text) {
		setBadge(usageNOBadge, text);
	}

	public void setAbbrNO(String text) {
		setBadge(abbrNOBadge, text);
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
