package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;

public class DictionaryTools {

	private static final Logger logger = Logger.getLogger(DictionaryTools.class.getSimpleName());

	// private static final Pattern pattern =
	// Pattern.compile("(\\s\\+{.*\\}\\s?)?(\\s+<.*>\\s?)?(\\s\\[.*\\]\\s?)?$");

	public static ListMultimap<String, Entry> readDictCc(InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();

		// Convert the dict.cc dump into a collection of dictionary entries.
		String line = null;
		String[] fields = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					// Comment in TSV file.
					continue;
				}
				fields = line.split("\\t");
				if (fields.length < 2) {
					// Empty/faulty line.
					continue;
				}
				String lemma = fields[0].trim();
				// When looking up e.g. 'student', we want to get both 'Student'
				// and 'Studentin' without having to specify 'student [mannlig]'
				// or 'student [kvinnelig]'.
				lemma = lemma.replaceAll(" \\[kvinnelig\\]", "");
				lemma = lemma.replaceAll(" \\[mannlig\\]", "");

				// Matcher matcher = pattern.matcher(lemma);
				String comment = "";
				// There should be 0 or 1 match(es) in total.
				// while (matcher.find()){
				// // The regex already includes the end-of-string.
				// comment = lemma.substring(matcher.end());
				// lemma = lemma.substring(0, matcher.end());
				// }
				String translation = fields[1].trim();

				// If available, get POS tag.
				String pos = null;
				if (fields.length >= 3) {
					pos = fields[2].trim();
					if ("verb".equals(pos) && lemma.startsWith("å ")) {
						// Remove the infinitive particle.
						lemma = lemma.substring(2);
					}

					// If available, get additional comments.
					if (fields.length >= 4) {
						translation += " " + fields[3].trim();
					}
				}

				// Save the entry.
				entries.put(lemma, new Entry(lemma, comment, pos, translation));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from dict.cc data.");
		return entries;
	}

	// Convert Språkbanken's lemma list into a map from lemma ID numbers to
	// strings.
	public static HashMap<Integer, String> readLemmaList(InputStream stream) {
		HashMap<Integer, String> lemmata = new HashMap<Integer, String>();

		String line = null;
		String[] fields = null;
		int id = -1;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null) {
				/*
				 * TSV structure: LOEPENR LEMMA_ID GRUNNFORM 'BM_ORDBOK' (line
				 * number, lemma id, lemma, bokmål dictionary)
				 */
				line = line.trim();
				fields = line.split("\\t");
				if (fields.length < 4) {
					System.err.println("Line too short: " + line);
					continue;
				}

				// Lemma ID
				try {
					id = Integer.parseInt(fields[1].trim());
				} catch (NumberFormatException e) {
					if (line.equals("LOEPENR	LEMMA_ID	GRUNNFORM	'BM_ORDBOK'")) {
						// File header
						continue;
					}
					System.err.println("Lemma ID is not an integer: " + line);
					continue;
				}

				// Save the entry.
				lemmata.put(id, fields[2].trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read " + lemmata.size() + " lemmata from Språkbanken's lemma list.");
		logger.info(lemmata.get(50065)); // TODO delete
		return lemmata;
	}

	public static ListMultimap<SimpleEntry<Integer, String>, SimpleEntry<String, String>> readSpraakbanken(
			HashMap<Integer, String> lemmata, InputStream stream) {
		ListMultimap<SimpleEntry<Integer, String>, SimpleEntry<String, String>> inflections = ArrayListMultimap.create();

		String line = null;
		String[] fields = null;
		int id = -1;
		String lemma = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null) {
				/*
				 * TSV structure: LOEPENR LEMMA_ID OPPSLAG TAG PARADIGME_ID
				 * BOY_NUMMER FRADATO TILDATO NORMERING (line number, lemma id,
				 * word, POS tag, inflection information, inflection number,
				 * from date, to date, normalization) TODO check in
				 * documentation
				 */
				line = line.trim();
				fields = line.split("\\t");
				if (fields.length < 9) {
					System.err.println("Line too short: " + line);
					continue;
				}

				// Get the lemma.
				try {
					id = Integer.parseInt(fields[1].trim());
				} catch (NumberFormatException e) {
					if (line.startsWith("LOEPENR")) {
						// File header
						continue;
					}
					System.err.println("Lemma ID is not an integer: " + line);
					continue;
				}
				lemma = lemmata.get(id);
				if (lemma == null) {
					System.err.println("Lemma ID " + id + " does not have a string form. Line: " + line);
					continue;
				}

				// Save lemma ID, inflection information and inflected form.
				inflections.put(new SimpleEntry<Integer, String>(id, lemma),
						new SimpleEntry<String, String>(fields[3].trim(), fields[2].trim()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read " + inflections.size() + " inflections for " + inflections.keySet().size()
				+ " lemmata from Språkbanken's fullformsliste.");
		logger.info(inflections.get(new SimpleEntry<Integer, String>(50065, "ord")).toString()); // TODO delete
		return inflections;
	}

}
