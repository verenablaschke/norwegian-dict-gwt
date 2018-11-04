package de.ws1819.colewe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("DictionaryService")
public interface DictionaryService extends RemoteService {
	
	String query(String word);

	// Allows for static access of the service.
	public static class App {
		private static final DictionaryServiceAsync instance = (DictionaryServiceAsync) GWT.create(DictionaryService.class);

		public static DictionaryServiceAsync getInstance() {
			return instance;
		}
	}
}
