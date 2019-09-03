package de.ws1819.colewe.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SampleSentence implements IsSerializable {

	private String no;
	private String de;

	public SampleSentence() {
		this("", "");
	}

	public SampleSentence(java.util.Map.Entry<String, String> sentencePair) {
		this(sentencePair.getKey(), sentencePair.getValue());
	}

	public SampleSentence(String no, String de) {
		this.no = no;
		this.de = de;
	}

	/**
	 * @return the no
	 */
	public String getNo() {
		return no;
	}

	/**
	 * @param no
	 *            the no to set
	 */
	public void setNo(String no) {
		this.no = no;
	}

	/**
	 * @return the de
	 */
	public String getDe() {
		return de;
	}

	/**
	 * @param de
	 *            the de to set
	 */
	public void setDe(String de) {
		this.de = de;
	}
	
	@Override
	public String toString(){
		return no + " : " + de;
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
		result = prime * result + ((de == null) ? 0 : de.hashCode());
		result = prime * result + ((no == null) ? 0 : no.hashCode());
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
		if (!(obj instanceof SampleSentence)) {
			return false;
		}
		SampleSentence other = (SampleSentence) obj;
		if (de == null) {
			if (other.de != null) {
				return false;
			}
		} else if (!de.equals(other.de)) {
			return false;
		}
		if (no == null) {
			if (other.no != null) {
				return false;
			}
		} else if (!no.equals(other.no)) {
			return false;
		}
		return true;
	}

}
