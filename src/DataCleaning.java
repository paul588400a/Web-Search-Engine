import java.util.*;

public class DataCleaning{
	
	// Create a stop word list
	static HashSet<String> stopWords = new HashSet<String>();
	static void buildStopWords(){
		String[] stopList = {"a","an","the","and","or","but","i","he","him","her",""};
		for(String s : stopList){
			stopWords.add(s);
		}
	}
	
	// stop word judgement
    static boolean isStopWords(String word){
    	return stopWords.contains(word);
    }
}