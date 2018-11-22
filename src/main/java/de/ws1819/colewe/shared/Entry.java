package de.ws1819.colewe.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Entry implements Serializable {

	private static final long serialVersionUID = 1614919089029847522L;
	private String lemma;
	private String comment;
	private String pos;
	// TODO? add inflection info (map)
	private HashSet<String> inflections;
	private String translation;

	public Entry() {
		this(null, null, null, null, null);
	}

	public Entry(String lemma, String comment, String pos, String translation) {
		this(lemma, comment, pos, null, translation);
	}

	public Entry(String lemma, String comment, String pos, Set<String> inflections, String translation) {
		this.lemma = lemma;
		this.comment = comment;
		this.pos = pos;
		this.inflections = (inflections == null ? null : new HashSet<String>(inflections));
		this.translation = translation;
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * @param lemma
	 *            the lemma to set
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * @param pOS
	 *            the pOS to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/**
	 * @return the inflections
	 */
	public Set<String> getInflections() {
		return inflections;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(Set<String> inflections) {
		this.inflections = (inflections == null ? null : new HashSet<String>(inflections));
	}

	/**
	 * @return the translation
	 */
	public String getTranslation() {
		return translation;
	}

	/**
	 * @param translation
	 *            the translation to set
	 */
	public void setTranslation(String translation) {
		this.translation = translation;
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
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((inflections == null) ? 0 : inflections.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((translation == null) ? 0 : translation.hashCode());
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
		if (!(obj instanceof Entry)) {
			return false;
		}
		Entry other = (Entry) obj;
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (inflections == null) {
			if (other.inflections != null) {
				return false;
			}
		} else if (!inflections.equals(other.inflections)) {
			return false;
		}
		if (lemma == null) {
			if (other.lemma != null) {
				return false;
			}
		} else if (!lemma.equals(other.lemma)) {
			return false;
		}
		if (pos == null) {
			if (other.pos != null) {
				return false;
			}
		} else if (!pos.equals(other.pos)) {
			return false;
		}
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param inflections
	 *            the inflections to set
	 */
	public void setInflections(HashSet<String> inflections) {
		this.inflections = inflections;
	}

	public String toString() {
		return lemma + ": " + translation;
	}

}
