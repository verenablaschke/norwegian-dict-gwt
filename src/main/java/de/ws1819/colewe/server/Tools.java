package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ws1819.colewe.shared.Pos;

/**
 * Helper methods for the {@link de.ws1819.colewe.server.DictionaryReader} class
 * so it does not become too cluttered.
 * 
 * @author Verena Blaschke
 */
public class Tools {

	private static final Pattern patternCurly = Pattern.compile("\\s\\{.*?\\}");
	private static final Pattern patternSquare = Pattern.compile("\\s\\[.*?\\]");
	static final Pattern patternSquareWithoutWS = Pattern.compile("\\[.*?\\]");
	private static final Pattern patternTriangle = Pattern.compile("\\s\\<.*?\\>");

	// X-SAMPA to IPA. Only includes characters actually used in the input data.
	private static final Map<String, String> one2one = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("A", "ɑ");
			put("C", "ç");
			put("E", "ɛ");
			put("I", "ɪ");
			put("N", "ŋ");
			put("O", "ɔ");
			put("S", "ʃ");
			put("U", "ʊ");
			put("Y", "ʏ");
			put("Z", "ʒ");
			put("%", "ˌ");
			put(":", "ː");
			put("@", "ə");
			put("\\{", "æ");
			put("\\}", "ʉ");
			put("2", "ø");
			put("9", "œ");
			put("\\?", "ʔ");
			// Custom: word break
			put("_", " ");
			// Custom: combination of primary stress and tone information
			put("'", "¹");
			put("\"", "²");
			// Occur as typos for their lowercase counterparts:
			put("D", "d");
			put("K", "k");
			put("P", "p");
		}
	};

	/**
	 * Transforms X-SAMPA to IPA.
	 * 
	 * @param pron
	 *            an X-SAMPA string
	 * @return the IPA version
	 */
	static String xsampaToIPA(String pron) {
		pron = pron.replaceAll("__", " "); // Custom: word break
		for (Entry<String, String> entry : one2one.entrySet()) {
			pron = pron.replaceAll(entry.getKey(), entry.getValue());
		}
		return "/" + pron + "/";
	}

	/**
	 * Transforms a POS tag string following the tag sets used in Langenscheidt,
	 * dict.cc and Ordbank into a {@link de.ws1819.colewe.shared.Pos} tag.
	 * 
	 * @param s
	 *            a POS tag string
	 * @return the Pos instance
	 */
	static Pos string2Pos(String s) {
		if (s == null) {
			return Pos.NULL;
		}
		switch (s.toLowerCase()) {
		case "":
			return Pos.NULL;
		case "adj":
		case "a":
		case "pres-p": // present participle
		case "past-p": // past participle
			return Pos.ADJ;
		case "adv":
		case "fadv": // question word adverb
		case "radv": // response adverb
			return Pos.ADV;
		case "conj":
		case "konj":
		case "cnj":
		case "sbu": // subordinating conjunction
		case "sbj": // subordinating conjunction
			return Pos.CONJ;
		case "det":
		case "fdet": // question word determiner
		case "num":
		case "fnum": // question word numeral ("how many")
			return Pos.DET;
		case "interj":
		case "itj":
		case "fitj": // question interjection
			return Pos.ITJ;
		case "noun":
		case "subst":
		case "n":
		case "ne": // named entity, here: languages/countries/etc.
		case "nm": // typo ('n')
			return Pos.NOUN;
		case "pref":
		case "prefix":
		case "pfx":
			return Pos.PFX;
		case "prep":
		case "prp":
		case "ccp": // circumposition
			return Pos.PREP;
		case "pron":
		case "prn":
		case "fprn": // question word pronoun
		case "rprn": // response pronoun
			return Pos.PRON;
		case "s":
		case "fs": // question
			return Pos.SENT;
		case "suffix":
		case "sfx":
			return Pos.SFX;
		case "verb":
		case "v":
		case "vi":
		case "vt":
		case "vtt":
		case "vr": // typo ('vt')
			return Pos.VERB;
		}
		return Pos.NULL;
	}

	/**
	 * Parses grammatical information given in Langenscheidt entries.
	 * 
	 * @param s
	 *            A string in the form POSTAGnoungender[extra=info], where only
	 *            the POS tag is required
	 * @return an Object array containing a {@link de.ws1819.colewe.shared.Pos}
	 *         tag, an ArrayList<String> containing grammatical information, and
	 *         an ArrayList<String> containing usage information
	 */
	static Object[] parsePOS(String s) {
		// Get the POS tag.
		String posString = "";
		int i;
		for (i = 0; i < s.length(); i++) {
			if (Character.isUpperCase(s.charAt(i))) {
				posString += s.charAt(i);
			} else {
				break;
			}
		}
		Pos pos = string2Pos(posString);

		// Check if there is additional information.
		if (i == s.length()) {
			return new Object[] { pos, new ArrayList<>(), new ArrayList<>() };
		}

		int startExtra = s.indexOf("[");
		String extra = "";
		if (startExtra != -1) {
			// Remove the brackets.
			extra = s.substring(startExtra + 1, s.length() - 1);
		}

		// Get the noun gender.
		String gender = "";
		if (startExtra == -1) {
			gender = s.substring(i);
		} else if (startExtra > i) {
			gender = s.substring(i, startExtra);
		}
		if (gender.equals("t") || gender.equals("u")) {
			// Typo: 'Vt' instead of 'VT'.
			// Typo: "Nu" is a typo for "Nm", "Nn" or "N".
			gender = "";
		}

		// Extract grammatical information.
		ArrayList<String> grammar = new ArrayList<>();
		grammar.add(gender);
		ArrayList<String> usage = new ArrayList<>();
		for (String extraInfo : extra.split(",\\s*")) {
			if (extraInfo.startsWith("sty=")) {
				// Style (familiar, vulgar, polite, etc.)
				usage.add(abbr2Style(extraInfo));
			} else if (extraInfo.startsWith("tmp=") || extraInfo.startsWith("prs=") || extraInfo.startsWith("mod=")
					|| extraInfo.startsWith("deg=") || extraInfo.startsWith("cas=")) {
				// Redundant (and used inconsistently).
				// We don't need entries for inflected adjectives/verbs
				// since we show the lemma instead. This is a warning flag:
				grammar.add("INFLECTED");
				break;
			} else {
				grammar.add(extraInfo.replace("gen=", "").replace("num=", "").replace("cas=", ""));
			}
		}

		return new Object[] { pos, grammar, usage };
	}

	private static String abbr2Style(String abbr) {
		switch (abbr) {
		case "sty=anc":
			// "anchor"? ("siehe")
			// Only used in one entry ("syv - s. sju" (sieben)).
			return "";
		case "sty=fam":
			return "colloquial";
		case "sty=pej":
			return "pejorative";
		case "sty=pol":
			return "polite";
		case "sty=vlg":
			return "vulgar";
		}
		return "";
	}

	/**
	 * Parses the "lemma {noun gender} <abbr> [usage/explanation]" structure
	 * used in dict.cc entries.
	 * 
	 * @param lemmaAndExtraInfo
	 * @return an Object array consisting of a String (the lemma), an ArrayList
	 *         <String> (grammar information), an ArrayList<String> (usage
	 *         information) and an ArrayList<String> (abbreviated forms)
	 */
	static Object[] extractDictCCComments(String lemmaAndExtraInfo) {
		Object[] grammar = match(patternCurly, lemmaAndExtraInfo);
		lemmaAndExtraInfo = (String) grammar[0];
		Object[] usage = match(patternSquare, lemmaAndExtraInfo);
		lemmaAndExtraInfo = (String) usage[0];
		Object[] abbr = match(patternTriangle, lemmaAndExtraInfo);
		// abbr[0] is the lemma
		return new Object[] { abbr[0], grammar[1], usage[1], abbr[1] };
	}

	/**
	 * Extracts a given type of dict.cc comment from a string.
	 * 
	 * @param pattern
	 * @param lemmaAndExtraInfo
	 * @return an Object array consisting of the string sans comment and an
	 *         ArrayList<String> containing the additional information.
	 */
	static Object[] match(Pattern pattern, String lemmaAndExtraInfo) {
		Matcher matcher = pattern.matcher(lemmaAndExtraInfo);
		ArrayList<String> comments = new ArrayList<>();
		String match;
		Stack<Integer> matches = new Stack<>();
		while (matcher.find()) {
			match = matcher.group().trim();
			// Remove the brackets around the comment.
			comments.add(match.substring(1, match.length() - 1));
			matches.push(matcher.end());
			matches.push(matcher.start());
		}
		while (!matches.isEmpty()) {
			lemmaAndExtraInfo = lemmaAndExtraInfo.substring(0, matches.pop())
					+ lemmaAndExtraInfo.substring(matches.pop());
		}
		return new Object[] { lemmaAndExtraInfo.trim(), comments };
	}

	/**
	 * See section 2.5 of the report.
	 * 
	 * @param lemma
	 * @param form
	 * @return true if the preterite form is regular, false otherwise
	 */
	static boolean isRegularPret(String lemma, String form) {
		if (lemma.length() > 2 && lemma.endsWith("e")) {
			if (form.equals(lemma + "t")) {
				// Conjugation Ia, e.g. 'regn+et'.
				return true;
			}
			String stem = lemma.substring(0, lemma.length() - 1);
			if (form.equals(stem + "a")) {
				// Conjugation Ib, e.g. 'regn+a'.
				return true;
			}
			if (form.endsWith("te")) {
				// Conjugation II, e.g. 'reis+te'.
				if (form.equals(stem + "te")) {
					return true;
				}
				// 'kjenne' -> 'kjen+te'
				return stem.charAt(stem.length() - 1) == stem.charAt(stem.length() - 2);
			}
			if ((stem.endsWith("v") || stem.endsWith("g") || stem.endsWith("d")
					|| (isVowel(stem.charAt(stem.length() - 1)) && isVowel(stem.charAt(stem.length() - 2))))
					&& form.equals(stem + "de")) {
				// Conjugation III, e.g. 'prøv+de'.
				return true;
			}
		} else {
			// Conjugation IV, e.g. 'bo+dde'.
			return (form.equals(lemma + "dde"));
		}
		return false;
	}

	/**
	 * See section 2.5 of the report.
	 * 
	 * @param lemma
	 * @param form
	 * @return true if the perfective form is regular, false otherwise
	 */
	static boolean isRegularPerf(String lemma, String form) {
		if (lemma.length() > 2 && lemma.endsWith("e")) {
			if (form.equals(lemma + "t")) {
				// Conjugation Ia, e.g. 'regn+et'.
				return true;
			}
			String stem = lemma.substring(0, lemma.length() - 1);
			if (form.equals(stem + "a")) {
				// Conjugation Ib, e.g. 'regn+a'.
				return true;
			}
			if (form.endsWith("t")) {
				// Conjugation II, e.g. 'reis+t'.
				if (form.equals(stem + "t")) {
					return true;
				}
				// 'kjenne' -> 'kjen+t'
				return stem.charAt(stem.length() - 1) == stem.charAt(stem.length() - 2);
			}
			if ((stem.endsWith("v") || stem.endsWith("g") || stem.endsWith("d")
					|| (isVowel(stem.charAt(stem.length() - 1)) && isVowel(stem.charAt(stem.length() - 2))))
					&& form.equals(stem + "d")) {
				// Conjugation III, e.g. 'prøv+d'.
				return true;
			}
		} else {
			// Conjugation IV, e.g. 'bo+dd'.
			return (form.equals(lemma + "dd"));
		}
		return false;
	}

	/**
	 * See section 2.5 of the report.
	 * 
	 * @param lemma
	 * @param form
	 * @return true if the plural form is regular, false otherwise
	 */
	static boolean isRegularPlural(String lemma, String form) {
		String lemmaASCII = lemma.replace("é", "e");
		if (form.equals(lemmaASCII)) {
			return false;
		}
		if (form.equals(lemmaASCII + "er") || form.equals(lemmaASCII + "r") || form.equals(lemmaASCII + "e")) {
			return true;
		}
		if (lemmaASCII.endsWith("el") || lemmaASCII.endsWith("er")) {
			// Stem: 'eksempel' -> 'eksempl', 'sommer' -> 'somr'
			String stem = lemmaASCII.substring(0, lemmaASCII.length() - 2);
			if (stem.length() > 2 && stem.charAt(stem.length() - 1) == stem.charAt(stem.length() - 2)) {
				stem = stem.substring(0, stem.length() - 1);

			}
			stem = stem + lemmaASCII.substring(lemmaASCII.length() - 1);
			return form.equals(stem + "er") || form.equals(stem + "e");
		}
		return false;
	}
	
	/**
	 * See section 2.5 of the report.
	 * 
	 * @param lemma
	 * @param form
	 * @return true if the comparative form is regular, false otherwise
	 */
	static boolean isRegularComparative(String lemma, String form){
		if (lemma.endsWith("e")){
			return form.equals(lemma + "re");
		}
		return form.equals(lemma + "ere");
	}

	/**
	 * See section 2.5 of the report.
	 * 
	 * @param lemma
	 * @param form
	 * @return true if the superlative form is regular, false otherwise
	 */
	static boolean isRegularSuperlative(String lemma, String form){
		if (lemma.endsWith("re")){
			return form.equals(lemma.substring(0, lemma.length() - 2) + "erst");
		}
		if (lemma.endsWith("en")){
			return form.equals(lemma.substring(0, lemma.length() - 2) + "nest");
		}
		if (lemma.endsWith("e") || lemma.endsWith("ig") || lemma.endsWith("som")){
			return form.equals(lemma + "st");
		}
		return form.equals(lemma + "est");
	}

	public static boolean isVowel(char c) {
		switch (c) {
		case 'i':
		case 'e':
		case 'a':
		case 'o':
		case 'u':
		case 'y':
		case 'å':
		case 'ø':
		case 'æ':
			return true;
		default:
			return false;
		}
	}

}
