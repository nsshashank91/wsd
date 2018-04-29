package com.shash.kannada.wsd;

import java.io.Serializable;


public class Result implements Serializable{

	private String input;
	
	private String polysemyWord;
	
	private String sense;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getPolysemyWord() {
		return polysemyWord;
	}

	public void setPolysemyWord(String polysemyWord) {
		this.polysemyWord = polysemyWord;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	@Override
	public String toString() {
		return "Result [input=" + input + ", polysemyWord=" + polysemyWord
				+ ", sense=" + sense + "]";
	}
	
	
}
