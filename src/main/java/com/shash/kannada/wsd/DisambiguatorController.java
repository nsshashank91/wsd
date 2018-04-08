package com.shash.kannada.wsd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.jcraft.jsch.JSchException;

@RestController
@RequestMapping("/kannada/")
public class DisambiguatorController {
	
	private String input;

	private String polysemyWord;

	private String secondaryPolysemyWord;

	private Multimap<String, String> word_pos_map;

	private List<String[]> semanticsList;

	private LinkedList<String> inputLinkedList;

	private String matchedLongestWordForNoun;

	private String matchedSemanticSentenceForNoun;

	private String matchedLongestWordForVerb;

	private String matchedSemanticSentenceForVerb;

	private Map<String, String> rootWords;
	
	private String laterNounMatchWord;
	private String laterNounMeaning;
	
	@RequestMapping("disambiguate")
	private String aataEegaShuru() throws IOException {
		this.obtainSemanticNet();
		System.out.println("*********************");
		System.out.println("Input is " + inputLinkedList.toString());
		System.out.println("Polysemy word = " + polysemyWord);
		System.out
				.println("Secondary Polysemy word = " + secondaryPolysemyWord);
		Collection<Entry<String, String>> entries = word_pos_map.entries();
		entries.forEach(item -> System.out.println(item.getKey() + " :: "
				+ item.getValue()));
		for (String[] semanticUnit : semanticsList) {
			System.out.println("POS = " + semanticUnit[0]);
			System.out.println("Semantic = " + semanticUnit[1]);
		}
		System.out.println("*********************");
		String matchingWordForNoun = null;
		String matchingWordForVerb = null;
		Set<Entry<String, String>> rootSet = rootWords.entrySet();
		for (Entry<String, String> rootEntry : rootSet) {
			System.out.println("Root values = " + rootEntry.getKey() + " "
					+ rootEntry.getValue());
		}
		List<String> posScenarios = new ArrayList<String>();

		for (Entry<String, String> entry : entries) {
			if (entry.getKey().contains(polysemyWord)
					|| entry.getKey().contains(secondaryPolysemyWord)) {
				if (entry.getValue().equals("NN")) {
					posScenarios.add("NN");
				} else if (entry.getValue().equals("VM")) {
					posScenarios.add("VM");
				}
			}
		}
		String posScenario;
		if (posScenarios.contains("NN") && posScenarios.contains("VM")) {
			posScenario = "differentPOS";
		} else {
			posScenario = "samePOS";
		}
		if (posScenario.equals("differentPOS")) {
			System.out.println("***********" + posScenario + "***********");
			for (Entry<String, String> entry : entries) {
				if (entry.getKey().contains(polysemyWord)
						|| entry.getKey().contains(secondaryPolysemyWord)) {
					if (entry.getValue().equals("NN")) {
						for (String[] semanticUnit : semanticsList) {
							if (semanticUnit[0].equals("noun")) {
								String tokensOtherThanPolysemy = null;
								if (semanticUnit[1].contains(polysemyWord)) {
									// tokensOtherThanPolysemy=semanticUnit[1].replaceAll(polysemyWord,
									// "");
									tokensOtherThanPolysemy = semanticUnit[1];
									String[] splitOtherTokens = tokensOtherThanPolysemy
											.split(" ");
									List<String> otherTokensList = Arrays
											.asList(splitOtherTokens);
									List<String> otherTokensLinkedList = new CopyOnWriteArrayList<String>();
									otherTokensLinkedList
											.addAll(otherTokensList);
									for (String polysemyToken : otherTokensLinkedList) {
										if (polysemyToken
												.contains(polysemyWord)) {
											otherTokensLinkedList
													.remove(polysemyToken);
										}
									}

									System.out
											.println("Replaced Semantics for Noun is "
													+ otherTokensLinkedList);
									for (String nounSemanticMatcher : inputLinkedList) {
										for (String otherSemanticToken : otherTokensLinkedList) {
											int length = 0;
											if (nounSemanticMatcher.length() <= otherSemanticToken
													.length()) {
												length = nounSemanticMatcher
														.length();
											} else {
												length = otherSemanticToken
														.length();
											}
											int matchLength = 0;
											boolean prestine = true;
											for (int j = 0; j < length; j++) {
												if (nounSemanticMatcher
														.charAt(j) == otherSemanticToken
														.charAt(j)) {
													continue;
												} else {
													matchLength = j;
													prestine = false;
													break;
												}
											}
											if (prestine) {
												matchingWordForNoun = nounSemanticMatcher;
												// if(!matchingWordForNoun.contains(polysemyWord)){
												if (matchedLongestWordForNoun == null
														|| matchingWordForNoun
																.length() > matchedLongestWordForNoun
																.length()) {
													matchedLongestWordForNoun = matchingWordForNoun;
													matchedSemanticSentenceForNoun = tokensOtherThanPolysemy;
												}
												break;
												// }
											}
											if (matchLength > 2) {
												matchingWordForNoun = otherSemanticToken
														.substring(0,
																matchLength);
												// if(!matchingWordForNoun.contains(polysemyWord)){
												if (matchedLongestWordForNoun == null
														|| matchingWordForNoun
																.length() > matchedLongestWordForNoun
																.length()) {
													matchedLongestWordForNoun = matchingWordForNoun;
													matchedSemanticSentenceForNoun = tokensOtherThanPolysemy;
												}
												break;
												// }

											}
										}
									}
								}
							} else {
								continue;
							}
						}
						if (matchedLongestWordForNoun != null)
							System.out.println("Matching word is "
									+ matchedLongestWordForNoun);
						if (matchedSemanticSentenceForNoun != null)
							System.out.println("Matched meaning is "
									+ matchedSemanticSentenceForNoun);
					} else if (entry.getValue().equals("VM")) {
						for (String[] semanticUnit : semanticsList) {
							if (semanticUnit[0].equals("verb")) {
								String tokensOtherThanPolysemy = null;
								if (semanticUnit[1].contains(polysemyWord)) {
									tokensOtherThanPolysemy = semanticUnit[1];
									String[] splitOtherTokens = tokensOtherThanPolysemy
											.split(" ");
									List<String> otherTokensList = Arrays
											.asList(splitOtherTokens);
									LinkedList<String> otherTokensLinkedList = new LinkedList<String>();
									otherTokensLinkedList
											.addAll(otherTokensList);
									System.out
											.println("Replaced Semantics for Verb is "
													+ otherTokensLinkedList);
									for (String verbSemanticMatcher : inputLinkedList) {
										for (String otherSemanticToken : otherTokensLinkedList) {
											int length = 0;
											if (verbSemanticMatcher.length() < otherSemanticToken
													.length()) {
												length = verbSemanticMatcher
														.length();
											} else {
												length = otherSemanticToken
														.length();
											}
											int matchLength = 0;
											boolean prestine = true;
											for (int j = 0; j < length; j++) {
												if (verbSemanticMatcher
														.charAt(j) == otherSemanticToken
														.charAt(j)) {
													continue;
												} else {
													matchLength = j;
													prestine = false;
													break;
												}
											}
											if (prestine) {
												matchingWordForVerb = verbSemanticMatcher;
												// if(matchingWordForVerb.contains(polysemyWord)){
												if (matchedLongestWordForVerb == null
														|| matchingWordForVerb
																.length() > matchedLongestWordForVerb
																.length()) {
													matchedLongestWordForVerb = matchingWordForVerb;
													matchedSemanticSentenceForVerb = tokensOtherThanPolysemy;
												}
												break;
												// }
											}
											if (matchLength > 2) {
												matchingWordForVerb = otherSemanticToken
														.substring(0,
																matchLength);
												// if(!matchingWordForVerb.contains(polysemyWord)){
												if (matchedLongestWordForVerb == null
														|| matchedLongestWordForVerb
																.length() > matchedLongestWordForVerb
																.length()) {
													matchedLongestWordForVerb = matchingWordForVerb;
													matchedSemanticSentenceForVerb = tokensOtherThanPolysemy;
												}
												break;
												// }

											}
										}
									}
								}
							} else {
								continue;
							}
						}
						if (matchedLongestWordForVerb != null)
							System.out.println("Matching word is "
									+ matchedLongestWordForVerb);
						if (matchedSemanticSentenceForVerb != null)
							System.out.println("Matched meaning is "
									+ matchedSemanticSentenceForVerb);
					}
				}
			}
		} else {
			System.out.println("***********" + posScenario + "***********");
			for (Entry<String, String> entry : entries) {
				if (entry.getKey().contains(polysemyWord)
						|| entry.getKey().contains(secondaryPolysemyWord)) {
					if (entry.getValue().equals("NN")) {
						for (String[] semanticUnit : semanticsList) {
							if (semanticUnit[0].equals("noun")) {
								String tokensOtherThanPolysemy = null;
								if (semanticUnit[1].contains(polysemyWord)) {
									// tokensOtherThanPolysemy=semanticUnit[1].replaceAll(polysemyWord,
									// "");
									tokensOtherThanPolysemy = semanticUnit[1];
									String[] splitOtherTokens = tokensOtherThanPolysemy
											.split(" ");
									List<String> otherTokensList = Arrays
											.asList(splitOtherTokens);
									List<String> otherTokensLinkedList = new CopyOnWriteArrayList<String>();
									otherTokensLinkedList
											.addAll(otherTokensList);
									for (String polysemyToken : otherTokensLinkedList) {
										if (polysemyToken
												.contains(polysemyWord)) {
											otherTokensLinkedList
													.remove(polysemyToken);
										}
									}

									System.out
											.println("Replaced Semantics for Noun is "
													+ otherTokensLinkedList);
									for (String nounSemanticMatcher : inputLinkedList) {
										for (String otherSemanticToken : otherTokensLinkedList) {
											int length = 0;
											if (nounSemanticMatcher.length() <= otherSemanticToken
													.length()) {
												length = nounSemanticMatcher
														.length();
											} else {
												length = otherSemanticToken
														.length();
											}
											int matchLength = 0;
											boolean prestine = true;
											for (int j = 0; j < length; j++) {
												if (nounSemanticMatcher
														.charAt(j) == otherSemanticToken
														.charAt(j)) {
													continue;
												} else {
													matchLength = j;
													prestine = false;
													break;
												}
											}
											if (prestine) {
												matchingWordForNoun = nounSemanticMatcher;
												// if(!matchingWordForNoun.contains(polysemyWord)){
												if (matchedLongestWordForNoun == null
														|| matchingWordForNoun
																.length() > matchedLongestWordForNoun
																.length()) {
													matchedLongestWordForNoun = matchingWordForNoun;
													matchedSemanticSentenceForNoun = tokensOtherThanPolysemy;
												}
												break;
												// }
											}
											if (matchLength > 2) {
												matchingWordForNoun = otherSemanticToken
														.substring(0,
																matchLength);
												// if(!matchingWordForNoun.contains(polysemyWord)){
												if (matchedLongestWordForNoun == null
														|| matchingWordForNoun
																.length() > matchedLongestWordForNoun
																.length()) {
													matchedLongestWordForNoun = matchingWordForNoun;
													matchedSemanticSentenceForNoun = tokensOtherThanPolysemy;
												}
												break;
												// }

											}
										}
									}
								}
							} else {
								continue;
							}
						}
						if (matchedLongestWordForNoun != null){
							laterNounMatchWord = matchedLongestWordForNoun;
							//System.out.println("Matching word is "+ matchedLongestWordForNoun);
						}
							
						if (matchedSemanticSentenceForNoun != null){
							laterNounMeaning = matchedSemanticSentenceForNoun;
							//System.out.println("Matched meaning is "+ matchedSemanticSentenceForNoun);
						}
					}
				}
			}
			System.out.println("Noun sense analyser");
			this.nounSenseAnalysis(matchedLongestWordForNoun);
		}
		if(laterNounMatchWord!=null){
			System.out.println("Matching word is "+laterNounMatchWord);
			laterNounMatchWord = null;
		}
		if(laterNounMeaning!=null){
			System.out.println("Matched meaning is "+laterNounMeaning);
			laterNounMeaning = null;
		}
		this.endInstance();
		return "disambiguated";
	}

