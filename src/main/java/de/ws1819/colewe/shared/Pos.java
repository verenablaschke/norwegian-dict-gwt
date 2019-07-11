package de.ws1819.colewe.shared;

import java.io.Serializable;

public enum Pos implements Serializable {

	ADJ, ADV, CONJ, NOUN, PREP, PRON, VERB,
	// other: For now, tags that do not match up between the different sources.
	// TODO
	OTHER;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}
