package de.ws1819.colewe.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class PreprocessTatoeba {

	private static final String tatoeba_path = Listener.BASE_PATH + Listener.RESOURCES_PATH + "/tatoeba/";
	private static final String sentence_path = tatoeba_path + "sentences.tar.bz2";
	private static final String links_path = tatoeba_path + "links.tar.bz2";
	private static final String out_path = Listener.BASE_PATH + Listener.TATOEBA_PATH;

	public static void main(String args[]) {
		HashMap<Integer, String> sentencesNO = new HashMap<Integer, String>();
		HashMap<Integer, String> sentencesDE = new HashMap<Integer, String>();

		System.out.println("Read sentences.tar.bz2");
		try (TarArchiveInputStream tin = new TarArchiveInputStream(
				new BZip2CompressorInputStream(new FileInputStream(sentence_path)))) {
			TarArchiveEntry currentEntry = tin.getNextTarEntry();
			BufferedReader br = null;
			while (currentEntry != null) {
				br = new BufferedReader(new InputStreamReader(tin));
				String line;
				while ((line = br.readLine()) != null) {
					String[] fields = line.split("\\t");
					if (fields[1].equals("nob")) {
						sentencesNO.put(Integer.parseInt(fields[0]), fields[2]);
					} else if (fields[1].equals("deu")) {
						sentencesDE.put(Integer.parseInt(fields[0]), fields[2]);
					}
				}
				currentEntry = tin.getNextTarEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		HashMap<String, String> sentencePairs = new HashMap<>();

		System.out.println("Read links.tar.bz2");
		try (TarArchiveInputStream tin = new TarArchiveInputStream(
				new BZip2CompressorInputStream(new FileInputStream(links_path)))) {
			BufferedReader br = null;
			while (tin.getNextTarEntry() != null) {
				br = new BufferedReader(new InputStreamReader(tin));
				String line;
				while ((line = br.readLine()) != null) {
					String[] fields = line.split("\\t");
					String sentNO, sentDE;
					if ((sentNO = sentencesNO.get(Integer.parseInt(fields[0]))) != null
							&& (sentDE = sentencesDE.get(Integer.parseInt(fields[1]))) != null) {
						sentencePairs.put(sentNO, sentDE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Serialize");
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(out_path))) {
			out.writeObject(sentencePairs);
		} catch (IOException i) {
			i.printStackTrace();
		}
		System.out.println("DONE");

	}

}
