package de.ws1819.colewe.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.thirdparty.guava.common.collect.ListMultimap;

import de.ws1819.colewe.shared.Entry;
import de.ws1819.colewe.shared.SampleSentence;

public class DownloadServiceImpl extends HttpServlet {
	private static final long serialVersionUID = -3653205520080376102L;

	private static final Logger logger = Logger.getLogger(DownloadServiceImpl.class.getSimpleName());

	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Make the results a file download.
		response.setContentType("text/plain");
		response.addHeader("Content-Disposition", "attachment;filename=\"results.txt\"");
		response.setCharacterEncoding("UTF-8");

		String query = request.getParameter("query");
		if (query == null) {
			return;
		}
		logger.info("New download query: " + query);

		StringBuilder sb = new StringBuilder();

		// Get the results.
		String[] queryParts = query.split("&");
		for (int i = 0; i < queryParts.length; i++) {
			sb.append("QUERY: ").append(queryParts[i]).append("\n");
			ArrayList<Entry> results = DictionaryServiceImpl.query(
					(ListMultimap<String, Entry>) getServletContext().getAttribute("entries"),
					(ListMultimap<String, Entry>) getServletContext().getAttribute("prefixes"),
					(ListMultimap<String, Entry>) getServletContext().getAttribute("suffixes"),
					(HashMap<String, String>) getServletContext().getAttribute("mlEntries"),
					(ListMultimap<String, SampleSentence>) getServletContext().getAttribute("extraSentences"),
					queryParts[i]);
			for (Entry entry : results) {
				sb.append(entry.toPrintString());
			}
			sb.append("##########################\n\n");
		}

		response.getWriter().print(sb.toString());
	}

}
