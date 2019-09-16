package de.ws1819.colewe.shared;

import java.util.ArrayList;
import java.util.Objects;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A translational equivalent that is part of an
 * {@link de.ws1819.colewe.shared.Entry}. See section 2.2 of the report.
 * 
 * @author Verena Blaschke
 */
public class TranslationalEquivalent implements IsSerializable {

	private ArrayList<String> translation;
	private ArrayList<String> usage;
	private ArrayList<String> abbr;
	boolean automaticallyInferred;

	/**
	 * @param translation
	 *            one or more translations (synonyms of one another)
	 * @param usage
	 *            usage information
	 * @param abbr
	 *            abbreviated forms
	 * @param automaticallyInferred
	 *            true if the translation was inferred through machine
	 *            translation, false if it was taken from dictionary input data
	 */
	public TranslationalEquivalent(ArrayList<String> translation, ArrayList<String> usage, ArrayList<String> abbr,
			boolean automaticallyInferred) {
		this.translation = translation;
		setUsage(usage);
		setAbbr(abbr);
		this.automaticallyInferred = automaticallyInferred;
	}

	// For GWT serialization.
	public TranslationalEquivalent() {
		this(new ArrayList<String>(), null, null, false);
	}

	// For no-de-dict.txt
	public TranslationalEquivalent(ArrayList<String> translation, ArrayList<String> usage) {
		this(translation, usage, null, false);
	}

	// For dict.cc
	public TranslationalEquivalent(String translation, ArrayList<String> usage, ArrayList<String> abbr) {
		this(new ArrayList<String>(), usage, abbr, false);
		addTranslation(translation);
	}

	// For the automatically inferred entries
	public TranslationalEquivalent(String translation, boolean automaticallyInferred) {
		this(new ArrayList<String>(), null, null, automaticallyInferred);
		addTranslation(translation);
	}

	@Override
	public String toString() {
		return getTranslationString() + " [" + getUsageString() + "] <" + getAbbrString() + ">";
	}

	/**
	 * @return a string for pretty printing
	 */
	public String toPrintString() {
		return getTranslationString() + (usage == null || usage.isEmpty() ? "" : " " + usage)
				+ (abbr == null || abbr.isEmpty() ? "" : " <" + getAbbrString() + ">");
	}

	public void addTranslation(String transl) {
		if (translation == null) {
			translation = new ArrayList<String>();
		}
		translation.add(transl);
	}

	public String getTranslationString() {
		return String.join(", ", translation);
	}

	/**
	 * @return the translation
	 */
	public ArrayList<String> getTranslation() {
		return translation;
	}

	/**
	 * @param translation
	 *            the translation to set
	 */
	public void setTranslation(ArrayList<String> translation) {
		this.translation = translation;
	}

	/**
	 * @return the usage
	 */
	public ArrayList<String> getUsage() {
		return usage;
	}

	public String getUsageString() {
		return String.join(", ", usage);
	}

	/**
	 * @param usage
	 *            the usage to set
	 */
	public void setUsage(ArrayList<String> usage) {
		if (usage == null) {
			this.usage = new ArrayList<>();
			return;
		}
		usage.removeIf(Objects::isNull);
		usage.removeIf(String::isEmpty);
		this.usage = usage;
	}

	public void addUsage(ArrayList<String> otherUsage) {
		for (String other : otherUsage) {
			if (!usage.contains(other)) {
				usage.add(other);
			}
		}
	}

	/**
	 * @return the abbr
	 */
	public ArrayList<String> getAbbr() {
		return abbr;
	}

	/**
	 * @param abbr
	 *            the abbr to set
	 */
	public void setAbbr(ArrayList<String> abbr) {
		if (abbr == null) {
			this.abbr = new ArrayList<>();
			return;
		}
		abbr.removeIf(Objects::isNull);
		abbr.removeIf(String::isEmpty);
		this.abbr = abbr;
	}

	public void addAbbr(ArrayList<String> otherAbbr) {
		for (String other : otherAbbr) {
			if (!abbr.contains(other)) {
				abbr.add(other);
			}
		}
	}

	public String getAbbrString() {
		return String.join(", ", abbr);
	}

	public boolean isAutomaticallyInferred() {
		return automaticallyInferred;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abbr == null) ? 0 : abbr.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TranslationalEquivalent)) {
			return false;
		}
		TranslationalEquivalent other = (TranslationalEquivalent) obj;
		if (abbr == null) {
			if (other.abbr != null) {
				return false;
			}
		} else if (!abbr.equals(other.abbr)) {
			return false;
		}
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		if (usage == null) {
			if (other.usage != null) {
				return false;
			}
		} else if (!usage.equals(other.usage)) {
			return false;
		}
		return true;
	}

}
