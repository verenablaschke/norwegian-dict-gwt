package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Contains grammar and/or usage and/or abbreviation information on a lemma.
 * 
 * @author Verena Blaschke
 */
public class BadgeWidget extends Composite {

	private static BadgeWidgetUiBinder uiBinder = GWT.create(BadgeWidgetUiBinder.class);

	interface BadgeWidgetUiBinder extends UiBinder<Widget, BadgeWidget> {
	}

	@UiField
	HTML grammarBadge;

	@UiField
	HTML usageBadge;

	@UiField
	HTML abbrBadge;

	public BadgeWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public BadgeWidget(String grammar, String usage, String abbr) {
		initWidget(uiBinder.createAndBindUi(this));
		setGrammar(grammar);
		setUsage(usage);
		setAbbr(abbr);
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
