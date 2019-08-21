package de.ws1819.colewe.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TranslationalEquivalent implements IsSerializable {

	private ArrayList<String> translation;
	private String grammar;
	private String usage;
	private String abbr;

	public TranslationalEquivalent() {
		// For GWT serialization.
		this(new ArrayList<String>(), null, null, null);
	}

	public TranslationalEquivalent(String translation) {
		this(translation, null, null, null);
	}

	public TranslationalEquivalent(ArrayList<String> translation) {
		this(translation, null, null, null);
	}

	public TranslationalEquivalent(ArrayList<String> translation, String usage) {
		this(translation, null, usage, null);
	}

	public TranslationalEquivalent(String translation, String grammar, String usage, String abbr) {
		this(new ArrayList<String>(), null, null, null);
		addTranslation(translation);
	}

	public TranslationalEquivalent(ArrayList<String> translation, String grammar, String usage, String abbr) {
		this.translation = translation;
		this.grammar = grammar;
		this.usage = usage;
		this.abbr = abbr;
	}

	@Override
	public String toString() {
		return getTranslationString() + " {" + grammar + "} [" + usage + "] <" + abbr + ">";
	}

	public void addTranslation(String transl) {
		if (translation == null) {
			translation = new ArrayList<String>();
		}
		translation.add(transl);
	}

	public String getTranslationString() {
		String s = translation.toString();
		return s.substring(1, s.length() - 1);
	}

	/**
	 * @return the translation
	 */
	public ArrayList<String> getTranslation() {
		return translation;
	}

	/**
	 * @param translation
	 *            the translation to set
	 */
	public void setTranslation(ArrayList<String> translation) {
		this.translation = translation;
	}

	/**
	 * @return the grammar
	 */
	public String getGrammar() {
		return grammar;
	}

	/**
	 * @param grammar
	 *            the grammar to set
	 */
	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}

	/**
	 * @return the usage
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * @param usage
	 *            the usage to set
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}

	/**
	 * @return the abbr
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * @param abbr
	 *            the abbr to set
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abbr == null) ? 0 : abbr.hashCode());
		result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TranslationalEquivalent)) {
			return false;
		}
		TranslationalEquivalent other = (TranslationalEquivalent) obj;
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		if (!equalsOrBothEmpty(abbr, other.abbr)) {
			return false;
		}
		if (!equalsOrBothEmpty(grammar, other.grammar)) {
			return false;
		}

		if (!equalsOrBothEmpty(usage, other.usage)) {
			return false;
		}
		return true;
	}

	private static boolean equalsOrBothEmpty(String s, String t) {
		if (s == null) {
			return t == null || t.isEmpty();
		}
		if (t == null) {
			return s.isEmpty();
		}
		return s.equals(t);
	}

}
