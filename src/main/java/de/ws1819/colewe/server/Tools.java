package de.ws1819.colewe.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tools {

	private static Map<String, String> one2one = new HashMap<String, String>() {
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
			put("\"", "ˈ");
			put("%", "ˌ");
			put(":", "ː");
			put("@", "ə");
			put("\\{", "æ");
			put("\\}", "ʉ");
			put("2", "ø");
			put("9", "œ");
			put("\\?", "ʔ");
			put("_", " "); // Custom: word break
			put("'", "ˈ"); // TODO custom
			// Occur as typos for their lowecase counterparts:
			put("D", "d");
			put("K", "k");
			put("P", "p");
		}
	};

	// ", %, ', ), ., 0, 2, 9, :, ?, @, A, C, D, E, I, K, N, O, P, S, U, Y, Z,
	// _, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, r, s, t, u, v, y, {,
	// }, æ]

	public static String xsampaToIPA(String pron) {
		pron = pron.replaceAll("__", " "); // Custom: word break
		for (Entry<String, String> entry : one2one.entrySet()) {
			pron = pron.replaceAll(entry.getKey(), entry.getValue());
		}
		return pron;
	}

}
