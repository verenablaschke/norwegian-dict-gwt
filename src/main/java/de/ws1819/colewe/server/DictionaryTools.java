package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;

public class DictionaryTools {

	private static final Logger logger = Logger.getLogger(DictionaryTools.class.getSimpleName());

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
				entries.put(lemma, new Entry(lemma, pos, translation));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from dict.cc data.");
		return entries;
	}

}
