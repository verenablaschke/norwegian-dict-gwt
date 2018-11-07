package de.ws1819.colewe.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.ws1819.colewe.shared.Entry;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("DictionaryService")
public interface DictionaryService extends RemoteService {
	
	ArrayList<Entry> query(String word);

	// Allows for static access of the service.
	public static class App {
		private static final DictionaryServiceAsync instance = (DictionaryServiceAsync) GWT.create(DictionaryService.class);

		public static DictionaryServiceAsync getInstance() {
			return instance;
		}
	}
}
