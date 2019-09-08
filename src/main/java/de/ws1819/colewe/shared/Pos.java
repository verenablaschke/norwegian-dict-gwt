package de.ws1819.colewe.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Part-of-speech tags. See section 2.6.1 of the report.
 * 
 * @author Verena Blaschke
 */
public enum Pos implements IsSerializable {

	ADJ, ADV, CONJ, DET, ITJ, NOUN, PREP, PFX, PRON, SENT, SFX, VERB,
	// Null: No tag provided. / Unusual tag that does not fit the tagging
	// system.
	NULL;

	@Override
	public String toString() {
		String s = super.toString().toLowerCase();
		if (s.equals("null")) {
			return "";
		}
		return s;
	}

	public String toString(Language lang) {
		switch (lang) {
		case NO:
			return toStringNO();
		case DE:
			return toStringDE();
		case EN:
		default:
			return toString();
		}
	}

	private String toStringNO() {
		switch (this) {
		case ADJ:
		case ADV:
		case DET:
		case ITJ:
		case PREP:
		case PRON:
		case VERB:
			return toString();
		case CONJ:
			return "konj";
		case NOUN:
			return "subst";
		case PFX:
			return "prefiks";
		case SENT:
			return "setn";
		case SFX:
			return "suffiks";
		case NULL:
		default:
			return "";
		}
	}

	private String toStringDE() {
		switch (this) {
		case ADJ:
			return "Adj";
		case ADV:
			return "Adv";
		case CONJ:
			return "Konj";
		case DET:
			return "Det";
		case ITJ:
			return "Interj";
		case NOUN:
			return "Subst";
		case PREP:
			return "Präpo";
		case PFX:
			return "Präfix";
		case PRON:
			return "Pron";
		case SENT:
			return "Satz";
		case SFX:
			return "Suffix";
		case VERB:
			return "Verb";
		case NULL:
		default:
			return "";
		}
	}

}
