package de.ws1819.colewe.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1614919089029847522L;
	private String lemma;
	private Pos pos;
	// TODO? add inflection info (map)
	private HashMap<String, String> inflections;
	private String translation;
	private String grammar;
	private String usage;
	private String abbr;

	public Entry() {
		this(null, null, null, null, null, null, null);
	}

	public Entry(String lemma, Pos pos, String translation, String grammar, String usage, String abbr) {
		this(lemma, pos, null, translation, grammar, usage, abbr);
	}

	public Entry(String lemma, Pos pos, Map<String, String> inflections) {
		this(lemma, pos, inflections, null, null, null, null);
	}

	public Entry(String lemma, Pos pos, String infl, String inflForm) {
		this(lemma, pos, null, null, null, null, null);
		addInflection(infl, inflForm);
	}

	public Entry(String lemma, Pos pos, Map<String, String> inflections, String translation, String grammar,
			String usage, String abbr) {
		this.lemma = lemma;
		setPos(pos);
		setInflections(inflections);
		this.translation = translation;
		setGrammar(grammar);
		setUsage(usage);
		setAbbr(abbr);
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @param lemma
	 *            the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	/**
	 * @return the pos
	 */
	public Pos getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(Pos pos) {
		if (pos == null) {
			this.pos = Pos.NULL;
		} else {
			this.pos = pos;
		}
	}

	/**
	 * @return the inflections
	 */
	public Map<String, String> getInflections() {
		return inflections;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(Map<String, String> inflections) {
		this.inflections = (inflections == null ? new HashMap<String, String>()
				: new HashMap<String, String>(inflections));
	}

	public void addInflection(String infl, String form) {
		inflections.put(infl, form);
	}

	/**
	 * @return the translation
	 */
	public String getTranslation() {
		return translation;
	}

	/**
	 * @param translation
	 *            the translation to set
	 */
	public void setTranslation(String translation) {
		this.translation = translation;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(HashMap<String, String> inflections) {
		this.inflections = inflections;
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
		if (grammar == null) {
			this.grammar = "";
		} else {
			this.grammar = grammar;
		}
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
		if (usage == null) {
			this.usage = "";
		} else {
			this.usage = usage;
		}
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
		if (abbr == null) {
			this.abbr = "";
		} else {
			this.abbr = abbr;
		}
	}

	public String toString() {
		return lemma + ": " + translation + " (" + pos + ", {" + grammar + "} [" + usage + "] <" + abbr
				+ ">, inflections: " + inflections + ")";
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
		result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((abbr == null) ? 0 : abbr.hashCode());
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
		if (!(obj instanceof Entry)) {
			return false;
		}
		Entry other = (Entry) obj;
		if (grammar == null) {
			if (other.grammar != null) {
				return false;
			}
		} else if (!grammar.equals(other.grammar)) {
			return false;
		}
		if (inflections == null) {
			if (other.inflections != null) {
				return false;
			}
		} else if (!inflections.equals(other.inflections)) {
			return false;
		}
		if (lemma == null) {
			if (other.lemma != null) {
				return false;
			}
		} else if (!lemma.equals(other.lemma)) {
			return false;
		}
		if (pos == null) {
			if (other.pos != null) {
				return false;
			}
		} else if (!pos.equals(other.pos)) {
			return false;
		}
		if (usage == null) {
			if (other.usage != null) {
				return false;
			}
		} else if (!usage.equals(other.usage)) {
			return false;
		}
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		if (abbr == null) {
			if (other.abbr != null) {
				return false;
			}
		} else if (!abbr.equals(other.abbr)) {
			return false;
		}
		return true;
	}

}