	private void nounSenseAnalysis(String matchedNounWord) {
		List<String> nounSenseList = new ArrayList<String>();
		nounSenseList.addAll(inputLinkedList);
		int nounIndex = -1;
		int matchingIndex = -1;
		int inputLength = inputLinkedList.size();
		for (int i = 0; i < inputLength; i++) {
			String word = inputLinkedList.get(i);
			if (word.contains(matchedNounWord)) {
				System.out.println("Matched word " + word);
				matchingIndex = i;
				break;
			}
		}
		if (matchingIndex != -1) {
			int removeIndex = -1;
			nounIndex = matchingIndex;
			while (nounIndex != -1 && nounIndex < inputLength) {
				String prevWord = inputLinkedList.get(nounIndex - 1);
				System.out.println("prev word " + prevWord);
				int successiveIndex = nounIndex + 1;
				String successiveWord = null;
				if (successiveIndex < inputLength) {
					successiveWord = inputLinkedList.get(successiveIndex);
					System.out.println("Successive word " + successiveWord);
				}
				if (prevWord.contains(polysemyWord)) {
					removeIndex = nounIndex - 1;
					break;
				} else if (successiveWord.contains(polysemyWord)) {
					removeIndex = successiveIndex;
					break;
				}
				nounIndex--;
			}
			if (removeIndex != -1) {
				System.out.println("Remove index = " + removeIndex);
				nounSenseList.remove(removeIndex);
				nounSenseList.remove(matchingIndex - 1);
				System.out.println(nounSenseList);
				this.startNounSenseAnalysis(nounSenseList);
			}
		}
	}

