package de.ws1819.colewe.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ws1819.colewe.shared.Pos;

public class Tools {

	private static final Pattern patternCurly = Pattern.compile("\\s\\{.*?\\}");
	private static final Pattern patternSquare = Pattern.compile("\\s\\[.*?\\]");
	static final Pattern patternSquareWithoutWS = Pattern.compile("\\[.*?\\]");
	private static final Pattern patternTriangle = Pattern.compile("\\s\\<.*?\\>");

	private static Map<String, String> one2one = new HashMap<String, String>() {
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
			put("_", " "); // Custom:
							// word
							// break
			// Custom: combination of primary stress and tone information
			put("'", "¹");
			put("\"", "²");
			// Occur as typos for their lowecase counterparts:
			put("D", "d");
			put("K", "k");
			put("P", "p");
		}
	};

	// ", %, ', ), ., 0, 2, 9, :, ?, @, A, C, D, E, I, K, N, O, P, S, U, Y, Z,
	// _, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, r, s, t, u, v, y, {,
	// }, æ]

	static String xsampaToIPA(String pron) {
		pron = pron.replaceAll("__", " "); // Custom: word break
		for (Entry<String, String> entry : one2one.entrySet()) {
			pron = pron.replaceAll(entry.getKey(), entry.getValue());
		}
		return "/" + pron + "/";
	}

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
		case "fadv": // question adverb
		case "radv": // response adverb
			return Pos.ADV;
		case "conj":
		case "konj":
		case "cnj":
		case "sbu": // subordinating conjunction
		case "sbj": // subordinating conjunction
			return Pos.CONJ;
		case "det":
		case "fdet": // question determiner
		case "num":
		case "fnum": // question numeral (how many)
			return Pos.DET;
		case "interj":
		case "itj":
		case "fitj": // question interjection
			return Pos.ITJ;
		case "ne":
			return Pos.NE;
		case "noun":
		case "subst":
		case "n":
		case "nm": // typo ('n')
			return Pos.NOUN;
		case "pref":
		case "prefix":
		case "pfx":
		case "i": // i sms = i samansetning = initial morphemes/words in
					// compound words
			return Pos.PFX;
		case "prep":
		case "prp":
		case "ccp": // circumposition
			return Pos.PREP;
		case "pron":
		case "prn":
		case "fprn": // question pronoun
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

	static Object[] parsePOS(String s) {
		// POSTAGnoungender[extra=info]
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
		if (i == s.length()) {
			return new Object[] { pos, new ArrayList<>(), new ArrayList<>() };
		}
		int startExtra = s.indexOf("[");
		String extra = "";
		if (startExtra != -1) {
			// without the brackets
			extra = s.substring(startExtra + 1, s.length() - 1);
		}
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
		ArrayList<String> grammar = new ArrayList<>();
		grammar.add(gender);
		ArrayList<String> usage = new ArrayList<>();
		for (String extraInfo : extra.split(",\\s*")) {
			if (extraInfo.startsWith("sty=")) {
				// Style (familiar, vulgar, polite, etc.)
				usage.add(extraInfo);
			} else if (extraInfo.startsWith("tmp=") || extraInfo.startsWith("prs=") || extraInfo.startsWith("mod=")) {
				// Redundant and used inconsistently.
				continue;
			} else {
				grammar.add(extraInfo.replace("gen=", "").replace("num=", "").replace("cas=", ""));
			}
		}

		return new Object[] { pos, grammar, usage };
	}

	static String[] extractDictCCComments(String lemma) {
		String[] grammar = match(patternCurly, lemma);
		lemma = grammar[0];
		String[] usage = match(patternSquare, lemma);
		lemma = usage[0];
		String[] abbr = match(patternTriangle, lemma);
		// abbr[0] is the lemma
		return new String[] { abbr[0], grammar[1], usage[1], abbr[1] };
	}

	static String[] match(Pattern pattern, String lemma) {
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

}
