package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.Pos;

public class DictionaryTools {

	private static final Logger logger = Logger.getLogger(DictionaryTools.class.getSimpleName());

	private static final Pattern patternCurly = Pattern.compile("\\s\\{.*?\\}");
	private static final Pattern patternSquare = Pattern.compile("\\s\\[.*?\\]");
	private static final Pattern patternTriangle = Pattern.compile("\\s\\<.*?\\>");

	public static ListMultimap<String, Entry> readDictCc(InputStream stream) {
		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		HashSet<String> tags = new HashSet<>(); // TODO del

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
				String[] lemmaAndCommentsNO = extractDictCCComments(lemma);
				lemma = lemmaAndCommentsNO[0];

				// Get the translational equivalent and extract comments.
				String[] lemmaAndCommentsDE = extractDictCCComments(fields[1].trim());

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
					entries.put(lemma,
							new Entry(lemma, string2Pos(pos), lemmaAndCommentsDE[0], lemmaAndCommentsNO[1],
									lemmaAndCommentsNO[2], lemmaAndCommentsNO[3], lemmaAndCommentsDE[1],
									lemmaAndCommentsDE[2], lemmaAndCommentsDE[3]));
					if (pos != null) {
						tags.add(pos);
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read (and generated) " + entries.size() + " entries from dict.cc data.");
		logger.info(tags.stream().collect(Collectors.toCollection(TreeSet::new)).toString());
		return entries;
	}

	private static String[] extractDictCCComments(String lemma) {
		String[] grammar = match(patternCurly, lemma);
		lemma = grammar[0];
		String[] usage = match(patternSquare, lemma);
		lemma = usage[0];
		String[] abbr = match(patternTriangle, lemma);
		// abbr[0] is the lemma
		return new String[] { abbr[0], grammar[1], usage[1], abbr[1] };
	}

	private static String[] match(Pattern pattern, String lemma) {
		Matcher matcher = pattern.matcher(lemma);
		String comment = "";
		String match;
		Stack<Integer> matches = new Stack<>();
		while (matcher.find()) {
			match = matcher.group().trim();
			// Remove the brackets around the comment.
			comment += match.substring(1, match.length() - 1) + ", ";
			matches.push(matcher.end());
			matches.push(matcher.start());
		}
		// Remove trailing ', '.
		if (comment.length() > 2) {
			comment = comment.substring(0, comment.length() - 2);
		}
		while (!matches.isEmpty()) {
			lemma = lemma.substring(0, matches.pop()) + lemma.substring(matches.pop());
		}
		return new String[] { lemma.trim(), comment.trim() };
	}

	private static Pos string2Pos(String s) {
		if (s == null) {
			return Pos.NULL;
		}
		switch (s.toLowerCase()) {
		case "adj":
			return Pos.ADJ;
		case "adv":
			return Pos.ADV;
		case "conj":
		case "konj":
			return Pos.CONJ;
		case "noun":
		case "subst":
			return Pos.NOUN;
		case "prep":
			return Pos.PREP;
		case "verb":
			return Pos.VERB;
		}
		return Pos.OTHER;
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

	public static ListMultimap<String, Entry> readSpraakbanken(HashMap<Integer, String> lemmata, InputStream stream) {
		HashMap<Integer, Entry> inflections = new HashMap<>();

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

				String infl = fields[3].trim();
				Pos pos = string2Pos(infl.split("\\s+")[0]);
				String inflForm = fields[2].trim();

				// Save lemma ID, inflection information and inflected form.
				Entry entry = inflections.get(id);
				if (entry == null) {
					entry = new Entry(lemma, pos, infl, inflForm);
				} else {
					entry.addInflection(infl, inflForm);
				}
				inflections.put(id, entry);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Read " + inflections.size() + " inflections for " + inflections.keySet().size()
				+ " lemmata from Språkbanken's fullformsliste.");
		logger.info(inflections.get(50065).toString()); // TODO
														// delete

		ListMultimap<String, Entry> entries = ArrayListMultimap.create();
		for (java.util.Map.Entry<Integer, Entry> entry : inflections.entrySet()) {
			entries.put(lemmata.get(entry.getKey()), entry.getValue());
		}
		return entries;
	}

}
