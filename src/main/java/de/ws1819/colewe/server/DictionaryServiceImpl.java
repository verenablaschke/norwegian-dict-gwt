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

	@SuppressWarnings("unchecked")
	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		logger.info("Querying " + word);
		List<Entry> results = ((ListMultimap<String, Entry>) getServletContext().getAttribute("entries")).get(word);
		for (Entry entry : results) {
			logger.info("-- " + entry.toString());
		}
		return new ArrayList<Entry>(results);
	}
}
