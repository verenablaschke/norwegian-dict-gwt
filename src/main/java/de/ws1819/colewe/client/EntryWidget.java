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

	public EntryWidget(String headword, String translation, String grammarNO, String usageNO, String abbrNO,
			String grammarDE, String usageDE, String abbrDE) {
		this();
		setHeadword(headword);
		setTranslation(translation);
		grammarNOBadge.setStyleName("badge badge-info");
		setGrammarNO(grammarNO);
		usageNOBadge.setStyleName("badge badge-warning");
		setUsageNO(usageNO);
		abbrNOBadge.setStyleName("badge badge-secondary");
		setAbbrNO(abbrNO);
		grammarDEBadge.setStyleName("badge badge-light");
		setGrammarDE(grammarDE);
		usageDEBadge.setStyleName("badge badge-light");
		setUsageDE(usageDE);
		abbrDEBadge.setStyleName("badge badge-light");
		setAbbrDE(abbrDE);
	}

	public EntryWidget(String headword, String translation) {
		this(headword, translation, null, null, null, null, null, null);
	}

	public void setHeadword(String headword) {
		this.headword.setText(headword);
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