	private void startNounSenseAnalysis(List<String> nounSenseList) {
		for (int i = 0; i < nounSenseList.size(); i++) {
			String nounWord = nounSenseList.get(i);
			if (nounWord.equals(polysemyWord)) {
				String nounLinkedWord = nounSenseList.get(i + 1);
				if (nounLinkedWord.endsWith("ನು")||nounLinkedWord.endsWith("ಳು")||nounLinkedWord.endsWith("ರು")
						|| nounLinkedWord.endsWith("ನನ್ನು")||nounLinkedWord.endsWith("ಳನ್ನು")||nounLinkedWord.endsWith("ರನ್ನು")
						|| nounLinkedWord.endsWith("ನನ್ನ")||nounLinkedWord.endsWith("ಳನ್ನ")||nounLinkedWord.endsWith("ರನ್ನ")
						|| nounLinkedWord.endsWith("ನಿಂದ")|| nounLinkedWord.endsWith("ಳಿಂದ")||nounLinkedWord.endsWith("ರಿಂದ")
						|| nounLinkedWord.endsWith("ನಿಗೆ")||nounLinkedWord.endsWith("ಳಿಗೆ")||nounLinkedWord.endsWith("ರಿಗೆ")
						|| nounLinkedWord.endsWith("ಗೆ")
						|| nounLinkedWord.endsWith("ನ")||nounLinkedWord.endsWith("ಳ")||nounLinkedWord.endsWith("ರ")
						|| nounLinkedWord.endsWith("ನಲ್ಲಿ")||nounLinkedWord.endsWith("ಳಲ್ಲಿ")||nounLinkedWord.endsWith("ರಲ್ಲಿ")
						|| nounLinkedWord.endsWith("ನೇ")||nounLinkedWord.endsWith("ಳೇ")||nounLinkedWord.endsWith("ರೇ")) {
					System.out.println("Matched meaning is ವ್ಯಕ್ತಿ  ಅಥವಾ ಪ್ರಾಣಿ ಅಥವಾ ವಸ್ತು ಅಥವಾ ಜಾಗ");
				}
				break;
			} else if (nounWord.contains(polysemyWord)) {
				if (nounWord.endsWith("ನು")||nounWord.endsWith("ಳು")||nounWord.endsWith("ರು")
						|| nounWord.endsWith("ನನ್ನು")||nounWord.endsWith("ಳನ್ನು")||nounWord.endsWith("ರನ್ನು")
						|| nounWord.endsWith("ನನ್ನ")||nounWord.endsWith("ಳನ್ನ")||nounWord.endsWith("ರನ್ನ")
						|| nounWord.endsWith("ನಿಂದ")|| nounWord.endsWith("ಳಿಂದ")||nounWord.endsWith("ರಿಂದ")
						|| nounWord.endsWith("ನಿಗೆ")||nounWord.endsWith("ಳಿಗೆ")||nounWord.endsWith("ರಿಗೆ")
						|| nounWord.endsWith("ಗೆ")
						|| nounWord.endsWith("ನ")||nounWord.endsWith("ಳ")||nounWord.endsWith("ರ")
						|| nounWord.endsWith("ನಲ್ಲಿ")||nounWord.endsWith("ಳಲ್ಲಿ")||nounWord.endsWith("ರಲ್ಲಿ")
						|| nounWord.endsWith("ನೇ")||nounWord.endsWith("ಳೇ")||nounWord.endsWith("ರೇ")) {
					System.out.println("Matched meaning is ವ್ಯಕ್ತಿ  ಅಥವಾ ಪ್ರಾಣಿ ಅಥವಾ ವಸ್ತು ಅಥವಾ ಜಾಗ");
				}
				break;
			}
		}
	}

