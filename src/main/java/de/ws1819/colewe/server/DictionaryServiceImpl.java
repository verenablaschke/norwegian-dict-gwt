package de.ws1819.colewe.server;

import java.util.List;

import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.ws1819.colewe.client.DictionaryService;
import de.ws1819.colewe.shared.Entry;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DictionaryServiceImpl extends RemoteServiceServlet implements DictionaryService {

	@SuppressWarnings("unchecked")
	public String query(String word) throws IllegalArgumentException {
		ListMultimap<String, Entry> tokenMap = (ListMultimap<String, Entry>) getServletContext()
				.getAttribute("entries");
		List<Entry> results = tokenMap.get(word);
		if (results == null || results.isEmpty()) {
			return "No results found :(";
		}
		return results.get(0).toString();
	}
}
