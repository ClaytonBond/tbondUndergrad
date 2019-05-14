import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Search {
	
	public static ArrayList<String> filePaths;
	public static ArrayList[] 		mostLikely;
	public static ArrayList<String> reducedNames = new ArrayList<>();
	public static HashSet<String>   spellCheck   = new HashSet<>(500000);   	//List of words is ~400,000
	public static int[] index;

	public static void main(String[] args) throws IOException {
		filePaths = FileSearch.getPath();
		run("quicksort");
		System.out.println("Finished preprocessing data...");		
	}
	
	public static int[] run(String phrase) throws IOException {
		int size = 500;
		phrase = phrase.toLowerCase();
		index = findTopPaths(phrase, size);										//This contains the file paths for the top search results
		mostLikely = new ArrayList[index.length];
		
		
		for(int i = 0; i < index.length; i++) {
			mostLikely[i] = HTMLToken.cleanRecord(filePaths.get(index[i]));		//Fill ArrayList with the top choices tokenized
		}
		
		for(int i = 0; i < index.length; i++) {									//Clean up the tokens a little by spell checking to remove garbage
			//System.out.println(index[i] + ":\t" + filePaths.get(index[i]));
			for(int j = 0; j < mostLikely[i].size(); j++) {
				if(!spellCheck.contains(mostLikely[i].get(j))) {
					//System.out.println(mostLikely[i].get(j) + " \t Removed");
					mostLikely[i].remove(j);
				}
			}
		}
	
		index = deapSearch(index, phrase);

		
		FileWriter writer = new FileWriter("test.txt");
		for (int i = 0; i < mostLikely.length; i++) {
			writer.write(filePaths.get(index[i]) + ", " + mostLikely[i] + "\n");
		}
		writer.close();
		
		/*
		for(int i = 0; i < index.length; i++) {
			System.out.println(filePaths.get(index[i]));
		}
		*/
		
		return index;
	}
	
	//-Searches the HTML content for the closest match------------------------------------------------------------------
	public static int[] deapSearch(int[] index, String phrase) {
		@SuppressWarnings("rawtypes")
		HashMap[] list  = new HashMap[index.length];
		double[] result = new double[index.length];
		int[] words 	= new int[index.length];
		int[] documents = new int[index.length];
		int totalWords 	= 0;
		double  probability = 1;
		
		Tf_Idf(index);
		
		
		for(int i = 0; i < index.length; i++) {
			words[i] = mostLikely[i].size();
			documents[i] = words[i];
		}
		
		for(int i = 0; i < index.length; i++) {
			totalWords += words[i];
		}
		
		for(int i = 0; i < index.length; i++) {
			HashMap<String, Integer> frequency = new HashMap<>();
			
			//Build Frequency -----------------------------------------
			for(int j = 0; j < mostLikely[i].size(); j++) {
				if(frequency.containsKey(mostLikely[i].get(j))) {
					int temp = frequency.get(mostLikely[i].get(j)) + 1;
					frequency.put((String) mostLikely[i].get(j), temp);
				}
				else {
					frequency.put((String) mostLikely[i].get(j), 1);
				}	
			}
			
			list[i] = frequency;
		}
		
		
				
		for(int i = 0; i < index.length; i++) {
			
			String[] split = phrase.split(" ");
			//double ClassProb = Math.log(words[i] / totalWords); 					//This was left out intentionally as we don't care about the length.
			double WordProb  = 0;													//^P(w_i|c)		occurrences of w_i + 1 \ words in c + totalWords
			
			for(int j = 0; j < split.length; j++) {
				if(list[i].containsKey(split[j])) {
					WordProb += Math.log((int)list[i].get(split[j]) + 1 / (words[i] + totalWords));
				}
			}
			
			//System.out.println(i + ": " + WordProb);
			
			probability = WordProb;
			for(int k = 0; k < index.length; k++) {
				if(probability > result[k]) {
					/*for(int r = 0; r < index.length; r++) {
						System.out.print(result[r] + " hi");
						System.out.println();
					}*/
					double temp = result[k];
					result[k] = probability;
					result[k + 1] = temp;
					
					int temp2 = index[i];
					index[i] = index[k];
					index[k] = temp2;
					
					k = Integer.MAX_VALUE - 1;
				}
			}
		}
		
		return index;
		
	}

	public static void Tf_Idf(int[] index) {
		HashMap[] list  = new HashMap[index.length];
		int[] words 	= new int[index.length];
		int totalWords 	= 0;
		
		for(int i = 0; i < index.length; i++) 
			words[i] = mostLikely[i].size();
		
		for(int i = 0; i < index.length; i++) 
			totalWords += words[i];
		
		
		for(int i = 0; i < index.length; i++) {
			HashMap<String, Double> frequency = new HashMap<>();
			
			//Build initial Frequency -----------------------------------------
			for(int j = 0; j < mostLikely[i].size(); j++) {
				if(frequency.containsKey(mostLikely[i].get(j))) {
					double temp = frequency.get(mostLikely[i].get(j)) + 1;
					frequency.put((String) mostLikely[i].get(j), temp);
				}
				else {
					frequency.put((String) mostLikely[i].get(j), 1.0);
				}	
			}
			
			list[i] = frequency;
		}
		
		double average = 0;
		
		for(int i = 0; i < index.length; i++) {
			double tf = 0;
			double idf = 0;
			double tfidf = 0;
			for(Object word : list[i].keySet()) {
				tf = ((double)list[i].get(word.toString()) + 0.0) / (words[i] + 0.0);
				for(int j = 0; j < index.length; j++) {
					if(list[j].get(word.toString()) != null){
						idf++;
					}
				}
				idf = idf / index.length;
				tfidf = tf * idf;
				average += tfidf;
				
				list[i].put(word.toString(), tfidf);
				//System.out.println(word.toString() + "\t" + (double)list[i].get(word.toString()) + "\t\t" + tfidf);
			}
		}
		
		average = (average / totalWords) * 50;
		//System.out.println(average);
		
		for(int i = 0; i < index.length; i++) {
			for(Object word : list[i].keySet()) {
				if((double)list[i].get(word.toString()) > average){
					mostLikely[i].remove(word.toString());
					//System.out.println(word.toString() + "\t" + " Removed");
				}
			}
		}
	}

	//-Finds most likely pages and returns the top 10-------------------------------------------------------------------
	
	public static int[] findTopPaths(String phrase, int size) {
		String[] split = phrase.split(" ");
		int[][] prob = new int[reducedNames.size()][2];					//Holds the number of occurrences in a title and the index of that title
		int k = split.length + 1;										//Range of highest to lowest match, we can assume it will be no more than the length
		
		
		for(int i = 0; i < split.length; i++) {							//finds the title with the most word matches and stores it's index 
			for(int j = 0; j < reducedNames.size(); j++) {
				String[] parse = reducedNames.get(j).split(" ");
				for(String word : parse) {
					if(word.contains(split[i])) {
						prob[j][0]++;
						prob[j][1] = j;
					}
				}
			}
		}
		
		int[] result = new int[size];
		return TopPath(prob, result, size, k, 0);
	}
	
	public static int[] TopPath(int[][] prob, int[] result, int total, int max, int index) {
		for(int i = 0; i < reducedNames.size(); i++) {
			if(total != 0) {
				if(prob[i][0] == max) {								
					if(max == 0) {
						int rand = (int)(Math.random() * reducedNames.size());		//If we've run out of possible titles, choose at random
						result[index] = rand;
						index++;
						total--;
					}
					else {
						result[index] = prob[i][1];
						index++;
						total--;
					}
				}
				if(i == reducedNames.size() - 1) {
					max--;
					TopPath(prob, result, total, max, index);
				}
			}
			else {
				return result;
			}
		}
		
		return result;
	}
	
	//-Shortens down the file path to just the name of the file---------------------------------------------------------

	public static void loadReducer(ArrayList<String> filePaths) {
		for(String record : filePaths) {
			String[] split = record.split("/");
			
			String line = split[split.length - 1].toLowerCase();
			line = line.substring(0, line.length() - 5);
			String add = "";
			
			for(int i = 0; i < line.length(); i++) {
				if(line.charAt(i) == '-' || line.charAt(i) == '_') {
					add += " ";
				}
				else {
					add += line.charAt(i);
				}
			}
			reducedNames.add(add);
		}
	}
	
	//-Creates a HashSet of ~400,000 words to check against, this helps clean up our tokens-----------------------------
	
	public static void loadSpellChecker() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("words_alpha.txt"));
		String line;
		
		while((line = reader.readLine()) != null) {
			spellCheck.add(line);
		}
		
		reader.close();
	}
	
	public static int[] getIndex() {
		return index;
	}

}
