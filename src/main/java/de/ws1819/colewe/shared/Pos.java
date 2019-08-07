package de.ws1819.colewe.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum Pos implements IsSerializable {

	ADJ, ADV, CONJ, DET, ITJ, NOUN, PREP, PFX, PRON, SFX, VERB, SENT,
	// other: For now, tags that do not match up between the different sources.
	OTHER,
	// null: No tag provided.
	NULL;

	@Override
	public String toString() {
		String s = super.toString().toLowerCase();
		if (s.equals("null")) {
			return "";
		}
		return s;
	}

}
