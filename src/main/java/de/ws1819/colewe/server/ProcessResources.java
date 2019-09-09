package de.ws1819.colewe.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;
import de.ws1819.colewe.shared.SampleSentence;
import de.ws1819.colewe.shared.WordForm;

/**
 * Extract the information from the dictionary dumps and inflection list. See
 * section 2 of the report.
 * 
 * @author Verena Blaschke
 */
public class ProcessResources {

	private static final Logger logger = Logger.getLogger(ProcessResources.class.getSimpleName());

	/**
	 * Processes the entry/inflection input files and merges them. See section
	 * 2.6 of the report.
	 * 
	 * @param dictccInputStream
	 * @param lemmaInputStream
	 * @param ordbankInputStream
	 * @param langenscheidtInputStream
	 * @return an Object array consisting of a ListMultimap<String, Entry> from
	 *         (inflected and uninflected) word forms to entries, a HashSet
	 *         <String> containing stopwords, a ListMultimap<String, Entry> from
	 *         prefix forms to entries, a ListMultimap<String, Entry> from
	 *         suffix forms to entries, and a HashMap<String, String> from
	 *         Norwegian sentences to their German translations
	 */
	@SuppressWarnings("unchecked")
	static Object[] generateEntries(InputStream dictccInputStream, InputStream lemmaInputStream,
			InputStream ordbankInputStream, InputStream langenscheidtInputStream) {
		logger.info("Start reading dict.cc");
		Object[] dictccResults = DictionaryReader.readDictCc(dictccInputStream);
		ListMultimap<String, Entry> dictcc = (ListMultimap<String, Entry>) dictccResults[0];
		HashSet<String> stopwords = (HashSet<String>) dictccResults[1];

		logger.info("Start reading lemma.txt");
		HashMap<Integer, String> lemmata = DictionaryReader.readOrdbankLemmas(lemmaInputStream);

		logger.info("Start reading fullformsliste.txt");
		Object[] ordbankResults = DictionaryReader.readOrdbankFullformsliste(lemmata, ordbankInputStream);
		ListMultimap<String, Entry> fullformsliste = (ListMultimap<String, Entry>) ordbankResults[0];
		stopwords.addAll((HashSet<String>) ordbankResults[1]);
		HashSet<String> affixesFullformsliste = (HashSet<String>) ordbankResults[2];

		logger.info("Start reading no-de-dict.txt");
		Object[] langenscheidtResults = DictionaryReader.readLangenscheidt(langenscheidtInputStream);
		ListMultimap<String, Entry> langenscheidt = (ListMultimap<String, Entry>) langenscheidtResults[0];
		stopwords.addAll((HashSet<String>) langenscheidtResults[1]);

		// Map from all inflected versions of the word to the entries.
		// Using list-based multimaps instead of set-based multimaps since they
		// require less memory.
		ListMultimap<String, Entry> allEntries = ArrayListMultimap.create();
		HashMultimap<String, Entry> collocations = HashMultimap.create();
		ListMultimap<String, Entry> prefixes = ArrayListMultimap.create();
		ListMultimap<String, Entry> suffixes = ArrayListMultimap.create();
		HashMap<String, String> sentencePairs = new HashMap<>();
		HashSet<String> allLemmas = new HashSet<String>(dictcc.keySet());
		allLemmas.addAll(langenscheidt.keySet());
		allLemmas.addAll(affixesFullformsliste);
		logger.info(allLemmas.size() + " distinct lemmata.");
		for (String lemma : allLemmas) {
			ListMultimap<String, Entry> entries = ArrayListMultimap.create();

			// Map lemmas and inflected forms from the NO>DE dictionary
			// to Entry objects.
			List<Entry> entriesLangenscheidt = langenscheidt.get(lemma);
			if (entriesLangenscheidt != null) {
				for (Entry entryL : entriesLangenscheidt) {
					if (entryL.getPos().equals(Pos.SENT)) {
						// Treat sentences as sample sentences rather than full
						// entries.
						sentencePairs.put(entryL.getLemma().getForm(),
								entryL.getTranslations().get(0).getTranslation().get(0));
						continue;
					}
					addInfMarker(entryL);
					addEntry(entryL, lemma, entries, collocations, stopwords, prefixes, suffixes, false);
					for (String wordForm : entryL.getInflections()) {
						addEntry(entryL, wordForm, entries, collocations, stopwords, prefixes, suffixes, false);
					}
				}
			}

			// Map deno.dict.cc lemmas to Entry objects.
			List<Entry> entriesDictCc = dictcc.get(lemma);
			if (entriesDictCc != null) {
				for (Entry entryD : entriesDictCc) {
					if (entryD.getPos().equals(Pos.SENT)) {
						// Treat sentences as sample sentences rather than full
						// entries.
						sentencePairs.put(entryD.getLemma().getForm(),
								entryD.getTranslations().get(0).getTranslation().get(0));
						continue;
					}
					addInfMarker(entryD);
					addEntry(entryD, lemma, entries, collocations, stopwords, prefixes, suffixes, false);
					for (String abbr : entryD.getAbbr()) {
						addEntry(entryD, abbr, entries, collocations, stopwords, prefixes, suffixes, false);
					}
					for (String wordForm : entryD.getInflections()) {
						addEntry(entryD, wordForm, entries, collocations, stopwords, prefixes, suffixes, false);
					}
				}
			}

			// Add inflection information to the bilingual entries.
			List<Entry> entriesFullformsliste = fullformsliste.get(lemma);
			if (entriesFullformsliste != null) {
				for (Entry entryF : entriesFullformsliste) {
					addInfMarker(entryF);
					addEntry(entryF, lemma, entries, collocations, stopwords, prefixes, suffixes, true);
					for (String wordForm : entryF.getInflections()) {
						addEntry(entryF, wordForm, entries, collocations, stopwords, prefixes, suffixes, true);
					}
				}
			}

			allEntries.putAll(entries);
		}

		// Set the collocations (section 2.8).
		for (java.util.Map.Entry<String, Entry> colloc : collocations.entries()) {
			for (Entry entry : allEntries.get(colloc.getKey())) {
				entry.addCollocation(colloc.getValue());
			}
		}

		return new Object[] { allEntries, stopwords, prefixes, suffixes, sentencePairs };
	}

