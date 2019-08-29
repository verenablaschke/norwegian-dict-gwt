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

	public EntryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public EntryWidget(Entry entry) {
		this();
		entryPanel.add(new SimpleEntryWidget(entry));

		if (entry.hasColloctations()) {
			String id = "collapse-" + entry.htmlAnchor();
			collocButton.getElement().setAttribute("data-toggle", "collapse");
			collocButton.getElement().setAttribute("data-target", "#" + id);
			collocInnerPanel.getElement().setAttribute("id", id);
			
			for (Entry colloc : entry.getCollocations()) {
				collocInnerPanel.add(new SimpleEntryWidget(colloc));
			}
		} else {
			collocOuterPanel.setVisible(false);
		}
	}

}
