package de.ws1819.colewe.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Entry implements IsSerializable  {

	private WordForm lemma;
	private Pos pos;
	// TODO? add inflection info (map)
	private HashMap<String, WordForm> inflections;
	private HashSet<String> translations;
	private String grammarNO;
	private String usageNO;
	private String abbrNO;
	private String grammarDE;
	private String usageDE;
	private String abbrDE;

	// For GWT
	public Entry() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	// For dict.cc
	public Entry(WordForm lemma, Pos pos, String translation, String grammarNO, String usageNO, String abbrNO,
			String grammarDE, String usageDE, String abbrDE) {
		this(lemma, pos, null, null, grammarNO, usageNO, abbrNO, grammarDE, usageDE, abbrDE);
		addTranslation(translation);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections) {
		this(lemma, pos, inflections, null, null, null, null, null, null, null);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, String infl, WordForm inflForm) {
		this(lemma, pos, null, null, null, null, null, null, null, null);
		addInflection(infl, inflForm);
	}

	// For the NO>DE dictionary
	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections, Collection<String> translations) {
		this(lemma, pos, inflections, translations, null, null, null, null, null, null);
	}

	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections, Collection<String> translations,
			String grammarNO, String usageNO, String abbrNO, String grammarDE, String usageDE, String abbrDE) {
		setLemma(lemma);
		setPos(pos);
		setInflections(inflections);
		setTranslations(translations);
		setGrammarNO(grammarNO);
		setUsageNO(usageNO);
		setAbbrNO(abbrNO);
		setGrammarDE(grammarDE);
		setUsageDE(usageDE);
		setAbbrDE(abbrDE);
	}

	/**
	 * @return the lemma
	 */
	public WordForm getLemma() {
		return lemma;
	}

	/**
	 * @param lemma
	 *            the lemma to set
	 */
	public void setLemma(WordForm lemma) {
		this.lemma = lemma;
	}

	// public void setLemma(String lemma) {
	// this.lemma = new WordForm(lemma);
	// }

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
	public Map<String, WordForm> getInflections() {
		return inflections;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(Map<String, WordForm> inflections) {
		this.inflections = (inflections == null ? new HashMap<String, WordForm>()
				: new HashMap<String, WordForm>(inflections));
	}

	public void addInflection(String infl, WordForm form) {
		inflections.put(infl, form);
	}

	// public void addInflection(String infl, String form) {
	// inflections.put(infl, new WordForm(form));
	// }

	/**
	 * @return the translations
	 */
	public HashSet<String> getTranslations() {
		return translations;
	}

	public String getTranslationString() {
		String transl = translations.toString();
		// Remove [ and ]
		// TODO sort??
		return transl.substring(1, transl.length() - 1);
	}

	/**
	 * @param translations
	 *            the translations to set
	 */
	public void setTranslations(Collection<String> translations) {
		this.translations = (translations == null ? new HashSet<String>() : new HashSet<String>(translations));
	}

	public void addTranslation(String translation) {
		translations.add(translation);
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(HashMap<String, WordForm> inflections) {
		this.inflections = inflections;
	}

	/**
	 * @return the grammarNO
	 */
	public String getGrammarNO() {
		return grammarNO;
	}

	/**
	 * @param grammarNO
	 *            the grammarNO to set
	 */
	public void setGrammarNO(String grammarNO) {
		this.grammarNO = (grammarNO == null ? "" : grammarNO);
	}

	/**
	 * @return the usageNO
	 */
	public String getUsageNO() {
		return usageNO;
	}

	/**
	 * @param usageNO
	 *            the usageNO to set
	 */
	public void setUsageNO(String usageNO) {
		this.usageNO = (usageNO == null ? "" : usageNO);

	}

	/**
	 * @return the abbrNO
	 */
	public String getAbbrNO() {
		return abbrNO;
	}

	/**
	 * @param abbrNO
	 *            the abbrNO to set
	 */
	public void setAbbrNO(String abbrNO) {
		this.abbrNO = (abbrNO == null ? "" : abbrNO);

	}

	public String getGrammarDE() {
		return grammarDE;
	}

	public void setGrammarDE(String grammarDE) {
		this.grammarDE = (grammarDE == null ? "" : grammarDE);
	}

	public String getUsageDE() {
		return usageDE;
	}

	public void setUsageDE(String usageDE) {
		this.usageDE = (usageDE == null ? "" : usageDE);

	}

	public String getAbbrDE() {
		return abbrDE;
	}

	public void setAbbrDE(String abbrDE) {
		this.abbrDE = (abbrDE == null ? "" : abbrDE);
	}

	public String toString() {
		return lemma + ": " + translations + " (" + pos + ", {" + grammarNO + "} [" + usageNO + "] <" + abbrNO
				+ ">, inflections: " + inflections + ", DE: " + "{" + grammarDE + "} [" + usageDE + "] <" + abbrDE
				+ ">)";
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
		result = prime * result + ((abbrDE == null) ? 0 : abbrDE.hashCode());
		result = prime * result + ((abbrNO == null) ? 0 : abbrNO.hashCode());
		result = prime * result + ((grammarDE == null) ? 0 : grammarDE.hashCode());
		result = prime * result + ((grammarNO == null) ? 0 : grammarNO.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translations == null) ? 0 : translations.hashCode());
		result = prime * result + ((usageDE == null) ? 0 : usageDE.hashCode());
		result = prime * result + ((usageNO == null) ? 0 : usageNO.hashCode());
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
		if (abbrDE == null) {
			if (other.abbrDE != null) {
				return false;
			}
		} else if (!abbrDE.equals(other.abbrDE)) {
			return false;
		}
		if (abbrNO == null) {
			if (other.abbrNO != null) {
				return false;
			}
		} else if (!abbrNO.equals(other.abbrNO)) {
			return false;
		}
		if (grammarDE == null) {
			if (other.grammarDE != null) {
				return false;
			}
		} else if (!grammarDE.equals(other.grammarDE)) {
			return false;
		}
		if (grammarNO == null) {
			if (other.grammarNO != null) {
				return false;
			}
		} else if (!grammarNO.equals(other.grammarNO)) {
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
		if (pos != other.pos) {
			return false;
		}
		if (translations == null) {
			if (other.translations != null) {
				return false;
			}
		} else if (!translations.equals(other.translations)) {
			return false;
		}
		if (usageDE == null) {
			if (other.usageDE != null) {
				return false;
			}
		} else if (!usageDE.equals(other.usageDE)) {
			return false;
		}
		if (usageNO == null) {
			if (other.usageNO != null) {
				return false;
			}
		} else if (!usageNO.equals(other.usageNO)) {
			return false;
		}
		return true;
	}

}
