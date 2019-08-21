package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HeaderWidget extends Composite {

	private static HeaderWidgetUiBinder uiBinder = GWT.create(HeaderWidgetUiBinder.class);
	
	interface HeaderWidgetUiBinder extends UiBinder<Widget, HeaderWidget> {
	}

	@UiField
	HTML header;
	
	public HeaderWidget() {
		this(false);
	}

	public HeaderWidget(boolean open) {
		initWidget(uiBinder.createAndBindUi(this));
		setHeader(open);
	}
	
	public void setHeader(boolean open){
		if (open){
			header.setHTML("Norsk Ordbok <i class=\"fa fa-book-open\"></i>");
		} else {
			header.setHTML("Norsk Ordbok <i class=\"fa fa-book\"></i>");
		}
	}
	
}
