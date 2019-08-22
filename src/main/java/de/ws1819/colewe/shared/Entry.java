package de.ws1819.colewe.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Entry implements IsSerializable {

	private WordForm lemma;
	private Pos pos;
	private HashMap<String, WordForm> inflections;
	private ArrayList<TranslationalEquivalent> translations;
	private ArrayList<String> grammarNO;
	private ArrayList<String> usageNO;
	private String abbrNO;
	private int lemmaID;

	// For GWT
	public Entry() {
		this(null, null, null, null, null, null, null, -1);
	}

	// For dict.cc
	public Entry(WordForm lemma, Pos pos, TranslationalEquivalent translation, ArrayList<String> grammarNO,
			ArrayList<String> usageNO, String abbrNO) {
		this(lemma, pos, null, null, grammarNO, usageNO, abbrNO, -1);
		addTranslation(translation);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections, int lemmaID) {
		this(lemma, pos, inflections, null, null, null, null, lemmaID);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, String infl, WordForm inflForm, int lemmaID) {
		this(lemma, pos, null, null, null, null, null, lemmaID);
		addInflection(infl, inflForm);
	}

	// For the NO>DE dictionary
	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections,
			ArrayList<TranslationalEquivalent> translations, ArrayList<String> grammarNO, ArrayList<String> usageNO) {
		this(lemma, pos, inflections, translations, grammarNO, usageNO, null, -1);
	}

	public Entry(WordForm lemma, Pos pos, Map<String, WordForm> inflections,
			ArrayList<TranslationalEquivalent> translations, ArrayList<String> grammarNO, ArrayList<String> usageNO,
			String abbrNO, int lemmaID) {
		setLemma(lemma);
		setPos(pos);
		setInflections(inflections);
		setTranslations(translations);
		setGrammarNO(grammarNO);
		setUsageNO(usageNO);
		setAbbrNO(abbrNO);
		setLemmaID(lemmaID);
	}

	public boolean merge(Entry other) {
		if (other.lemma == null || other.lemma.getForm() == null) {
			return false;
		}
		if (!other.lemma.getForm().equals(lemma.getForm())) {
			return false;
		}
		if (pos != null && !pos.equals(Pos.NULL) && !pos.equals(other.pos)) {
			return false;
		}
		mergeWith(other);
		return true;

	}

	private void mergeWith(Entry other) {
		if (lemma.getPronunciation() == null || lemma.getPronunciation().isEmpty()) {
			lemma.setPronunciation(other.lemma.getPronunciation());
		}
		if (pos == null || pos.equals(Pos.NULL)) {
			setPos(other.pos);
		}
		if (inflections == null || inflections.isEmpty()) {
			setInflections(other.inflections);
		} else if (other.inflections != null) {
			for (java.util.Map.Entry<String, WordForm> infl : other.inflections.entrySet()) {
				addInflection(infl.getKey(), infl.getValue());
			}
		}
		if (translations == null || translations.isEmpty()) {
			translations = other.translations;
		} else if (other.translations != null && !other.translations.isEmpty()) {
			for (TranslationalEquivalent otherTransl : other.translations) {
				// Avoid duplicate translations
				boolean add = true;
				mid: for (TranslationalEquivalent transl : translations) {
					if (transl.equals(otherTransl)) {
						add = false;
						break;
					}
					for (String translString : otherTransl.getTranslation()) {
						if (transl.getTranslation().contains(translString)) {
							add = false;
							break mid;
						}
					}
				}
				if (add) {
					translations.add(otherTransl);
				}
			}
		}
		if (grammarNO == null || grammarNO.isEmpty()) {
			setGrammarNO(other.grammarNO);
		} else if (other.grammarNO != null && !other.grammarNO.isEmpty()) {
			for (String gram : other.grammarNO) {
				if (!grammarNO.contains(gram)) {
					grammarNO.add(gram);
				}
			}
		}
		if (usageNO == null || usageNO.isEmpty()) {
			setUsageNO(other.usageNO);
		} else if (other.usageNO != null && !other.usageNO.isEmpty()) {
			for (String usage : other.usageNO) {
				if (!usageNO.contains(usage)) {
					usageNO.add(usage);
				}
			}
		}
		// TODO should this be a list instead?
		if (abbrNO == null || abbrNO.isEmpty()) {
			setAbbrNO(other.abbrNO);
		} else if (other.abbrNO != null && !other.abbrNO.isEmpty()) {
			abbrNO += ", " + other.abbrNO;
		}
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
	public ArrayList<TranslationalEquivalent> getTranslations() {
		return translations;
	}

	/**
	 * @param translations
	 *            the translations to set
	 */
	public void setTranslations(ArrayList<TranslationalEquivalent> translations) {
		this.translations = (translations == null ? new ArrayList<TranslationalEquivalent>() : translations);
	}

	public void addTranslation(TranslationalEquivalent translation) {
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
	public ArrayList<String> getGrammarNO() {
		return grammarNO;
	}

	public String getGrammarString() {
		ArrayList<String> contents = new ArrayList<>(grammarNO);
		contents.add(0, pos.toString());
		return String.join(", ", contents);
	}

	/**
	 * @param grammarNO
	 *            the grammarNO to set
	 */
	public void setGrammarNO(ArrayList<String> grammarNO) {
		if (grammarNO == null) {
			this.grammarNO = new ArrayList<>();
			return;
		}
		grammarNO.removeIf(Objects::isNull);
		grammarNO.removeIf(String::isEmpty);
		this.grammarNO = grammarNO;
	}

	/**
	 * @return the usageNO
	 */
	public ArrayList<String> getUsageNO() {
		return usageNO;
	}

	public String getUsageString() {
		return String.join(", ", usageNO);
	}

	/**
	 * @param usageNO
	 *            the usageNO to set
	 */
	public void setUsageNO(ArrayList<String> usageNO) {
		if (usageNO == null) {
			this.usageNO = new ArrayList<>();
			return;
		}
		usageNO.removeIf(Objects::isNull);
		usageNO.removeIf(String::isEmpty);
		this.usageNO = usageNO;
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
		// this.abbrNO = (abbrNO == null ? new ArrayList<>() : abbrNO);
		this.abbrNO = (abbrNO == null ? "" : abbrNO);

	}

	/**
	 * @return the lemmaID
	 */
	public int getLemmaID() {
		return lemmaID;
	}

	/**
	 * @param lemmaID
	 *            the lemmaID to set
	 */
	public void setLemmaID(int lemmaID) {
		this.lemmaID = lemmaID;
	}

	public String toString() {
		return lemma + ": " + translations + " (" + pos + ", {" + grammarNO + "} [" + usageNO + "] <" + abbrNO
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
		result = prime * result + ((abbrNO == null) ? 0 : abbrNO.hashCode());
		result = prime * result + ((grammarNO == null) ? 0 : grammarNO.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translations == null) ? 0 : translations.hashCode());
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
		if (abbrNO == null) {
			if (other.abbrNO != null) {
				return false;
			}
		} else if (!abbrNO.equals(other.abbrNO)) {
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
