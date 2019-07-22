package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.SetMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.ws1819.colewe.client.DictionaryService;
import de.ws1819.colewe.shared.Entry;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DictionaryServiceImpl extends RemoteServiceServlet implements DictionaryService {

	private static final Logger logger = Logger.getLogger(DictionaryServiceImpl.class.getSimpleName()); // TODO del?

	@SuppressWarnings("unchecked")
	public ArrayList<Entry> query(String word) throws IllegalArgumentException {
		logger.info("Querying " + word);
		SetMultimap<String, Entry> tokenMap = (SetMultimap<String, Entry>) getServletContext().getAttribute("entries");
		logger.info("-- " + tokenMap.get(word).toString());
		return new ArrayList<Entry>(tokenMap.get(word));
	}
}
