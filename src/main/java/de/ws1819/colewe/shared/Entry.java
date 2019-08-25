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
	private ArrayList<String> grammar;
	private ArrayList<String> usage;
	private ArrayList<String> abbr;
	private int lemmaID;

	// For GWT
	public Entry() {
		this(null, null, null, null, null, null, null, -1);
	}

	// For dict.cc
	public Entry(WordForm lemma, Pos pos, TranslationalEquivalent translation, ArrayList<String> grammarNO,
			ArrayList<String> usageNO, ArrayList<String> abbrNO) {
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
			ArrayList<String> abbrNO, int lemmaID) {
		setLemma(lemma);
		setPos(pos);
		setInflections(inflections);
		setTranslations(translations);
		setGrammar(grammarNO);
		setUsage(usageNO);
		setAbbr(abbrNO);
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
					for (String otherTranslString : otherTransl.getTranslation()) {
						if (transl.getTranslation().contains(otherTranslString)) {
							add = false;
							transl.addGrammar(otherTransl.getGrammar());
							transl.addUsage(otherTransl.getUsage());
							transl.addAbbr(otherTransl.getAbbr());
							break mid;
						}
					}
				}
				if (add) {
					translations.add(otherTransl);
				}
			}
		}
		if (grammar == null || grammar.isEmpty()) {
			setGrammar(other.grammar);
		} else if (other.grammar != null && !other.grammar.isEmpty()) {
			for (String gram : other.grammar) {
				if (!grammar.contains(gram)) {
					grammar.add(gram);
				}
			}
		}
		if (usage == null || usage.isEmpty()) {
			setUsage(other.usage);
		} else if (other.usage != null && !other.usage.isEmpty()) {
			for (String use : other.usage) {
				if (!usage.contains(use)) {
					usage.add(use);
				}
			}
		}
		if (abbr == null || abbr.isEmpty()) {
			setAbbr(other.abbr);
		} else if (other.abbr != null && !other.abbr.isEmpty()) {
			for (String abbrev : other.abbr) {
				if (!abbr.contains(abbrev)) {
					abbr.add(abbrev);
				}
			}
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

	public ArrayList<WordForm> getDisplayableInflections() {
		// Only display noteworthy inflections, i.e. inflections that were
		// listed in the curated dictionary.
		ArrayList<WordForm> inflectionsToDisplay = new ArrayList<>();
		int i = 1;
		WordForm infl = null;
		while ((infl = inflections.get(((Integer) i).toString())) != null) {
			inflectionsToDisplay.add(infl);
			i++;
		}
		return inflectionsToDisplay;
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
	 * @return the grammar
	 */
	public ArrayList<String> getGrammar() {
		return grammar;
	}

	public String getGrammarString() {
		ArrayList<String> contents = new ArrayList<>(grammar);
		if (pos != null && !pos.equals(Pos.NULL)) {
			contents.add(0, pos.toString());
		}
		return String.join(", ", contents);
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

	public String getAbbrString() {
		return String.join(", ", abbr);
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
		return lemma + ": " + translations + " (" + pos + ", {" + grammar + "} [" + usage + "] <" + abbr
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
		result = prime * result + ((abbr == null) ? 0 : abbr.hashCode());
		result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translations == null) ? 0 : translations.hashCode());
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
		if (!(obj instanceof Entry)) {
			return false;
		}
		Entry other = (Entry) obj;
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
