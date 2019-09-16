package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Language;
import de.ws1819.colewe.shared.TranslationalEquivalent;

/**
 * Contains a translational equivalent and optionally domain/usage/abbreviation
 * information. See section 3.1.
 * 
 * @author Verena Blaschke
 */
public class TranslationWidget extends Composite {

	private static TranslationWidgetUiBinder uiBinder = GWT.create(TranslationWidgetUiBinder.class);

	interface TranslationWidgetUiBinder extends UiBinder<Widget, TranslationWidget> {
	}

	@UiField
	Label translation;

	@UiField
	HTML usageBadge;

	@UiField
	HTML abbrBadge;

	@UiField
	HTML autoBadge;

	public TranslationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TranslationWidget(TranslationalEquivalent transl, Language lang) {
		this();
		setTranslation(transl.getTranslationString());
		setUsage(transl.getUsageString());
		setAbbr(transl.getAbbrString());
		setAuto(transl.isAutomaticallyInferred(), lang);
	}

	public TranslationWidget(String transl) {
		this();
		setTranslation(transl);
	}

	public void setTranslation(String translation) {
		this.translation.setText(translation);
	}

	public void setUsage(String text) {
		setBadge(usageBadge, text);
	}

	public void setAbbr(String text) {
		setBadge(abbrBadge, text);
	}

	public void setAuto(boolean isAutomaticallyInferred, Language lang) {
		String text = null;
		if (isAutomaticallyInferred) {
			switch (lang) {
			case DE:
				text = "maschinenübersetzt";
				break;
			case EN:
				text = "machine-translated";
				break;
			case NO:
			default:
				text = "maskinoversatt";
			}
		}
		setBadge(autoBadge, text);
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
