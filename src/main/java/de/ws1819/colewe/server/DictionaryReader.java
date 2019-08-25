package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;
import de.ws1819.colewe.shared.TranslationalEquivalent;
import de.ws1819.colewe.shared.WordForm;

public class DictionaryReader {

	private static final Logger logger = Logger.getLogger(DictionaryReader.class.getSimpleName());

	@SuppressWarnings("unchecked")
	public static ListMultimap<String, Entry> readDictCc(InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		Set<String> grammarNOSet = new HashSet<>(); // TODO del

		// Convert the dict.cc dump into a collection of dictionary entries.
		String line = null;
		String[] fields = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
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
				Object[] lemmaAndCommentsNO = Tools.extractDictCCComments(lemma);
				lemma = (String) lemmaAndCommentsNO[0];

				// Get the translational equivalent and extract comments.
				Object[] lemmaAndCommentsDE = Tools.extractDictCCComments(fields[1].trim());

				ArrayList<String> grammarNO = new ArrayList<>();
				for (String gram : (ArrayList<String>) lemmaAndCommentsNO[1]) {
					for (String s : gram.split(",\\s*|\\s+|/")) {
						grammarNO.add(s.replace("[", "").replace("]", ""));
					}
				}
				grammarNOSet.addAll(grammarNO);
				// grammarDESet.add(lemmaAndCommentsDE[1]);

				ArrayList<String> usageNO = new ArrayList<String>();
				// If available, get POS tag.
				String posTags[] = null;
				if (fields.length >= 3) {
					posTags = fields[2].trim().split("\\s+");

					if (fields[2].contains("verb") && lemma.startsWith("å ")) {
						// Remove the infinitive particle.
						lemma = lemma.substring(2);
					}

					// Ignore potential domain information (fields[3]), since it
					// does not add helpful information, only visual noise.
				} else {
					posTags = new String[] { null };
				}

				// Save the entry.
				for (String pos : posTags) {
					Pos posTag = Tools.string2Pos(pos);

					entries.put(lemma,
							new Entry(new WordForm(lemma), posTag,
									new TranslationalEquivalent((String) lemmaAndCommentsDE[0],
											(ArrayList<String>) lemmaAndCommentsDE[1],
											(ArrayList<String>) lemmaAndCommentsDE[2],
											(ArrayList<String>) lemmaAndCommentsDE[3]),
									grammarNO, usageNO, (ArrayList<String>) lemmaAndCommentsNO[3]));
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from dict.cc data.");
		logger.info("Dict.cc grammarNO");
		ArrayList<String> infoList = new ArrayList<>(grammarNOSet);
		infoList.sort(String::compareToIgnoreCase);
		logger.info(infoList.toString());
		return entries;
	}

	// Convert Språkbanken's lemma list into a map from lemma ID numbers to
	// strings.
	public static HashMap<Integer, String> readLemmaList(InputStream stream) {
		HashMap<Integer, String> lemmata = new HashMap<Integer, String>();

		String line = null;
		String[] fields = null;
		int id = -1;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
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

		String line = null;
		String[] fields = null;
		int id = -1;
		String lemma = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
			while ((line = br.readLine()) != null) {
				/*
				 * TSV structure: LOEPENR LEMMA_ID OPPSLAG TAG PARADIGME_ID
				 * BOY_NUMMER FRADATO TILDATO NORMERING (line number, lemma id,
				 * word, POS tag + inflection information, paradigm ID,
				 * inflection number, from date, to date, normalization)
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
				if (posString.contains("+") || posString.contains("-")) {
					// TODO mention in documentation?
					// multi-word phrases
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
							if (entry.getPos() == Pos.ADJ && pos == Pos.VERB) {
								// Verb entries also contain participles that
								// are tagged as adjectives.
								entry.setPos(Pos.VERB);
							}
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

		for (java.util.Map.Entry<String, WordForm> infl : entries.get("bli").get(0).getInflections().entrySet()) {
			logger.info(infl.getValue().getForm() + " : " + infl.getKey());
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	public static ListMultimap<String, Entry> readWoerterbuch(InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		HashSet<String> info = new HashSet<>(); // TODO del
		HashSet<String> usageDESet = new HashSet<>(); // TODO del

		String line = null;
		String[] fields = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				fields = line.split("\\s+");
				// Format: lemma#[pronunciation] POS one_or_more_translations
				if (fields.length < 3) {
					// Empty/faulty line.
					continue;
				}

				Object[] posAndInfl = Tools.parsePOS(fields[1]);
				Pos pos = (Pos) posAndInfl[0];
				ArrayList<String> grammarNO = (ArrayList<String>) posAndInfl[1];
				ArrayList<String> usageNO = (ArrayList<String>) posAndInfl[2];
				if (grammarNO.contains("INFLECTED")) {
					// We don't need entries for inflected adjectives/verbs
					// since we show the lemma instead.
					continue;
				}
				info.addAll(grammarNO);

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
				// double slashes.
				// TODO look up the proper terminology and rename the vars
				String[] transl = fields[2].split("//");
				ArrayList<TranslationalEquivalent> translations = new ArrayList<>();
				for (int i = 0; i < transl.length; i++) {
					// German synonyms are separated by a single slash.
					String[] translRaw = transl[i].split("/");
					// A list instead of a set so that we can keep the order the
					// dictionary editors deemed best.
					ArrayList<String> translElements = new ArrayList<>();
					ArrayList<String> usageDE = new ArrayList<>();
					for (int j = 0; j < translRaw.length; j++) {
						//
						Object[] wordAndComment = Tools.match(Tools.patternSquareWithoutWS, translRaw[j]);
						for (String usage : (ArrayList<String>) wordAndComment[1]) {
							usageDE.add(usage.replace("_", " "));
							usageDESet.add(usage.replace("_", " "));
						}
						// Some of the translational equivalents include domain
						// information. Then, the entry in the txt file looks
						// like this:
						// "anløp#["Anl2:p] Nn Anlaufen[eines_Hafens]/Anlaufen"
						// with the German translational equivalent repeated.
						String translation = ((String) wordAndComment[0]).replace("_", " ");
						if (!translElements.contains(translation)) {
							translElements.add(translation);
						}
					}
					translations.add(new TranslationalEquivalent(translElements, usageDE));
				}
				entries.put(lemma.getForm(), new Entry(lemma, pos, infl, translations, grammarNO, usageNO));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from the NO>DE dictionary.");
		ArrayList<String> infoList = new ArrayList<>(info);
		infoList.sort(String::compareToIgnoreCase);
		logger.info(infoList.toString());
		logger.info("Usage no-de");
		infoList = new ArrayList<>(usageDESet);
		infoList.sort(String::compareToIgnoreCase);
		logger.info(infoList.toString());
		logger.info(entries.get("bli").toString());
		return entries;
	}

}
