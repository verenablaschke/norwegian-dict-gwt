package de.ws1819.colewe.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum Pos implements IsSerializable {

	ADJ, ADV, CONJ, DET, ITJ, NOUN, PREP, PFX, PRON, SENT, SFX, VERB,
	// null: No tag provided. / Unusual tag that does not fit the tagging system.
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
