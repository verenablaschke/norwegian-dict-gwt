package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;
import de.ws1819.colewe.shared.WordForm;

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

				// Find and remove comments.
				String[] lemmaAndCommentsNO = Tools.extractDictCCComments(lemma);
				lemma = lemmaAndCommentsNO[0];

				// Get the translational equivalent and extract comments.
				String[] lemmaAndCommentsDE = Tools.extractDictCCComments(fields[1].trim());

				// If available, get POS tag.
				String posTags[] = null;
				if (fields.length >= 3) {
					posTags = fields[2].trim().split("\\s+");

					if (fields[2].contains("verb") && lemma.startsWith("å ")) {
						// Remove the infinitive particle.
						lemma = lemma.substring(2);
					}

					// If available, get additional comments.
					// TODO Where to put these? Issue #16
					// if (fields.length >= 4) {
					// translation += " " + fields[3].trim();
					// }
				} else {
					posTags = new String[] { null };
				}

				// Save the entry.
				for (String pos : posTags) {
					Pos posTag = Tools.string2Pos(pos);

					entries.put(lemma,
							new Entry(new WordForm(lemma), posTag, lemmaAndCommentsDE[0], lemmaAndCommentsNO[1],
									lemmaAndCommentsNO[2], lemmaAndCommentsNO[3], lemmaAndCommentsDE[1],
									lemmaAndCommentsDE[2], lemmaAndCommentsDE[3]));
				}

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
		return lemmata;
	}

	public static ListMultimap<String, Entry> readSpraakbanken(HashMap<Integer, String> lemmata, InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		HashSet<String> tags = new HashSet<>(); // TODO del

		String line = null;
		String[] fields = null;
		int id = -1;
		String lemma = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null) {
				/*
				 * TSV structure: LOEPENR LEMMA_ID OPPSLAG TAG PARADIGME_ID
				 * BOY_NUMMER FRADATO TILDATO NORMERING (line number, lemma id,
				 * word, POS tag + inflection information, paradigm ID,
				 * inflection number, from date, to date, normalization) TODO
				 * check in documentation
				 */
				line = line.trim();
				fields = line.split("\\t");
				if (fields.length < 9) {
					logger.warning("Line too short: " + line);
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
					logger.warning("Lemma ID is not an integer: " + line);
					continue;
				}
				lemma = lemmata.get(id);
				if (lemma == null) {
					logger.fine("Lemma ID " + id + " does not have a string form. Line: " + line);
					continue;
				}

				String infl = fields[3].trim();
				String posString = infl.split("\\s+")[0];
				Pos pos = Tools.string2Pos(posString);
				if (posString.equals("adjektiv") || posString.equals("forsk") || posString.equals("inf")) {
					// Duplicate entries.
					continue;
				}
				if (!posString.contains("+") && !posString.contains("-")) {
					// multi-word phrases
					tags.add(posString);
				}
				// If the POS tag is OTHER, we're either dealing with an
				// abbreviation or a multi-word phrase.
				String inflForm = fields[2].trim();

				// Save lemma ID, inflection information and inflected form.
				List<Entry> entryList = entries.get(lemma);
				boolean addedInfl = false;
				if (entryList != null) {
					for (Entry entry : entryList) {
						if (id == entry.getLemmaID()) {
							entry.addInflection(infl, new WordForm(inflForm));
							addedInfl = true;
							break;
						}
					}
				}
				if (entryList == null || !addedInfl) {
					entries.put(lemma, new Entry(new WordForm(lemma), pos, infl, new WordForm(inflForm), id));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read " + entries.size() + " inflections for " + entries.keySet().size()
				+ " lemmata from Språkbanken's fullformsliste.");
		logger.info(tags.stream().collect(Collectors.toCollection(TreeSet::new)).toString());

		for (java.util.Map.Entry<String, WordForm> infl : entries.get("bli").get(0).getInflections().entrySet()) {
			logger.info(infl.getValue().getForm() + " : " + infl.getKey());
		}
		return entries;
	}

	public static ListMultimap<String, Entry> readWoerterbuch(InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		HashSet<String> tags = new HashSet<>(); // TODO del
		HashSet<String> lower = new HashSet<>(); // TODO del
		HashSet<String> info = new HashSet<>(); // TODO del

		String line = null;
		String[] fields = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				fields = line.split("\\s+");
				/*
				 * Format: lemma#[pronunciation] POS one_or_more_translations
				 */
				if (fields.length < 3) {
					// Empty/faulty line.
					continue;
				}

				String[] posAndInfl = Tools.parsePOS(fields[1]);
				String posString = posAndInfl[0];
				if (posString.equals("VTT")) {
					// Duplicate entry.
					continue;
				}
				String grammarNO = posAndInfl[1];
				String extra = posAndInfl[2];
				tags.add(posString);
				lower.add(grammarNO);
				info.add(extra);
				Pos pos = Tools.string2Pos(posString);

				String[] inflections = fields[0].trim().split(",");
				WordForm lemma = null;
				HashMap<String, WordForm> infl = new HashMap<String, WordForm>();
				for (int i = 0; i < inflections.length; i++) {
					String[] wordPron = inflections[i].trim().split("#");
					String word = wordPron[0].replace("_", " ");
					String pron = null;
					if (wordPron.length > 1) {
						pron = wordPron[1].trim();
						// Remove [ and ] from the transcription.
						pron = pron.substring(1, pron.length() - 1);
						pron = Tools.xsampaToIPA(pron);
					}
					if (i == 0) {
						lemma = new WordForm(word, pron);
					} else {
						if (word.startsWith("-")) {
							// TODO #17
						}
						infl.put(((Integer) i).toString(), new WordForm(word, pron)); // TODO
																						// key
					}
				}

				// Major meaning blocks in polysemous entries are separated by
				// //.
				// TODO look up the proper terminology and rename the vars
				String[] transl = fields[2].split("//");
				for (int i = 0; i < transl.length; i++) {
					// German synonyms are separated by /.
					String[] translationsRaw = transl[i].split("/");
					HashSet<String> translations = new HashSet<String>();
					// TODO or make this a list instead? canonical order?
					String usageDE = "";
					for (int j = 0; j < translationsRaw.length; j++) {
						//
						String[] wordAndComment = Tools.match(Tools.patternSquareWithoutWS, translationsRaw[j]);
						if (!wordAndComment[1].isEmpty()) {
							// There is no more than one comment per meaning.
							usageDE = wordAndComment[1].replace("_", " ");
						}
						/*
						 * Some of the translational equivalents include domain
						 * // information. Then, the entry in the txt file looks
						 * like this: "anløp#["Anl2:p] Nn
						 * Anlaufen[eines_Hafens]/Anlaufen" with the German
						 * translational equivalent repeated.
						 */
						translations.add(wordAndComment[0].replace("_", " "));
					}
					entries.put(lemma.getForm(), new Entry(lemma, pos, infl, translations, grammarNO, usageDE));
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from the NO>DE dictionary.");
		logger.info(tags.stream().collect(Collectors.toCollection(TreeSet::new)).toString());
		logger.info(lower.stream().collect(Collectors.toCollection(TreeSet::new)).toString());
		logger.info(info.stream().collect(Collectors.toCollection(TreeSet::new)).toString());

		logger.info(entries.get("bli").toString());
		return entries;
	}

}
