package de.ws1819.colewe.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tools {

	private static Map<String, String> two2one = new HashMap<String, String>() {
		{

			// Retroflexes TODO in transcription?
			put("d`", "ɖ");
			put("l`", "ɭ");
			put("r`", "ɽ");
			put("s`", "ʂ");
			put("t`", "ʈ");
			put("z`", "ʐ");
		}
	};

	private static Map<String, String> one2one = new HashMap<String, String>() {
		{
			put("A", "ɑ");
			put("C", "ç");
			put("D", "ð	"); // TODO where??
			put("E", "ɛ");
			put("I", "ɪ");
			put("K", "ɬ"); // TODO where
			put("N", "ŋ");
			put("O", "ɔ");
			put("P", "ʋ"); // TODO where
			put("S", "ʃ");
			put("U", "ʊ");
			put("Y", "ʏ");
			put("Z", "ʒ"); // TODO where
			put("\"", "ˈ");
			put("%", "ˌ");
			put("'", "ʲ"); // TODO where
			put(":", "ː");
			put("@", "ə");
			put("\\{", "æ");
			put("\\}", "ʉ");
			put("2", "ø");
			put("9", "œ");
			put("\\?", "ʔ");
		}
	};

	// TODO - (separator) and affricates
	// TODO /
	// TODO å
	// TODO [ and ]
	// TODO diacritics added with _

	// ", %, ', ), ,, -, ., /, 0, 2, 9, :, ?, @, A, C, D, E, I, K, N, O, P, S,
	// U, Y, Z, [, ], _, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, r, s,
	// t, u, v, y, {, }, å, æ, ø

	public static String xsampaToIPA(String pron) {
		if (pron.contains("-") || pron.contains("[") || pron.contains("]") || pron.contains("_")
				|| pron.contains("/") || pron.contains("å")) {
			System.out.println(pron);
		}
		for (Entry<String, String> entry : two2one.entrySet()) {
			pron = pron.replaceAll(entry.getKey(), entry.getValue());
		}
		for (Entry<String, String> entry : one2one.entrySet()) {
			pron = pron.replaceAll(entry.getKey(), entry.getValue());
		}
		return pron;
	}

}
