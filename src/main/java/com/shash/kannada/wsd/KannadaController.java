package com.shash.kannada.wsd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jcraft.jsch.JSchException;

@RestController
@RequestMapping("/kannada2/")
public class KannadaController {

	private String input;

	private String polysemyWord;

	private String secondaryPolysemyWord;

	private Multimap<String, String> word_pos_map;

	private List<String[]> semanticsList;

	private Collection<Map<String, String>> semanticSet;

	private LinkedList<String> inputLinkedList;

	private String matchedLongestWordForNoun;

	private String matchedSemanticSentenceForNoun;

	private String matchedLongestWordForVerb;

	private String matchedSemanticSentenceForVerb;

	private Map<String, String> rootWords;

	private List<String> rootList;
	
	private Multimap<String, String> keyPosRootMap; 
	
	private String inputPolysemyWord; 
	
	private String inputPolysemyPosTag;

	@RequestMapping("semantics")
	private String semantics() throws IOException {
		BufferedReader brInput = new BufferedReader(
				new FileReader(
						"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\message.txt"));
		String input = "";
		while (true) {
			String line = brInput.readLine();
			if (line == null)
				break;
			input = input + line;
		}
		String[] synsets = null;
		if (input.contains("----------------------------")) {
			synsets = input.split("----------------------------");
		}

		semanticSet = new ArrayList<Map<String, String>>();
		if (synsets != null) {
			String[] semanticValues = null;

			for (String synset : synsets) {
				// System.out.println(synset);
				if (synset.contains("::::::")) {
					String[] synsetFragments = synset.split("::::::");
					String posWord = synsetFragments[0];
					String targetWord = synsetFragments[1];
					this.polysemyWord = targetWord;
					String semantics = synsetFragments[2];
					Map<String, String> semanticPosMap = new HashMap<String, String>();
					semanticPosMap.put(semantics, posWord);
					semanticSet.add(semanticPosMap);
				}
			}
			for (Map<String, String> posSemanticMapObj : semanticSet) {
				Iterator<Entry<String, String>> iterator = posSemanticMapObj
						.entrySet().iterator();
				Entry<String, String> next = iterator.next();
				System.out.println("Semantics = " + next.getKey());
				System.out.println("POS = " + next.getValue());
			}
		}
		return "semantics obtained";
	}