	private static void addEntry(Entry entry, String wordForm, ListMultimap<String, Entry> entries,
			HashMultimap<String, Entry> collocations, HashSet<String> stopwords, ListMultimap<String, Entry> prefixes,
			ListMultimap<String, Entry> suffixes, boolean fullformsliste) {
		switch (entry.getPos()) {
		case PFX:
			addEntry(entry, wordForm, prefixes, null, null, true, fullformsliste);
			break;
		case SFX:
			addEntry(entry, wordForm, suffixes, null, null, true, fullformsliste);
			break;
		case NOUN:
			// Noun forms used in compounds.
			if (wordForm.equals(entry.getLemma().getForm())) {
				if (!wordForm.endsWith("s")) {
					// e.g. varmt+vann+s+kran (hot water tap)
					addEntry(entry, wordForm + "s", prefixes, null, null, true, fullformsliste);
				}
				if (!Tools.isVowel(wordForm.charAt(wordForm.length() - 1))
						// The following suffixes are always combined via -s-:
						&& !wordForm.endsWith("sjon") && !wordForm.endsWith("else") && !wordForm.endsWith("het")
						&& !wordForm.endsWith("tet") && !wordForm.endsWith("ment") && !wordForm.endsWith("dom")
						&& !wordForm.endsWith("skap")
						// Words ending in -ing are either combined directly or
						// via -s-:
						&& !wordForm.endsWith("ing")) {
					// e.g. trikk+e+stans (tram system interruption)
					addEntry(entry, wordForm + "e", prefixes, null, null, true, fullformsliste);
				}
			}
		default:
			addEntry(entry, wordForm, entries, collocations, stopwords, false, fullformsliste);
		}
	}

	/**
	 * Normalizes a look-up form (inflected or not) of an entry.
	 * 
	 * @param s
	 * @param pos
	 * @return
	 */
	private static String normalize(String s, Pos pos) {
		s = s.toLowerCase().replaceAll("[®&:§–@\"\\{\\}\\[\\]\\(\\)\\!\\?\\.,%/]+", " ");
		if (pos != null && pos.equals(Pos.VERB)) {
			// Remove 'noen' ('somebody') and 'noe' ('something').
			s = s.replaceAll(" noen?s?", "");
		}
		return s.replaceAll("\\s+", " ").trim();
	}

