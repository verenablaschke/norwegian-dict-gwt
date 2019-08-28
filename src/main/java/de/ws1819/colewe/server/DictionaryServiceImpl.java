package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.ws1819.colewe.client.DictionaryService;
import de.ws1819.colewe.shared.Entry;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DictionaryServiceImpl extends RemoteServiceServlet implements DictionaryService {

	private static final Logger logger = Logger.getLogger(DictionaryServiceImpl.class.getSimpleName());

	@SuppressWarnings("unchecked")
	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		logger.info("QUERY: " + word);
		ListMultimap<String, Entry> entries = (ListMultimap<String, Entry>) getServletContext().getAttribute("entries");
		ListMultimap<String, Entry> prefixes = (ListMultimap<String, Entry>) getServletContext()
				.getAttribute("prefixes");
		ListMultimap<String, Entry> suffixes = (ListMultimap<String, Entry>) getServletContext()
				.getAttribute("suffixes");
		ArrayList<Entry> results = queryWithPossibleSplit(entries, prefixes, suffixes, word);
		logger.info("RESULTS: " + word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}

		ArrayList<Entry> collocs = new ArrayList<>();
		HashMultimap<String, String> collocations = (HashMultimap<String, String>) getServletContext()
				.getAttribute("collocations");
		logger.info("COLLOCATIONS: " + word);
		// TODO should be lemma
		for (String colloc : collocations.get(word)) {
			logger.info("-- " + colloc);
			collocs.addAll(entries.get(colloc));
		}

		// TODO
		results.addAll(collocs);

		return results;
	}

	private ArrayList<Entry> querySingleWord(ListMultimap<String, Entry> entries, String word) {
		logger.info("Querying " + word);
		List<Entry> results = entries.get(word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}
		return new ArrayList<Entry>(results);
	}

	private ArrayList<Entry> queryWithPossibleSplit(ListMultimap<String, Entry> entries,
			ListMultimap<String, Entry> prefixes, ListMultimap<String, Entry> suffixes, String word) {
		ArrayList<Entry> results = querySingleWord(entries, word);
		if (results.isEmpty()) {
			// Attempt compound splitting.
			String first, second;
			// Try to do the least amount of splits possible.
			// Minimum morpheme length: 2 letters.
			for (int i = 2; i < word.length() - 2; i++) {
				first = word.substring(0, i);
				ArrayList<Entry> resultsFirst = querySingleWord(entries, first);
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
}
