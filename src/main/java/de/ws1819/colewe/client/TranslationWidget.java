package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.TranslationalEquivalent;

public class TranslationWidget extends Composite {

	
	private static TranslationWidgetUiBinder uiBinder = GWT.create(TranslationWidgetUiBinder.class);

	interface TranslationWidgetUiBinder extends UiBinder<Widget, TranslationWidget> {
	}

	@UiField
	Label translation;

	@UiField
	HTML grammarBadge;

	@UiField
	HTML usageBadge;

	@UiField
	HTML abbrBadge;

	public TranslationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TranslationWidget(TranslationalEquivalent transl) {
		this();
		setTranslation(transl.getTranslationString());
		setGrammar(transl.getGrammar());
		setUsage(transl.getUsage());
		setAbbr(transl.getAbbr());
	}

	public void setTranslation(String translation) {
		this.translation.setText(translation);
	}

	public void setGrammar(String text) {
		setBadge(grammarBadge, text);
	}

	public void setUsage(String text) {
		setBadge(usageBadge, text);
	}

	public void setAbbr(String text) {
		setBadge(abbrBadge, text);
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