	private static void addEntry(Entry entry, String wordForm, ListMultimap<String, Entry> entries,
			HashMultimap<String, Entry> collocations, HashSet<String> stopwords, boolean affixes,
			boolean fullformsliste) {

		wordForm = normalize(wordForm, entry.getPos());
		if (affixes) {
			wordForm = wordForm.replace("-", "");
		} else if (!fullformsliste) {
			// Extract collocations (see section 2.8).
			String[] components = wordForm.split(" ");
			if (components.length > 1 && !(components.length == 2 && components[0].equals("å"))) {
				for (int i = 0; i < components.length; i++) {
					if (stopwords.contains(components[i])) {
						continue;
					}
					boolean added = false;
					for (Entry colloc : collocations.get(components[i])) {
						if (colloc.equals(entry) || colloc.merge(entry)) {
							added = true;
							break;
						}
					}
					if (!added) {
						collocations.put(components[i], entry);
					}
				}
			}
		}

		// Try to merge entries.
		for (Entry existingEntry : entries.values()) {
			if (existingEntry.merge(entry)) {
				// This would have been nicer with a SetMultimap, but the memory
				// overhead issues are too big a downside.
				List<Entry> entryList = entries.get(wordForm);
				if (entryList == null || entryList.isEmpty() || !entryList.contains(existingEntry)) {
					entries.put(wordForm, existingEntry);
					return;
				}
				return;
			}
		}

		// Don't add entries without translation information,
		// we're already struggling with memory as is.
		if (fullformsliste && !affixes) {
			return;
		}

		// Couldn't merge the entry with an existing one, add to collection:
		entries.put(wordForm, entry);
	}

	/**
	 * If appropriate, adds the infinitive marker to a lemma.
	 * 
	 * @param entry
	 */
	private static void addInfMarker(Entry entry) {
		if (entry.getPos().equals(Pos.VERB) && !entry.getLemma().getForm().startsWith("å ")) {
			entry.setLemma(new WordForm("å " + entry.getLemma().getForm(), entry.getLemma().getPronunciation()));
			entry.addInflection(entry.getLemma().getForm());
		}
	}

	/**
	 * Sets the sample sentences (section 2.9).
	 * 
	 * @param tatoebaInputStream
	 * @param entries
	 * @param stopwords
	 * @param sentencePairs
	 * @return an Object array consisting of a ListMultimap<String, Entry> from
	 *         word forms to entries and a ListMultimap<String, SampleSentence>
	 *         from word forms that are not in the previous map to sample
	 *         sentences
	 */
	static Object[] setSampleSentences(InputStream tatoebaInputStream, ListMultimap<String, Entry> entries,
			HashSet<String> stopwords, HashMap<String, String> sentencePairs) {
		logger.info("Start reading sentence-pairs.ser");
		sentencePairs.putAll(DictionaryReader.readTatoeba(tatoebaInputStream));
		ListMultimap<String, SampleSentence> extraSentences = ArrayListMultimap.create();
		for (java.util.Map.Entry<String, String> sentencePair : sentencePairs.entrySet()) {
			if (sentencePair.getKey().length() > 120) {
				// Some of the sample sentences are very long and/or entire
				// paragraphs. This seems very impractical for a dictionary
				// user.
				continue;
			}
			String[] words = normalize(sentencePair.getKey(), null).split(" ");
			if (words.length > 1 && !(words.length == 2 && words[0].equals("å"))) {
				for (int i = 0; i < words.length; i++) {
					String word = words[i];
					if (stopwords.contains(word)) {
						continue;
					}
					SampleSentence sample = new SampleSentence(sentencePair);
					List<Entry> matchingEntries = entries.get(word);
					if (matchingEntries.isEmpty()) {
						extraSentences.put(word, sample);
					} else {
						for (Entry entry : matchingEntries) {
							entry.addSampleSentence(sample);
						}
					}
				}
			}
		}

		return new Object[] { entries, extraSentences };
	}

}