	private void readFromWordNet() {
		ReadFromWordnet readWordNet = new ReadFromWordnet();
		readWordNet.readFromWordNet(polysemyWord);

	}

	private void identifyPolysemyWord() throws IOException {
		List<String> inputWordsList = null;
		BufferedReader brInput = new BufferedReader(new FileReader("C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\sen31"));
		input = brInput.readLine();
		input = input.replaceAll("[-+.^:,]", "");
		String[] splitWords = input.split(" ");
		inputWordsList = Arrays.asList(splitWords);
		inputLinkedList = new LinkedList<String>();
		inputLinkedList.addAll(inputWordsList);

		for (int i = 0; i < inputLinkedList.size(); i++) {
			String word = inputLinkedList.get(i);
			inputLinkedList.remove(i);
			// System.out.println(linkedList);
			for (String otherToken : inputLinkedList) {
				int length = 0;
				if (word.length() <= otherToken.length()) {
					length = word.length();
				} else {
					length = otherToken.length();
				}
				int matchLength = 0;
				boolean prestine = true;
				for (int j = 0; j < length; j++) {
					if (word.charAt(j) == otherToken.charAt(j)) {
						continue;
					} else {
						matchLength = j;
						prestine = false;
						break;
					}
				}
				if (prestine) {
					polysemyWord = word;
					break;
				}
				if (matchLength >= 2) {
					polysemyWord = otherToken.substring(0, matchLength);
					break;
				}
			}
			inputLinkedList.add(i, word);
		}
		String rootMatchingWord = null;
		for (Entry<String, String> rootWord : rootWords.entrySet()) {
			int length = 0;
			String rootValue = rootWord.getValue();
			if (polysemyWord.length() <= rootValue.length()) {
				length = polysemyWord.length();
			} else {
				length = rootValue.length();
			}
			int matchLength = 0;
			boolean prestine = true;
			for (int j = 0; j < length; j++) {
				if (rootValue.charAt(j) == polysemyWord.charAt(j)) {
					continue;
				} else {
					matchLength = j;
					prestine = false;
					break;
				}
			}
			if (prestine) {
				String rootPrestineWord = rootValue;
				if (rootMatchingWord == null
						|| rootPrestineWord.length() <= rootMatchingWord
								.length()) {
					rootMatchingWord = rootPrestineWord;
				}
			}
			if (matchLength >= 2) {
				// String rootSubstring = rootValue.substring(0, matchLength);
				String rootSubstring = rootValue;
				if (rootMatchingWord == null
						|| rootSubstring.length() <= rootMatchingWord.length()) {
					rootMatchingWord = rootSubstring;
				}
			}
		}
		System.out.println("Polysemy word is " + rootMatchingWord);
		if (polysemyWord.equals(rootMatchingWord)) {
			polysemyWord = rootMatchingWord;
			secondaryPolysemyWord = rootMatchingWord;
		} else {
			secondaryPolysemyWord = polysemyWord;
			polysemyWord = rootMatchingWord;
		}

	}

