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

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

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
	@SuppressWarnings("unchecked")
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
		Object[] dictccResults = DictionaryReader.readDictCc(dictccInputStream);
		ListMultimap<String, Entry> dictcc = (ListMultimap<String, Entry>) dictccResults[0];
		HashSet<String> stopwords = (HashSet<String>) dictccResults[1];
		logger.info("Start reading lemma.txt");
		HashMap<Integer, String> lemmata = DictionaryReader.readLemmaList(lemmaInputStream);
		logger.info("Start reading fullformsliste.txt");
		Object[] fullformslisteResults = DictionaryReader.readSpraakbanken(lemmata, inflInputStream);
		ListMultimap<String, Entry> fullformsliste = (ListMultimap<String, Entry>) fullformslisteResults[0];
		stopwords.addAll((HashSet<String>) fullformslisteResults[1]);
		logger.info("Start reading no-de-dict.txt");
		Object[] woerterbuchResults = DictionaryReader.readWoerterbuch(woerterbuchInputStream);
		ListMultimap<String, Entry> woerterbuch = (ListMultimap<String, Entry>) woerterbuchResults[0];
		stopwords.addAll((HashSet<String>) woerterbuchResults[1]);

		// Combine the information from both sources by merging the entries when
		// possible.
		HashSet<String> allLemmata = new HashSet<String>(dictcc.keySet());
		allLemmata.addAll(fullformsliste.keySet());
		allLemmata.addAll(woerterbuch.keySet());
		logger.info(allLemmata.size() + " distinct lemmata.");

		// Map from all inflected versions of the word to the entries.
		// Using list multimaps instead of set multimaps since they require less
		// memory.
		ListMultimap<String, Entry> allEntries = ArrayListMultimap.create();
		ListMultimap<String, Entry> prefixes = ArrayListMultimap.create();
		ListMultimap<String, Entry> suffixes = ArrayListMultimap.create();
		HashMultimap<String, String> collocations = HashMultimap.create();
		for (String lemma : allLemmata) {
			ListMultimap<String, Entry> entries = ArrayListMultimap.create();

			// Map lemmas and inflected forms from the NO>DE dictionary
			// to Entry objects.
			List<Entry> entriesWoerterbuch = woerterbuch.get(lemma);
			if (entriesWoerterbuch != null) {
				for (Entry entryW : entriesWoerterbuch) {
					addInfMarker(entryW);
					addEntry(entryW, lemma, entries, collocations, stopwords, prefixes, suffixes, false);
					for (String wordForm : entryW.getInflections()) {
						addEntry(entryW, wordForm, entries, collocations, stopwords, prefixes, suffixes, false);
					}
				}
			}

			// Map DictCC lemmas to Entry objects.
			List<Entry> entriesDictCc = dictcc.get(lemma);
			if (entriesDictCc != null) {
				for (Entry entryD : entriesDictCc) {
					addInfMarker(entryD);
					addEntry(entryD, lemma, entries, collocations, stopwords, prefixes, suffixes, false);
					for (String abbr : entryD.getAbbr()) {
						addEntry(entryD, abbr, entries, collocations, stopwords, prefixes, suffixes, false);
					}
				}
			}

			List<Entry> entriesFullformsliste = fullformsliste.get(lemma);
			if (entriesFullformsliste != null) {
				for (Entry entryF : entriesFullformsliste) {
					addInfMarker(entryF);
					addEntry(entryF, lemma, entries, collocations, stopwords, prefixes, suffixes, true);
					for (String wordForm : entryF.getInflections()) {
						addEntry(entryF, wordForm, entries, collocations, stopwords, prefixes, suffixes, true);
					}
				}
			}

			allEntries.putAll(entries);
		}

		// Add the entries to the servlet context.
		sce.getServletContext().setAttribute("entries", allEntries);
		sce.getServletContext().setAttribute("prefixes", prefixes);
		sce.getServletContext().setAttribute("suffixes", suffixes);
		sce.getServletContext().setAttribute("collocations", collocations);
	}

	private void addEntry(Entry entry, String wordForm, ListMultimap<String, Entry> entries,
			HashMultimap<String, String> collocations, HashSet<String> stopwords, ListMultimap<String, Entry> prefixes,
			ListMultimap<String, Entry> suffixes, boolean fullformsliste) {
		switch (entry.getPos()) {
		case PFX:
			addEntry(entry, wordForm, prefixes, null, null, true, fullformsliste);
			if (!fullformsliste) {
				addEntry(entry, wordForm, entries, collocations, stopwords, true, fullformsliste);
			}
			break;
		case SFX:
			addEntry(entry, wordForm, suffixes, null, null, true, fullformsliste);
			if (!fullformsliste) {
				addEntry(entry, wordForm, entries, collocations, stopwords, true, fullformsliste);
			}
			break;
		default:
			addEntry(entry, wordForm, entries, collocations, stopwords, false, fullformsliste);
		}
	}

	private void addEntry(Entry entry, String wordForm, ListMultimap<String, Entry> entries,
			HashMultimap<String, String> collocations, HashSet<String> stopwords, boolean affixes,
			boolean fullformsliste) {
		if (entry.getPos().equals(Pos.VERB)) {
			// Remove 'noen' ('somebody') and 'noe' ('something').
			wordForm = wordForm.replace(" noen/noe", "");
			wordForm = wordForm.replace(" noe/noen", "");
			wordForm = wordForm.replace(" noens", "");
			wordForm = wordForm.replace(" noen", "");
			wordForm = wordForm.replace(" noe", "");
		}
		wordForm = wordForm.replaceAll("[®&:§–@\"\\{\\}\\!\\?\\.,%]+", "").trim();
		wordForm = wordForm.toLowerCase().replaceAll("\\s+", " ");
		if (affixes) {
			wordForm = wordForm.replace("-", "");
		} else {
			// Extract collocations.
			String[] components = wordForm.split(" ");
			if (components.length > 1) {
				for (int i = 0; i < components.length; i++) {
					if (stopwords.contains(components[i])) {
						continue;
					}
					collocations.put(components[i], wordForm);
				}
			}
		}

		// Noun forms used in compounds.
		if (entry.getPos().equals(Pos.NOUN) && wordForm.equals(entry.getLemma().getForm())) {
			if (!wordForm.endsWith("s")) {
				addEntry(entry, wordForm + "s", entries, collocations, stopwords, affixes, fullformsliste);
			}
			if (!Tools.isVowel(wordForm.charAt(wordForm.length() - 1))) {
				addEntry(entry, wordForm + "e", entries, collocations, stopwords, affixes, fullformsliste);
			}
		}

		// Try to merge entries.
		for (Entry existingEntry : entries.values()) {
			if (existingEntry.merge(entry)) {
				// This would have been nicer with a SetMultimap, but the memory
				// overhead issues are too big a downside.
				List<Entry> entryList = entries.get(wordForm);
				if (entryList == null || entryList.isEmpty()) {
					entries.put(wordForm, existingEntry);
					return;
				}

				return;
			}
		}

		// Don't add entries without translation information,
		// we're already struggling with memory as is.
		if (fullformsliste && !affixes) {
			return;
		}

		// Couldn't merge the entry with an existing one, add to collection:
		entries.put(wordForm, entry);
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
