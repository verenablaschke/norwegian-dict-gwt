package de.ws1819.colewe.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;

// @WebListener()
public class Listener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

	private static final Logger logger = Logger.getLogger(Listener.class.getSimpleName());

	// GWT looks for this inside src/main/webapp
	public static final String RESOURCES_PATH = "/WEB-INF/";
	private static final String DICTCC_PATH = RESOURCES_PATH + "dict.cc/dict.cc.tsv";
	private static final String LEMMA_PATH = RESOURCES_PATH + "spraakbanken/lemma.txt";
	private static final String INFL_PATH = RESOURCES_PATH + "spraakbanken/fullformsliste.txt";

	// Public constructor is required by servlet spec
	public Listener() {
	}

	// -------------------------------------------------------
	// ServletContextListener implementation
	// -------------------------------------------------------
	public void contextInitialized(ServletContextEvent sce) {
		/*
		 * This method is called when the servlet context is initialized (when
		 * the Web application is deployed). You can initialize servlet context
		 * related data here.
		 */

		// TODO Handle missing resources. Demo files?

		// Extract the information from the dictionary dumps and inflection
		// lists.
		InputStream dictccInputStream = sce.getServletContext().getResourceAsStream(DICTCC_PATH);
		InputStream lemmaInputStream = sce.getServletContext().getResourceAsStream(LEMMA_PATH);
		InputStream inflInputStream = sce.getServletContext().getResourceAsStream(INFL_PATH);

		logger.info("Start reading dict.cc");
		ListMultimap<String, Entry> dictcc = DictionaryTools.readDictCc(dictccInputStream);
		logger.info("Start reading lemma.txt");
		HashMap<Integer, String> lemmata = DictionaryTools.readLemmaList(lemmaInputStream);
		logger.info("Start reading fullformsliste.txt");
		ListMultimap<String, Entry> fullformsliste = DictionaryTools.readSpraakbanken(lemmata, inflInputStream);

		// Combine the information from both sources by merging the entries when
		// possible.
		HashSet<String> allLemmata = new HashSet<String>(dictcc.keySet());
		allLemmata.addAll(fullformsliste.keySet());
		logger.info(allLemmata.size() + " distinct lemmata.");
		ListMultimap<String, Entry> allEntries = ArrayListMultimap.create();
		for (String lemma : allLemmata) {
			List<Entry> entriesD = dictcc.get(lemma);
			List<Entry> entriesF = fullformsliste.get(lemma);
			if (entriesD == null) {
				allEntries.putAll(lemma, entriesF);
				continue;
			}
			if (entriesF == null) {
				allEntries.putAll(lemma, entriesD);
				continue;
			}
			for (Entry entryD : entriesD) {
				for (Entry entryF : entriesF) {
					if (entryD.getPos().equals(entryF.getPos())) {
						// Same lemma and same POS tag? Merge entries!
						entryD.setInflections(entryF.getInflections());
					} else {
						allEntries.put(lemma, entryF);
					}
					allEntries.put(lemma, entryD);
				}
			}
		}
		logger.info(allEntries.size() + " final entries");
		logger.info(allEntries.get("ord").toString()); // TODO del?

		// Add the entries to the servlet context.
		sce.getServletContext().setAttribute("entries", allEntries);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		/*
		 * This method is invoked when the Servlet Context (the Web application)
		 * is undeployed or Application Server shuts down.
		 */
	}

	// -------------------------------------------------------
	// HttpSessionListener implementation
	// -------------------------------------------------------
	public void sessionCreated(HttpSessionEvent se) {
		/* Session is created. */
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		/* Session is destroyed. */
	}

	// -------------------------------------------------------
	// HttpSessionAttributeListener implementation
	// -------------------------------------------------------

	public void attributeAdded(HttpSessionBindingEvent sbe) {
		/*
		 * This method is called when an attribute is added to a session.
		 */
	}

	public void attributeRemoved(HttpSessionBindingEvent sbe) {
		/*
		 * This method is called when an attribute is removed from a session.
		 */
	}

	public void attributeReplaced(HttpSessionBindingEvent sbe) {
		/*
		 * This method is invoked when an attibute is replaced in a session.
		 */
	}
}