	private void extractPOSTagging() throws IOException {
		rootWords = new LinkedHashMap<String, String>();
		List<String> inputWordsList = null;
		BufferedReader brInput = new BufferedReader(new FileReader("C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\sen31"));
		while (true) {
			String line = brInput.readLine();
			if (line == null)
				break;
			line = line.replaceAll("[-+.^:,]", "");
			String[] splitWords = line.split(" ");
			inputWordsList = Arrays.asList(splitWords);
			// System.out.println(inputWordsList);
		}
		word_pos_map = LinkedListMultimap.create();
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\out3"));
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;

			if (line.contains("Sentence") || line.contains("((")
					|| line.contains("))") || line.contains("SYM")) {
				continue;
			} else {
				// System.out.println(line);
				String[] split = line.split("<");

				// System.out.println(split[0]);
				String indexedToken = split[0];
				String metaToken = split[1];
				String[] metaTokenArray = metaToken.split(",");
				String[] metaData = metaTokenArray[0].split("'");
				String rootWord = metaData[1];

				String[] word_pos = indexedToken.split("\\s");
				/*
				 * System.out.println(word_pos[1]);
				 * System.out.println(word_pos[2]);
				 */
				word_pos_map.put(word_pos[1], word_pos[2]);
				rootWords.put(word_pos[1], rootWord);
			}

		}

