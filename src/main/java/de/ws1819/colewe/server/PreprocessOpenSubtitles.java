package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.google.gwt.thirdparty.guava.common.collect.ArrayListMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

public class PreprocessOpenSubtitles {

	public static void main(String[] args) {
		// ListMultimap<String, String> entries = ArrayListMultimap.create();
		HashMap<String, String> entries = new HashMap<>();
		try (BufferedReader br = new BufferedReader(
				new FileReader(Listener.BASE_PATH + Listener.RESOURCES_PATH + "opensubtitles/no-de.actual.ti.final"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(" ");
				double prob = Double.parseDouble(fields[2]);
				// if (prob > 0.3) {
				if (prob > 0.5) {
					entries.put(fields[0], fields[1]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Serialize");
		try (ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(Listener.BASE_PATH + Listener.OPENSUBTITLES_PATH))) {
			out.writeObject(entries);
		} catch (IOException i) {
			i.printStackTrace();
		}
		System.out.println("DONE");

	}

}
