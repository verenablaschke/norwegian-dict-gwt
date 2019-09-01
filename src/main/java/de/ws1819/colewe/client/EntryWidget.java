package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Language;
import de.ws1819.colewe.shared.SampleSentence;

public class EntryWidget extends Composite {

	private static EntryWidgetUiBinder uiBinder = GWT.create(EntryWidgetUiBinder.class);

	interface EntryWidgetUiBinder extends UiBinder<Widget, EntryWidget> {
	}

	@UiField
	HTMLPanel entryPanel;

	@UiField
	HTMLPanel collocOuterPanel;

	@UiField
	Button collocButton;

	@UiField
	VerticalPanel collocInnerPanel;

	@UiField
	HTMLPanel sampleOuterPanel;

	@UiField
	Button sampleButton;

	@UiField
	VerticalPanel sampleInnerPanel;

	public EntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntryWidget(Entry entry, Language lang) {
		this();
		entryPanel.add(new SimpleEntryWidget(entry, lang));

		if (entry.hasColloctations()) {
			String id = "collapse-colloc-" + entry.htmlAnchor();
			collocButton.getElement().setAttribute("data-toggle", "collapse");
			collocButton.getElement().setAttribute("data-target", "#" + id);
			switch (lang) {
			case DE:
				collocButton.setText("Kollokationen");
				break;
			case EN:
				collocButton.setText("Collocations");
				break;
			case NO:
			default:
				collocButton.setText("Kollokasjoner");
				break;
			}
			collocInnerPanel.getElement().setAttribute("id", id);

			for (Entry colloc : entry.getCollocations()) {
				collocInnerPanel.add(new SimpleEntryWidget(colloc, lang));
			}
		} else {
			collocOuterPanel.setVisible(false);
		}

		if (entry.hasSampleSentences()) {
			String id = "collapse-sample-" + entry.htmlAnchor();
			sampleButton.getElement().setAttribute("data-toggle", "collapse");
			sampleButton.getElement().setAttribute("data-target", "#" + id);
			switch (lang) {
			case DE:
				sampleButton.setText("Beispielsätze");
				break;
			case EN:
				sampleButton.setText("Sample sentences");
				break;
			case NO:
			default:
				sampleButton.setText("Eksempelsetninger");
				break;
			}
			sampleInnerPanel.getElement().setAttribute("id", id);

			for (SampleSentence sample : entry.getSampleSentences()) {
				sampleInnerPanel.add(new SimpleEntryWidget(sample, lang));
			}
		} else {
			sampleOuterPanel.setVisible(false);
		}

	}

}
