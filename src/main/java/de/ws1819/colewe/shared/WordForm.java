package de.ws1819.colewe.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A lemma or inflected word form and its pronunciation.
 * 
 * @author Verena Blaschke
 */
public class WordForm implements IsSerializable {

	private String form;
	private String pronunciation;

	// For GWT
	public WordForm() {
		this("", "");
	}

	public WordForm(String form, String pronunciation) {
		setForm(form);
		setPronunciation(pronunciation);
	}

	public WordForm(String form) {
		this(form, "");
	}

	/**
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(String form) {
		this.form = form.replaceAll("\\s+", " ");
	}

	/**
	 * @return the pronunciation
	 */
	public String getPronunciation() {
		return pronunciation;
	}

	/**
	 * @param pronunciation
	 *            the pronunciation to set
	 */
	public void setPronunciation(String pronunciation) {
		if (pronunciation == null) {
			this.pronunciation = "";
		} else {
			this.pronunciation = pronunciation;
		}
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
		result = prime * result + ((form == null) ? 0 : form.hashCode());
		result = prime * result + ((pronunciation == null) ? 0 : pronunciation.hashCode());
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
		if (!(obj instanceof WordForm)) {
			return false;
		}
		WordForm other = (WordForm) obj;
		if (form == null) {
			if (other.form != null) {
				return false;
			}
		} else if (!form.equals(other.form)) {
			return false;
		}
		if (pronunciation == null) {
			if (other.pronunciation != null) {
				return false;
			}
		} else if (!pronunciation.equals(other.pronunciation)) {
			return false;
		}
		return true;
	}

	public String toString() {
		if (pronunciation.isEmpty()) {
			return form;
		}
		return form + " " + pronunciation;
	}
}
