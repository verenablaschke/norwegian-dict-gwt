package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EntryWidget extends Composite {

	private static EntryWidgetUiBinder uiBinder = GWT.create(EntryWidgetUiBinder.class);

	interface EntryWidgetUiBinder extends UiBinder<Widget, EntryWidget> {
	}

	@UiField
	Label headword;

	@UiField
	HTML grammarBadge;

	@UiField
	HTML usageBadge;

	@UiField
	HTML abbrBadge;

	@UiField
	Label translation;

	public EntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntryWidget(String headword, String translation, String grammar, String usage, String abbr) {
		this();
		setHeadword(headword);
		setTranslation(translation);
		grammarBadge.setStyleName("badge badge-info");
		setGrammar(grammar);
		usageBadge.setStyleName("badge badge-warning");
		setUsage(usage);
		abbrBadge.setStyleName("badge badge-secondary");
		setAbbr(abbr);
	}

	public EntryWidget(String headword, String translation) {
		this(headword, translation, null, null, null);
	}

	public void setHeadword(String headword) {
		this.headword.setText(headword);
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
