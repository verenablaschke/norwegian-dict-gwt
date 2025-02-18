package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.ws1819.colewe.client.DictionaryService;
import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;
import de.ws1819.colewe.shared.SampleSentence;

/**
 * The server side implementation of the RPC service. See section 3.2.
 * 
 * @author Verena Blaschke
 */
@SuppressWarnings("serial")
public class DictionaryServiceImpl extends RemoteServiceServlet implements DictionaryService {

	private static final Logger logger = Logger.getLogger(DictionaryServiceImpl.class.getSimpleName());

	@SuppressWarnings("unchecked")
	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		// Use the maps that were created in ProcessResources and added to the
		// servlet context in Listener.
		return query((ListMultimap<String, Entry>) getServletContext().getAttribute("entries"),
				(ListMultimap<String, Entry>) getServletContext().getAttribute("prefixes"),
				(ListMultimap<String, Entry>) getServletContext().getAttribute("suffixes"),
				(HashMap<String, String>) getServletContext().getAttribute("mtEntries"),
				(ListMultimap<String, SampleSentence>) getServletContext().getAttribute("extraSentences"), word);

	}

	// This method needs to be accessed from within DownloadServiceImpl as well
	// -> static.
	static ArrayList<Entry> query(ListMultimap<String, Entry> entries, ListMultimap<String, Entry> prefixes,
			ListMultimap<String, Entry> suffixes, HashMap<String, String> mlEntries,
			ListMultimap<String, SampleSentence> extraSentences, String word) {

		logger.info("QUERY: " + word);
		ArrayList<Entry> results = querySingleWord(entries, word);

		if (results.isEmpty()) {
			// Try splitting the word into translatable components.
			results = queryWithPossibleSplit(entries, prefixes, suffixes, word);

			// Fall-back options (section 3.5):
			
			// Try the automatically inferred translations.
			if (!word.contains(" ")) {
				logger.info("Machine translation");
				String translation = mlEntries.get(word);
				if (translation != null) {
					results.add(new Entry(word, translation, true));
				}
				logger.info("Sample sentences");
				for (SampleSentence sample : extraSentences.get(word)) {
					results.add(new Entry(sample.getNo(), sample.getDe(), false));
				}
			}

			// Still no results? Multi-word phrase with inflected word forms?
			if (results.isEmpty() && word.contains(" ")) {
				results = queryMultiWordPhrase(entries, word);
			}
		}

		logger.info("RESULTS: " + word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}
		return results;

	}

	private static ArrayList<Entry> querySingleWord(ListMultimap<String, Entry> entries, String word) {
		return querySingleWord(entries, word, false);
	}

	private static ArrayList<Entry> querySingleWord(ListMultimap<String, Entry> entries, String word,
			boolean uninflected) {
		logger.info("Querying " + word);
		List<Entry> results = entries.get(word);
		List<Entry> resultsChecked = new ArrayList<>();
		for (Entry entry : results) {
			if (uninflected) {
				// Skip inflected forms while looking for a potential first
				// element of a compound word.
				if (entry.getPos().equals(Pos.VERB) && !entry.getLemma().getForm().equals("å " + word)
						&& !entry.getLemma().getForm().equals("å " + word + "e")) {
					// Also accepting forms without the final -e,
					// e.g. blomstre -> blomstr+ing (blossom)
					logger.info("-- SKIP " + entry.toString());
					continue;
				}
				if (entry.getPos().equals(Pos.NOUN) && !entry.getLemma().getForm().equals(word)
						&& !entry.getLemma().equals(word + "e")) {
					// Also accepting forms without the final -e,
					// e.g. tilrettelegge -> tilrettelegg+else (preparation,
					// editing)
					logger.info("-- SKIP " + entry.toString());
					continue;
				}
			}
			resultsChecked.add(entry);
			logger.info("-- " + entry.toString());
		}
		return new ArrayList<Entry>(resultsChecked);
	}

	// Section 3.3
	private static ArrayList<Entry> queryWithPossibleSplit(ListMultimap<String, Entry> entries,
			ListMultimap<String, Entry> prefixes, ListMultimap<String, Entry> suffixes, String word) {
		ArrayList<Entry> results = querySingleWord(entries, word);
		if (results.isEmpty() && !word.contains(" ")) {
			// Attempt compound splitting.
			String first, second;
			// Try to only perform one split if possible.
			// Minimum morpheme length: 2 letters.
			for (int i = 2; i < word.length() - 2; i++) {
				first = word.substring(0, i);
				// If the first element is a verb or noun, it needs to be
				// uninflected.
				ArrayList<Entry> resultsFirst = querySingleWord(entries, first, true);
				if (resultsFirst.isEmpty()) {
					resultsFirst = querySingleWord(prefixes, first);
					if (resultsFirst.isEmpty()) {
						continue;
					}
				}
				second = word.substring(i);
				ArrayList<Entry> resultsSecond = querySingleWord(entries, second);
				if (resultsSecond.isEmpty()) {
					resultsSecond = querySingleWord(suffixes, second);
					if (resultsSecond.isEmpty()) {
						continue;
					}
				}
				results.addAll(resultsFirst);
				results.addAll(resultsSecond);
				return results;
			}

			// Attempt more splits.
			for (int i = 2; i < word.length() - 2; i++) {
				first = word.substring(0, i);
				ArrayList<Entry> resultsFirst = querySingleWord(entries, first);
				if (resultsFirst.isEmpty()) {
					continue;
				}
				second = word.substring(i);
				ArrayList<Entry> resultsSecond = queryWithPossibleSplit(entries, prefixes, suffixes, second);
				if (resultsSecond.isEmpty()) {
					continue;
				}
				results.addAll(resultsFirst);
				results.addAll(resultsSecond);
				break;
			}
		}
		return results;
	}

	// Section 3.4
	private static ArrayList<Entry> queryMultiWordPhrase(ListMultimap<String, Entry> entries, String word) {
		ArrayList<Entry> results = new ArrayList<>();
		String[] words = word.split(" ");
		HashSet<String> lemmatized = new HashSet<>();
		lemmatized.add("");
		for (int i = 0; i < words.length; i++) {
			ArrayList<Entry> intermResults = querySingleWord(entries, words[i]);
			if (intermResults.isEmpty()) {
				break;
			}
			HashSet<String> temp = new HashSet<>();
			for (String lemmatizedVersion : lemmatized) {
				for (Entry lemma : intermResults) {
					temp.add(lemmatizedVersion + " " + lemma.getLemma().getForm());
				}
			}
			lemmatized = temp;
		}
		logger.info("Possible lemmatized versions: " + lemmatized);
		for (String s : lemmatized) {
			s = s.trim();
			if (s.startsWith("å ")){
				s = s.substring(2);
			}
			results.addAll(querySingleWord(entries, s));
		}
		return results;
	}
}
