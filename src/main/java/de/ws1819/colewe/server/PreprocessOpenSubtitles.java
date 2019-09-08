package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class PreprocessOpenSubtitles {

	public static void main(String[] args) {
		HashMap<String, String> entries = new HashMap<>();
		try (BufferedReader br = new BufferedReader(
				new FileReader(Listener.BASE_PATH + Listener.RESOURCES_PATH + "opensubtitles/no-de.actual.ti.final"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(" ");
				double prob = Double.parseDouble(fields[2]);
				// Get the single most likely translation per word
				// (if there exists one).
				if (prob > 0.5) {
					entries.put(fields[0], fields[1]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Serialize to " + Listener.BASE_PATH + Listener.OPENSUBTITLES_PATH);
		try (ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(Listener.BASE_PATH + Listener.OPENSUBTITLES_PATH))) {
			out.writeObject(entries);
		} catch (IOException i) {
			i.printStackTrace();
		}
		System.out.println("DONE");

	}

}
