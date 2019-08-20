package de.ws1819.colewe.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.SetMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;
import de.ws1819.colewe.shared.WordForm;

// @WebListener()
public class Listener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

	private static final Logger logger = Logger.getLogger(Listener.class.getSimpleName());

	// GWT looks for this inside src/main/webapp
	public static final String RESOURCES_PATH = "/WEB-INF/";
	private static final String DICTCC_PATH = RESOURCES_PATH + "dict.cc/dict.cc.tsv";
	private static final String LEMMA_PATH = RESOURCES_PATH + "spraakbanken/lemma.txt";
	private static final String INFL_PATH = RESOURCES_PATH + "spraakbanken/fullformsliste.txt";
	private static final String WOERTERBUCH_PATH = RESOURCES_PATH + "woerterbuch/no-de-dict.txt";

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
		InputStream woerterbuchInputStream = sce.getServletContext().getResourceAsStream(WOERTERBUCH_PATH);

		logger.info("Start reading dict.cc");
		ListMultimap<String, Entry> dictcc = DictionaryReader.readDictCc(dictccInputStream);
		logger.info("Start reading lemma.txt");
		HashMap<Integer, String> lemmata = DictionaryReader.readLemmaList(lemmaInputStream);
		logger.info("Start reading fullformsliste.txt");
		ListMultimap<String, Entry> fullformsliste = DictionaryReader.readSpraakbanken(lemmata, inflInputStream);
		logger.info("Start reading no-de-dict.txt");
		ListMultimap<String, Entry> woerterbuch = DictionaryReader.readWoerterbuch(woerterbuchInputStream);

		// Combine the information from both sources by merging the entries when
		// possible.
		HashSet<String> allLemmata = new HashSet<String>(dictcc.keySet());
		allLemmata.addAll(fullformsliste.keySet());
		allLemmata.addAll(woerterbuch.keySet());
		logger.info(allLemmata.size() + " distinct lemmata.");
		// Map from all inflected versions of the word to the entries.
		SetMultimap<String, Entry> allEntries = HashMultimap.create();
		for (String lemma : allLemmata) {
			List<Entry> entriesD = dictcc.get(lemma);
			List<Entry> entriesF = fullformsliste.get(lemma);
			List<Entry> entriesW = woerterbuch.get(lemma);

			// TODO proper merging with entriesW

			// Map lemmas and inflected forms from the NO>DE dictionary
			// to Entry objects.
			if (entriesW != null) {
				for (Entry entryW : entriesW) {
					addInfMarker(entryW);
					allEntries.put(lemma, entryW);
					for (WordForm wordForm : entryW.getInflections().values()) {
						allEntries.put(wordForm.getForm(), entryW);
					}
				}
			}

			if (entriesD == null) {
				for (Entry entryF : entriesF) {
					addInfMarker(entryF);
					for (WordForm wordForm : entryF.getInflections().values()) {
						allEntries.put(wordForm.getForm(), entryF);
					}
				}
				continue;
			}
			if (entriesF == null) {
				for (Entry entryD : entriesD) {
					addInfMarker(entryD);
					allEntries.put(lemma, entryD);
				}
				continue;
			}
			for (Entry entryD : entriesD) {
				addInfMarker(entryD);
				for (Entry entryF : entriesF) {
					addInfMarker(entryF);
					if (entryD.getPos().equals(entryF.getPos())) {
						// Same lemma and same POS tag? Merge entries!
						entryD.setInflections(entryF.getInflections());
						for (WordForm wordForm : entryF.getInflections().values()) {
							allEntries.put(wordForm.getForm(), entryD);
						}
					} else {
						allEntries.put(lemma, entryD);
						for (WordForm wordForm : entryF.getInflections().values()) {
							allEntries.put(wordForm.getForm(), entryF);
						}
					}
				}
			}
		}
		logger.info(allEntries.get("ord").toString()); // TODO del?

		// Add the entries to the servlet context.
		sce.getServletContext().setAttribute("entries", allEntries);
	}

	private void addInfMarker(Entry entry) {
		// If appropriate, add the infinitive marker.
		if (entry.getPos().equals(Pos.VERB) && !entry.getLemma().getForm().startsWith("å ")) {
			entry.setLemma(new WordForm("å " + entry.getLemma().getForm(), entry.getLemma().getPronunciation()));
		}
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
