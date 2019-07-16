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
	private String grammarNO;
	private String usageNO;
	private String abbrNO;
	private String grammarDE;
	private String usageDE;
	private String abbrDE;

	public Entry() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public Entry(String lemma, Pos pos, String translation, String grammarNO, String usageNO, String abbrNO, String grammarDE,
			String usageDE, String abbrDE) {
		this(lemma, pos, null, translation, grammarNO, usageNO, abbrNO, grammarDE, usageDE, abbrDE);
	}

	public Entry(String lemma, Pos pos, Map<String, String> inflections) {
		this(lemma, pos, inflections, null, null, null, null, null, null, null);
	}

	public Entry(String lemma, Pos pos, String infl, String inflForm) {
		this(lemma, pos, null, null, null, null, null, null, null, null);
		addInflection(infl, inflForm);
	}

	public Entry(String lemma, Pos pos, Map<String, String> inflections, String translation, String grammarNO,
			String usageNO, String abbrNO, String grammarDE, String usageDE, String abbrDE) {
		this.lemma = lemma;
		setPos(pos);
		setInflections(inflections);
		this.translation = translation;
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
		if (grammarNO == null) {
			this.grammarNO = "";
		} else {
			this.grammarNO = grammarNO;
		}
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
		if (usageNO == null) {
			this.usageNO = "";
		} else {
			this.usageNO = usageNO;
		}
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
		if (abbrNO == null) {
			this.abbrNO = "";
		} else {
			this.abbrNO = abbrNO;
		}
	}

	public String getGrammarDE() {
		return grammarDE;
	}

	public void setGrammarDE(String grammarDE) {
		if (grammarDE == null) {
			this.grammarDE = "";
		} else {
			this.grammarDE = grammarDE;
		}
	}

	public String getUsageDE() {
		return usageDE;
	}

	public void setUsageDE(String usageDE) {
		if (usageDE == null) {
			this.usageDE = "";
		} else {
			this.usageDE = usageDE;
		}
	}

	public String getAbbrDE() {
		return abbrDE;
	}

	public void setAbbrDE(String abbrDE) {
		if (abbrDE == null) {
			this.abbrDE = "";
		} else {
			this.abbrDE = abbrDE;
		}
	}

	public String toString() {
		return lemma + ": " + translation + " (" + pos + ", {" + grammarNO + "} [" + usageNO + "] <" + abbrNO
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
		result = prime * result + ((abbrNO == null) ? 0 : abbrNO.hashCode());
		result = prime * result + ((abbrDE == null) ? 0 : abbrDE.hashCode());
		result = prime * result + ((grammarNO == null) ? 0 : grammarNO.hashCode());
		result = prime * result + ((grammarDE == null) ? 0 : grammarDE.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((usageNO == null) ? 0 : usageNO.hashCode());
		result = prime * result + ((usageDE == null) ? 0 : usageDE.hashCode());
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
		if (abbrNO == null) {
			if (other.abbrNO != null) {
				return false;
			}
		} else if (!abbrNO.equals(other.abbrNO)) {
			return false;
		}
		if (abbrDE == null) {
			if (other.abbrDE != null) {
				return false;
			}
		} else if (!abbrDE.equals(other.abbrDE)) {
			return false;
		}
		if (grammarNO == null) {
			if (other.grammarNO != null) {
				return false;
			}
		} else if (!grammarNO.equals(other.grammarNO)) {
			return false;
		}
		if (grammarDE == null) {
			if (other.grammarDE != null) {
				return false;
			}
		} else if (!grammarDE.equals(other.grammarDE)) {
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
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		if (usageNO == null) {
			if (other.usageNO != null) {
				return false;
			}
		} else if (!usageNO.equals(other.usageNO)) {
			return false;
		}
		if (usageDE == null) {
			if (other.usageDE != null) {
				return false;
			}
		} else if (!usageDE.equals(other.usageDE)) {
			return false;
		}
		return true;
	}

}
