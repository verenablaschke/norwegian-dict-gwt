package de.ws1819.colewe.shared;

import java.util.ArrayList;
import java.util.Objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TranslationalEquivalent implements IsSerializable {

	private ArrayList<String> translation;
	private ArrayList<String> grammar;
	private ArrayList<String> usage;
	private ArrayList<String> abbr;

	public TranslationalEquivalent() {
		// For GWT serialization.
		this(new ArrayList<String>(), null, null, null);
	}

	public TranslationalEquivalent(ArrayList<String> translation, ArrayList<String> usage) {
		this(translation, null, usage, null);
	}

	public TranslationalEquivalent(String translation, ArrayList<String> grammar, ArrayList<String> usage,
			ArrayList<String> abbr) {
		this(new ArrayList<String>(), grammar, usage, abbr);
		addTranslation(translation);
	}

	public TranslationalEquivalent(ArrayList<String> translation, ArrayList<String> grammar, ArrayList<String> usage,
			ArrayList<String> abbr) {
		this.translation = translation;
		setGrammar(grammar);
		setUsage(usage);
		setAbbr(abbr);
	}

	@Override
	public String toString() {
		return getTranslationString() + " {" + getGrammarString() + "} [" + getUsageString() + "] <" + getAbbrString()
				+ ">";
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
	public ArrayList<String> getGrammar() {
		return grammar;
	}

	public String getGrammarString() {
		return String.join(", ", grammar);
	}

	/**
	 * @param grammar
	 *            the grammar to set
	 */
	public void setGrammar(ArrayList<String> grammar) {
		if (grammar == null) {
			this.grammar = new ArrayList<>();
			return;
		}
		grammar.removeIf(Objects::isNull);
		grammar.removeIf(String::isEmpty);
		this.grammar = grammar;
	}

	public void addGrammar(ArrayList<String> otherGrammar) {
		for (String other : otherGrammar) {
			if (!grammar.contains(other)) {
				grammar.add(other);
			}
		}
	}

	/**
	 * @return the usage
	 */
	public ArrayList<String> getUsage() {
		return usage;
	}

	public String getUsageString() {
		return String.join(", ", usage);
	}

	/**
	 * @param usage
	 *            the usage to set
	 */
	public void setUsage(ArrayList<String> usage) {
		if (usage == null) {
			this.usage = new ArrayList<>();
			return;
		}
		usage.removeIf(Objects::isNull);
		usage.removeIf(String::isEmpty);
		this.usage = usage;
	}

	public void addUsage(ArrayList<String> otherUsage) {
		for (String other : otherUsage) {
			if (!usage.contains(other)) {
				usage.add(other);
			}
		}
	}

	/**
	 * @return the abbr
	 */
	public ArrayList<String> getAbbr() {
		return abbr;
	}

	/**
	 * @param abbr
	 *            the abbr to set
	 */
	public void setAbbr(ArrayList<String> abbr) {
		if (abbr == null) {
			this.abbr = new ArrayList<>();
			return;
		}
		abbr.removeIf(Objects::isNull);
		abbr.removeIf(String::isEmpty);
		this.abbr = abbr;
	}

	public void addAbbr(ArrayList<String> otherAbbr) {
		for (String other : otherAbbr) {
			if (!abbr.contains(other)) {
				abbr.add(other);
			}
		}
	}

	public String getAbbrString() {
		return String.join(", ", abbr);
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
		if (abbr == null) {
			if (other.abbr != null) {
				return false;
			}
		} else if (!abbr.equals(other.abbr)) {
			return false;
		}
		if (grammar == null) {
			if (other.grammar != null) {
				return false;
			}
		} else if (!grammar.equals(other.grammar)) {
			return false;
		}
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		if (usage == null) {
			if (other.usage != null) {
				return false;
			}
		} else if (!usage.equals(other.usage)) {
			return false;
		}
		return true;
	}

}
