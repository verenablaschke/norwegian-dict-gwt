package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.HashSet;
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

	@SuppressWarnings("unchecked")
	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		logger.info("QUERY: " + word);
		ListMultimap<String, Entry> entries = (ListMultimap<String, Entry>) getServletContext().getAttribute("entries");
		ArrayList<Entry> results = querySingleWord(entries, word);
		if (results.isEmpty()) {
			// Attempt compound splitting.
			String first, second;
			for (int i = 1; i < word.length() - 1; i++) {
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
				break;
			}
		}
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

//	private ArrayList<Entry> query(ListMultimap<String, Entry> entries, String word) {
//		ArrayList<Entry> results = querySingleWord(entries, word);
//		if (results.isEmpty()) {
//			// Attempt compound splitting.
//			String first, second;
//			for (int i = 1; i < word.length() - 1; i++) {
//				first = word.substring(0, i);
//				ArrayList<Entry> resultsFirst = querySingleWord(entries, first);
//				if (resultsFirst.isEmpty()) {
//					continue;
//				}
//				second = word.substring(i);
//				ArrayList<Entry> resultsSecond = querySingleWord(entries, second);
//				if (resultsSecond.isEmpty()) {
//					if (i < word.length() - 3 && (second.startsWith("e") || second.startsWith("s"))) {
//						// -e- and -s- can be used to join compounds
//						resultsSecond = querySingleWord(entries, second.substring(1));
//						if (!resultsSecond.isEmpty()) {
//							results.addAll(resultsFirst);
//							results.addAll(resultsSecond);
//							break;
//						}
//					}
//					
//					
//					resultsSecond = query(entries, second);
//					// TODO -e-, -s-
//					if (! resultsSecond.isEmpty()){
//						break;
//					}
//				}
//				results.addAll(resultsFirst);
//				results.addAll(resultsSecond);
//				break;
//			}
//		}
//		return results;
//	}
}