	private String innondAataEegaShuru() throws IOException {
		BufferedReader brInput = new BufferedReader(
				new FileReader(
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
			String[] semanticValues = null;
			for (String synset : synsets) {
				// System.out.println(synset);
				if (synset.contains("::::::")) {
					String[] synsetFragments = synset.split("::::::");
					String posWord = synsetFragments[0];
					String targetWord = synsetFragments[1];
					String semantics = synsetFragments[2];
					String[] semanticUnitArray = new String[2];
					semanticUnitArray[0] = posWord;
					semanticUnitArray[1] = semantics;
					semanticsList.add(semanticUnitArray);
				}
			}
			boolean nounFlag = false;
			boolean verbFlag = false;
			for (String[] semanticUnit : semanticsList) {
				System.out.println("POS = " + semanticUnit[0]);
				System.out.println("Semantic = " + semanticUnit[1]);
				if (semanticUnit[0].equals("noun")) {
					nounFlag = true;
					String semanticValue = semanticUnit[1];
					semanticValue = semanticValue.replaceAll("[\".,()]", "");
					if (semanticValue.contains(rootList.get(0))) {
						semanticValue = semanticValue.replaceAll(
								rootList.get(0), "");
					}
					semanticValues = semanticValue.split(" ");
					for (int i = 0; i < semanticValues.length; i++) {
						System.out.println("********" + semanticValues[i]
								+ "********");
					}
				}
				if (semanticUnit[0].equals("verb")) {
					verbFlag = true;
				}
			}
			if (nounFlag == true && verbFlag == true) {
				System.out.println("Polysemy Word is " + rootList.get(0));
			}

		}
		return "semantics obtained";
	}

	@RequestMapping("disambiguate")
	private String aataEegaShuru() throws IOException {
		List<String> inputWordsList = null;
		String matchingWordForNoun = null;
		BufferedReader brInput = new BufferedReader(
				new FileReader(
						"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\sen31"));
		input = brInput.readLine();
		input = input.replaceAll("[-+.^:,]", "");
		String[] splitWords = input.split(" ");
		inputWordsList = Arrays.asList(splitWords);
		inputLinkedList = new LinkedList<String>();
		inputLinkedList.addAll(inputWordsList);
		System.out.println("*********************");
		System.out.println("Input is " + input.toString());
		System.out.println("Polysemy word = " + polysemyWord);
		Collection<Entry<String, String>> entries = word_pos_map.entries();
		entries.forEach(item -> {
			System.out.println(item.getKey() + " :: "+ item.getValue());
		});
		for (Map<String, String> posSemanticMapObj : semanticSet) {
			Iterator<Entry<String, String>> iterator = posSemanticMapObj
					.entrySet().iterator();
			Entry<String, String> next = iterator.next();
			System.out.println("Semantics = " + next.getKey());
			System.out.println("POS = " + next.getValue());
		}
		System.out.println("*********************");
		Collection<Entry<String, String>> posRootEntries = keyPosRootMap.entries();
		boolean rootPosFlag = false;
		for(Entry<String,String> posRootEntry:posRootEntries){
			String posRootKey = posRootEntry.getKey();
			String posRootValue = posRootEntry.getValue();
			if(posRootValue.equals(polysemyWord)||rootPosFlag==true){
				System.out.println("**** Root POS ****"+posRootValue);
				if(rootPosFlag){
					inputPolysemyPosTag = posRootValue;
					rootPosFlag = false;
				}
				else{
					rootPosFlag = true;
					inputPolysemyWord = posRootKey;
				}
				
			}
		}
		System.out.println("**** Root POS Polysemy word ****"+inputPolysemyWord);
		System.out.println("**** Root POS Polysemy POS Tag ****"+inputPolysemyPosTag);
		if (inputPolysemyPosTag.startsWith("V")) {
			for (Map<String, String> posSemanticMapObj : semanticSet) {
				Iterator<Entry<String, String>> iterator = posSemanticMapObj.entrySet().iterator();
				Entry<String, String> next = iterator.next();
				String pos = next.getValue();
				String semantic = next.getKey();
				if (pos.equals("verb")) {
					matchedSemanticSentenceForVerb = semantic;
					System.out.println("Matched Meaning is "
							+ matchedSemanticSentenceForVerb);
					this.endInstance();
					return "disambiguated";
				}
			}
		}
		else if (inputPolysemyPosTag.startsWith("N")) {
			for (Map<String, String> posSemanticMapObj : semanticSet) {
				Iterator<Entry<String, String>> iterator = posSemanticMapObj
						.entrySet().iterator();
				Entry<String, String> next = iterator.next();
				String pos = next.getValue();
				String semantic = next.getKey();
				if (pos.equals("noun")) {
					System.out.println("******Inside noun*********");
					String inputWithoutPolysemyWord = input.replaceAll(
							polysemyWord, "");
					System.out.println(inputWithoutPolysemyWord);
					String[] splitInputList = inputWithoutPolysemyWord.split(" ");
					for (String inputWord : splitInputList) {
						System.out.println(inputWord);
					}
					System.out.println("************");
					String[] nounSplitSemanticWords = semantic.split(" ");
					for (String nounInputSemanticWord : nounSplitSemanticWords) {
						System.out.println(nounInputSemanticWord);
					}
					for (int i = 0; i < splitInputList.length; i++) {
						for (int j = 0; j < nounSplitSemanticWords.length; j++) {
							String inputWord = splitInputList[i];
							if(inputWord.length()==0){
								continue;
							}
							String semanticWord = nounSplitSemanticWords[j];
							if(semanticWord.length()==0){
								continue;
							}
							int length = 0;
							if (inputWord.length() <= semanticWord.length()) {
								length = inputWord.length();
							} else {
								length = semanticWord.length();
							}
							int matchLength = 0;
							boolean prestine = true;
							for (int x = 0; x < length; x++) {
								if (inputWord.charAt(x) == semanticWord.charAt(x)) {
									continue;
								} else {
									matchLength = x;
									prestine = false;
									break;
								}
							}
							if (prestine) {
								matchingWordForNoun = inputWord;
								// if(!matchingWordForNoun.contains(polysemyWord)){
								if (matchedLongestWordForNoun == null
										|| matchingWordForNoun.length() > matchedLongestWordForNoun
												.length()) {
									matchedLongestWordForNoun = matchingWordForNoun;
									matchedSemanticSentenceForNoun = semantic;
								}
								break;
								// }
							}
							if (matchLength > 2) {
								matchingWordForNoun = semanticWord.substring(0,
										matchLength);
								// if(!matchingWordForNoun.contains(polysemyWord)){
								if (matchedLongestWordForNoun == null
										|| matchingWordForNoun.length() > matchedLongestWordForNoun
												.length()) {
									matchedLongestWordForNoun = matchingWordForNoun;
									matchedSemanticSentenceForNoun = semantic;
								}
								break;
								// }
	
							}
						}
						System.out.println("************");
					}
				}
			}
		}
		if (matchedLongestWordForNoun != null)
			System.out.println("Matching word is "+ matchedLongestWordForNoun);
		if (matchedSemanticSentenceForNoun != null)
			System.out.println("Matched meaning is "+ matchedSemanticSentenceForNoun);
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
				if (nounLinkedWord.endsWith("ನು")
						|| nounLinkedWord.endsWith("ಳು")
						|| nounLinkedWord.endsWith("ರು")
						|| nounLinkedWord.endsWith("ನನ್ನು")
						|| nounLinkedWord.endsWith("ಳನ್ನು")
						|| nounLinkedWord.endsWith("ರನ್ನು")
						|| nounLinkedWord.endsWith("ನನ್ನ")
						|| nounLinkedWord.endsWith("ಳನ್ನ")
						|| nounLinkedWord.endsWith("ರನ್ನ")
						|| nounLinkedWord.endsWith("ನಿಂದ")
						|| nounLinkedWord.endsWith("ಳಿಂದ")
						|| nounLinkedWord.endsWith("ರಿಂದ")
						|| nounLinkedWord.endsWith("ನಿಗೆ")
						|| nounLinkedWord.endsWith("ಳಿಗೆ")
						|| nounLinkedWord.endsWith("ರಿಗೆ")
						|| nounLinkedWord.endsWith("ಗೆ")
						|| nounLinkedWord.endsWith("ನ")
						|| nounLinkedWord.endsWith("ಳ")
						|| nounLinkedWord.endsWith("ರ")
						|| nounLinkedWord.endsWith("ನಲ್ಲಿ")
						|| nounLinkedWord.endsWith("ಳಲ್ಲಿ")
						|| nounLinkedWord.endsWith("ರಲ್ಲಿ")
						|| nounLinkedWord.endsWith("ನೇ")
						|| nounLinkedWord.endsWith("ಳೇ")
						|| nounLinkedWord.endsWith("ರೇ")) {
					System.out
							.println("Matched meaning is ವ್ಯಕ್ತಿ  ಅಥವಾ ಪ್ರಾಣಿ ಅಥವಾ ವಸ್ತು ಅಥವಾ ಜಾಗ");
				}
				break;
			} else if (nounWord.contains(polysemyWord)) {
				if (nounWord.endsWith("ನು") || nounWord.endsWith("ಳು")
						|| nounWord.endsWith("ರು")
						|| nounWord.endsWith("ನನ್ನು")
						|| nounWord.endsWith("ಳನ್ನು")
						|| nounWord.endsWith("ರನ್ನು")
						|| nounWord.endsWith("ನನ್ನ")
						|| nounWord.endsWith("ಳನ್ನ")
						|| nounWord.endsWith("ರನ್ನ")
						|| nounWord.endsWith("ನಿಂದ")
						|| nounWord.endsWith("ಳಿಂದ")
						|| nounWord.endsWith("ರಿಂದ")
						|| nounWord.endsWith("ನಿಗೆ")
						|| nounWord.endsWith("ಳಿಗೆ")
						|| nounWord.endsWith("ರಿಗೆ") || nounWord.endsWith("ಗೆ")
						|| nounWord.endsWith("ನ") || nounWord.endsWith("ಳ")
						|| nounWord.endsWith("ರ") || nounWord.endsWith("ನಲ್ಲಿ")
						|| nounWord.endsWith("ಳಲ್ಲಿ")
						|| nounWord.endsWith("ರಲ್ಲಿ")
						|| nounWord.endsWith("ನೇ") || nounWord.endsWith("ಳೇ")
						|| nounWord.endsWith("ರೇ")) {
					System.out
							.println("Matched meaning is ವ್ಯಕ್ತಿ  ಅಥವಾ ಪ್ರಾಣಿ ಅಥವಾ ವಸ್ತು ಅಥವಾ ಜಾಗ");
				}
				break;
			}
		}
	}

	private void readFromWordNet() {
		/*
		 * ReadFromWordnet readWordNet = new ReadFromWordnet();
		 * readWordNet.readFromWordNet(polysemyWord);
		 */

	}

	private void identifyPolysemyWord() throws IOException {
		List<String> inputWordsList = null;
		BufferedReader brInput = new BufferedReader(
				new FileReader(
						"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\sen31"));
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
		BufferedReader brInput = new BufferedReader(
				new FileReader(
						"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\sen31"));
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
		keyPosRootMap = LinkedListMultimap.create();
		BufferedReader br = new BufferedReader(
				new FileReader(
						"C:\\Users\\shashank\\Documents\\wsd\\wsd\\src\\main\\resources\\out3"));
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
				keyPosRootMap.put(word_pos[1], rootWord);
				keyPosRootMap.put(word_pos[1], word_pos[2]);
			}

		}

		Collection<Entry<String, String>> entries = word_pos_map.entries();
		entries.forEach(item -> System.out.println(item.getKey() + " :: "
				+ item.getValue()));
		
		Collection<Entry<String, String>> posRootentries = keyPosRootMap.entries();
		posRootentries.forEach(item -> System.out.println(item.getKey() + " :: "
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
		this.processRootWords();
		return "preprocessed";
	}

	private void processRootWords() {
		this.deleteExistingFiles();
		ReadFromWordnet readWordNet = new ReadFromWordnet();
		rootList = new ArrayList<String>();
		Set<Entry<String, String>> rootWordEntires = rootWords.entrySet();
		AtomicInteger count = new AtomicInteger(0);
		rootWordEntires.forEach((item) -> {
			System.out.println(item.getKey() + " :: " + item.getValue());
			rootList.add(item.getValue());
			readWordNet.readFromWordNet(item.getValue(),
					"semantic" + count.incrementAndGet() + ".txt");
		});
	}

	private boolean deleteExistingFiles() {
		boolean flag = true;
		int i = 0;
		while (flag) {
			try {
				i++;
				Files.delete(Paths
						.get("C:\\Users\\shashank\\Documents\\inh\\KannadaPolysemyImplementation3\\semantic"
								+ i + ".txt"));
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
		}

		return true;
	}

	private void obtainSemanticNet() throws IOException {
		BufferedReader brInput = new BufferedReader(
				new FileReader(
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
					String semantics = synonyms + " " + gloss + " " + example;
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

	public boolean endInstance() {
		input = null;
		polysemyWord = null;
		secondaryPolysemyWord = null;
		word_pos_map = null;
		semanticsList = null;
		inputLinkedList = null;
		matchedLongestWordForNoun = null;
		matchedSemanticSentenceForNoun = null;
		matchedLongestWordForVerb = null;
		matchedSemanticSentenceForVerb = null;
		rootWords = null;
		return true;
	}
}