		Collection<Entry<String, String>> entries = word_pos_map.entries();
		entries.forEach(item -> System.out.println(item.getKey() + " :: "
				+ item.getValue()));

	}

	@RequestMapping("preprocess")
	private String assignPOSTagging() throws JSchException, IOException {
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "password");
		TagPos schObj = new TagPos();
		schObj.sshCon(config);
		this.extractPOSTagging();
		this.identifyPolysemyWord();
		this.readFromWordNet();
		return "preprocessed";
	}

	private void obtainSemanticNet() throws IOException {
		BufferedReader brInput = new BufferedReader(new FileReader(
				"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\message.txt"));
		String input = "";
		while (true) {
			String line = brInput.readLine();
			if (line == null)
				break;
			input = input + line;
		}
		// System.out.println(input);
		String[] synsets = null;
		if (input.contains("----------------------------")) {
			synsets = input.split("----------------------------");
		}
		semanticsList = new ArrayList<String[]>();
		if (synsets != null) {

			for (String synset : synsets) {
				// System.out.println(synset);
				if (synset.contains("::::::")) {
					String[] synsetFragments = synset.split("::::::");
					String pos = synsetFragments[0];
					String synonyms = synsetFragments[1];
					String gloss = synsetFragments[2];
					String example = synsetFragments[3];
					example = example.replaceAll("[\".]", "");
					String semantics = synonyms + " " + gloss+ " "+example;
					String[] semanticUnitArray = new String[2];
					semanticUnitArray[0] = pos;
					semanticUnitArray[1] = semantics;
					semanticsList.add(semanticUnitArray);
				}
			}

			for (String[] semanticUnit : semanticsList) {
				System.out.println("POS = " + semanticUnit[0]);
				System.out.println("Semantic = " + semanticUnit[1]);
			}
		}
	}	
	
	public boolean endInstance(){
		input=null;
		polysemyWord=null;
		secondaryPolysemyWord=null;
		word_pos_map=null;
		semanticsList=null;
		inputLinkedList=null;
		matchedLongestWordForNoun=null;
		matchedSemanticSentenceForNoun=null;
		matchedLongestWordForVerb=null;
		matchedSemanticSentenceForVerb=null;
		rootWords=null;
		return true;
	}
}
