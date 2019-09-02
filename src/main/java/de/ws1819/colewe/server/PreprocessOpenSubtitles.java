package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

public class PreprocessOpenSubtitles {

	private static final String opensubtitles_path = Listener.BASE_PATH + Listener.RESOURCES_PATH + "opensubtitles/";

	public static void main(String[] args) {
		ListMultimap<String, String> entries = ArrayListMultimap.create();
		try (BufferedReader br = new BufferedReader(new FileReader(opensubtitles_path + "no-de.actual.ti.final"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(" ");
				double prob = Double.parseDouble(fields[2]);
				if (prob > 0.4) {
					entries.put(fields[0], fields[1]);
					System.out.println(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
