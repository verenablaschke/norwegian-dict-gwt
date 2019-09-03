package de.ws1819.colewe.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Entry implements IsSerializable {

	private WordForm lemma;
	private Pos pos;
	private HashSet<String> inflections;
	private ArrayList<WordForm> displayableInflections;
	private ArrayList<TranslationalEquivalent> translations;
	private ArrayList<String> grammar;
	private ArrayList<String> usage;
	private ArrayList<String> abbr;
	private int lemmaID;
	private HashSet<Entry> collocations;
	private HashSet<SampleSentence> sampleSentences;

	// For GWT
	public Entry() {
		this(null, null, null, null, null, null, null, null, -1);
	}

	// For dict.cc
	public Entry(WordForm lemma, Pos pos, TranslationalEquivalent translation, ArrayList<String> grammarNO,
			ArrayList<String> usageNO, ArrayList<String> abbrNO) {
		this(lemma, pos, null, null, null, grammarNO, usageNO, abbrNO, -1);
		addTranslation(translation);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, HashSet<String> inflections, ArrayList<WordForm> displayableInflections,
			int lemmaID) {
		this(lemma, pos, inflections, displayableInflections, null, null, null, null, lemmaID);
	}

	// For språkbanken
	public Entry(WordForm lemma, Pos pos, String infl, WordForm displayableInfl, int lemmaID) {
		this(lemma, pos, null, null, null, null, null, null, lemmaID);
		addInflection(infl);
		addDisplayableInflection(displayableInfl);
	}

	// For the NO>DE dictionary
	public Entry(WordForm lemma, Pos pos, HashSet<String> inflections, ArrayList<WordForm> displayableInflections,
			ArrayList<TranslationalEquivalent> translations, ArrayList<String> grammarNO, ArrayList<String> usageNO) {
		this(lemma, pos, inflections, displayableInflections, translations, grammarNO, usageNO, null, -1);
	}

	// For automatically inferred entries
	public Entry(String lemma, String translation) {
		this(new WordForm(lemma), Pos.NULL, null, null, null, null, null, null, -1);
		addTranslation(new TranslationalEquivalent(translation, true));
	}

	public Entry(WordForm lemma, Pos pos, HashSet<String> inflections, ArrayList<WordForm> displayableInflections,
			ArrayList<TranslationalEquivalent> translations, ArrayList<String> grammarNO, ArrayList<String> usageNO,
			ArrayList<String> abbrNO, int lemmaID) {
		setLemma(lemma);
		setPos(pos);
		setInflections(inflections);
		setDisplayableInflections(displayableInflections);
		setTranslations(translations);
		setGrammar(grammarNO);
		setUsage(usageNO);
		setAbbr(abbrNO);
		setLemmaID(lemmaID);
		setCollocations(null);
		setSampleSentences(null);
	}

	public boolean merge(Entry other) {
		if (other.lemma == null || other.lemma.getForm() == null) {
			return false;
		}
		if (!other.lemma.getForm().equals(lemma.getForm())) {
			return false;
		}
		if (pos != null && other.pos != null && !pos.equals(Pos.NULL) && !other.pos.equals(Pos.NULL)
				&& !pos.equals(other.pos)) {
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
			inflections = other.inflections;
		} else if (other.inflections != null && !other.inflections.isEmpty()) {
			inflections.addAll(other.inflections);
		}
		// Only add irregular inflections from fullformsliste if there weren't
		// any in no-de-dict.
		if (displayableInflections == null || displayableInflections.isEmpty()) {
			setDisplayableInflections(other.displayableInflections);
		}
		if (translations == null || translations.isEmpty()) {
			translations = other.translations;
		} else if (other.translations != null && !other.translations.isEmpty()
				&& !translations.equals(other.translations)) {
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
		// Collocations don't matter here, since they're set at a later step.
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
	public HashSet<String> getInflections() {
		return inflections;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(HashSet<String> inflections) {
		this.inflections = (inflections == null ? new HashSet<String>() : inflections);
	}

	public void addInflection(String infl) {
		inflections.add(infl);
	}

	public void setDisplayableInflections(ArrayList<WordForm> displayableInflections) {
		this.displayableInflections = (displayableInflections == null ? new ArrayList<>() : displayableInflections);
	}

	public ArrayList<WordForm> getDisplayableInflections() {
		// Only display noteworthy inflections, i.e. inflections that were
		// listed in the curated dictionary.
		return displayableInflections;
	}

	public void addDisplayableInflection(WordForm infl) {
		if (infl == null) {
			return;
		}
		if (!displayableInflections.contains(infl)) {
			displayableInflections.add(infl);
		}
	}

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
	 * @return the grammar
	 */
	public ArrayList<String> getGrammar() {
		return grammar;
	}

	public String getGrammarString(Language lang) {
		ArrayList<String> contents = new ArrayList<>(grammar);
		if (pos != null && !pos.equals(Pos.NULL)) {
			contents.add(0, pos.toString(lang));
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
	 * @return the collocations
	 */
	public HashSet<Entry> getCollocations() {
		return collocations;
	}

	public boolean hasColloctations() {
		return !collocations.isEmpty();
	}

	/**
	 * @param collocations
	 *            the collocations to set
	 */
	public void setCollocations(HashSet<Entry> collocations) {
		this.collocations = (collocations == null ? new HashSet<>() : collocations);
	}

	public void addCollocation(Entry colloc) {
		collocations.add(colloc);
	}

	public HashSet<SampleSentence> getSampleSentences() {
		return sampleSentences;
	}

	public void setSampleSentences(HashSet<SampleSentence> sampleSentences) {
		this.sampleSentences = (sampleSentences == null ? new HashSet<>() : sampleSentences);
	}

	public void addSampleSentence(SampleSentence sampleSentence) {
		sampleSentences.add(sampleSentence);
	}

	public boolean hasSampleSentences() {
		return !sampleSentences.isEmpty();
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
				+ ">, irreg infl: " + displayableInflections + ", all: " + inflections + ")";
	}

	public String htmlAnchor() {
		return lemma.getForm().replaceAll("[®&:§–@\"\\{\\}\\[\\]\\(\\)\\!\\?\\.,%/]+", " ").replace(" ", "_") + "-"
				+ pos;
	}

	public String toPrintString() {
		return toPrintString(true, "");
	}

	public String toPrintString(boolean showCollocs, String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append(lemma).append(" (").append(pos).append(")");
		if (grammar != null && !grammar.isEmpty()) {
			sb.append(" {").append(String.join(", ", grammar)).append("}");
		}
		if (usage != null && !usage.isEmpty()) {
			sb.append(usage);
		}
		if (abbr != null && !abbr.isEmpty()) {
			sb.append(" <").append(String.join(", ", abbr)).append(">");
		}
		if (displayableInflections != null && !displayableInflections.isEmpty()) {
			for (WordForm infl : displayableInflections) {
				sb.append(infl).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("\n");
		for (TranslationalEquivalent transl : translations) {
			sb.append(prefix);
			sb.append("> ");
			sb.append(transl.toPrintString());
			sb.append("\n");
		}
		if (showCollocs) {
			if (hasColloctations()) {
				sb.append("Collocations:\n");
				for (Entry colloc : collocations) {
					sb.append("-\t");
					sb.append(colloc.toPrintString(false, "\t"));
					sb.append("\n");
				}
			}
			if (hasSampleSentences()) {
				sb.append("Sample sentences:\n");
				for (SampleSentence sent : sampleSentences) {
					sb.append("--\t");
					sb.append(sent);
					sb.append("\n");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
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
		result = prime * result + ((displayableInflections == null) ? 0 : displayableInflections.hashCode());
		result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + lemmaID;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translations == null) ? 0 : translations.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		// Exclude collocations and sample sentences!
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
		if (displayableInflections == null) {
			if (other.displayableInflections != null) {
				return false;
			}
		} else if (!displayableInflections.equals(other.displayableInflections)) {
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
		if (lemmaID != other.lemmaID) {
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
		// Exclude collocations and sample sentences!
	}

}
