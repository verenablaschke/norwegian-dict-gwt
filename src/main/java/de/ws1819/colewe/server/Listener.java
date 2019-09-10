package de.ws1819.colewe.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.SampleSentence;

// @WebListener()
public class Listener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

	private static final Logger logger = Logger.getLogger(Listener.class.getSimpleName());

	public static final String BASE_PATH = "src/main/webapp";
	// GWT looks for these inside the base path:
	public static final String RESOURCES_PATH = "/WEB-INF/";
	private static final String DICTCC_PATH = RESOURCES_PATH + "dict.cc/dict.cc.tsv";
	private static final String LEMMA_PATH = RESOURCES_PATH + "ordbank/lemma.txt";
	private static final String ORDBANK_PATH = RESOURCES_PATH + "ordbank/fullformsliste.txt";
	private static final String LANGENSCHEIDT_PATH = RESOURCES_PATH + "langenscheidt/no-de-dict.txt";
	static final String TATOEBA_PATH = RESOURCES_PATH + "tatoeba/sentence-pairs.ser";
	static final String OPENSUBTITLES_PATH = RESOURCES_PATH + "opensubtitles/no-de.actual.ti.ser";

	// Public constructor is required by servlet spec
	public Listener() {
	}

	// -------------------------------------------------------
	// ServletContextListener implementation
	// -------------------------------------------------------

	// See section 2 of the report.
	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent sce) {

		// Extract the information from the dictionary dumps and inflection
		// lists.
		InputStream dictccInputStream = sce.getServletContext().getResourceAsStream(DICTCC_PATH);
		InputStream lemmaInputStream = sce.getServletContext().getResourceAsStream(LEMMA_PATH);
		InputStream ordbankInputStream = sce.getServletContext().getResourceAsStream(ORDBANK_PATH);
		InputStream langenscheidtInputStream = sce.getServletContext().getResourceAsStream(LANGENSCHEIDT_PATH);
		InputStream tatoebaInputStream = sce.getServletContext().getResourceAsStream(TATOEBA_PATH);
		InputStream openSubtitlesInputStream = sce.getServletContext().getResourceAsStream(OPENSUBTITLES_PATH);

		logger.info("Get entries, collocations, sample sentences.");
		Object[] generatedEntries = ProcessResources.generateEntries(dictccInputStream, lemmaInputStream,
				ordbankInputStream, langenscheidtInputStream);
		ListMultimap<String, Entry> entries = (ListMultimap<String, Entry>) generatedEntries[0];
		HashSet<String> stopwords = (HashSet<String>) generatedEntries[1];
		ListMultimap<String, Entry> prefixes = (ListMultimap<String, Entry>) generatedEntries[2];
		ListMultimap<String, Entry> suffixes = (ListMultimap<String, Entry>) generatedEntries[3];
		HashMap<String, String> sentencePairs = (HashMap<String, String>) generatedEntries[4];

		logger.info("Get sample sentences");
		Object[] entriesWithSentences = ProcessResources.setSampleSentences(tatoebaInputStream, entries, stopwords,
				sentencePairs);
		entries = (ListMultimap<String, Entry>) entriesWithSentences[0];
		ListMultimap<String, SampleSentence> extraSentences = (ListMultimap<String, SampleSentence>) entriesWithSentences[1];

		logger.info("Start reading no-de.actual.ti.final");
		HashMap<String, String> mtEntries = DictionaryReader.readOpenSubtitles(openSubtitlesInputStream);

		// Add the entries to the servlet context.
		sce.getServletContext().setAttribute("entries", entries);
		sce.getServletContext().setAttribute("prefixes", prefixes);
		sce.getServletContext().setAttribute("suffixes", suffixes);
		sce.getServletContext().setAttribute("mtEntries", mtEntries);
		sce.getServletContext().setAttribute("extraSentences", extraSentences);
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
