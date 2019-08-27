package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		logger.info("QUERY: " + word);
		ArrayList<Entry> results = startQuery(word);
		logger.info("RESULTS: " + word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Entry> startQuery(String word) {
		ListMultimap<String, Entry> entries = (ListMultimap<String, Entry>) getServletContext().getAttribute("entries");

		ArrayList<Entry> results = queryWithPossibleSplit(entries, word);
		if (!results.isEmpty()) {
			return results;
		}

		// Try combinations with (possibly untranslated) prefixes.
		ListMultimap<String, Entry> prefixes = (ListMultimap<String, Entry>) getServletContext()
				.getAttribute("prefixes");
		for (String pfx : prefixes.keySet()) {
			if (word.startsWith(pfx)) {
				results = queryWithPossibleSplit(entries, word.substring(pfx.length()));
				if (results.isEmpty()) {
					continue;
				}
				results.addAll(0, prefixes.get(pfx));
				return results;
			}
		}

		// Try combinations with (possibly untranslated) suffixes.
		ListMultimap<String, Entry> suffixes = (ListMultimap<String, Entry>) getServletContext()
				.getAttribute("suffixes");
		for (String sfx : suffixes.keySet()) {
			if (word.endsWith(sfx)) {
				results = queryWithPossibleSplit(entries, word.substring(0, word.length() - sfx.length()));
				if (results.isEmpty()) {
					continue;
				}
				results.addAll(suffixes.get(sfx));
				return results;
			}
		}

		return results; // empty list
	}

	private ArrayList<Entry> querySingleWord(ListMultimap<String, Entry> entries, String word) {
		logger.info("Querying " + word);
		List<Entry> results = entries.get(word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}
		return new ArrayList<Entry>(results);
	}

	private ArrayList<Entry> queryWithPossibleSplit(ListMultimap<String, Entry> entries, String word) {
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
					continue;
				}
				second = word.substring(i);
				ArrayList<Entry> resultsSecond = querySingleWord(entries, second);
				if (resultsSecond.isEmpty()) {
					continue;
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
				ArrayList<Entry> resultsSecond = queryWithPossibleSplit(entries, second);
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
