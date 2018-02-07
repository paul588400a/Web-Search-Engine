import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//all trie operation including trie build and trie search
public class TrieExec{

    ArrayList<String> urls = new ArrayList<String>();
    
    // initialize a new root
	private Trie root = new Trie('\0');
	
	public TrieExec(){
		DataCleaning.buildStopWords();
		Path path = Paths.get(System.getProperty("user.dir")+"\\websites.dat");
		
		
        try (BufferedReader bf = Files.newBufferedReader(path)){
            String line = bf.readLine();
            while(line != null){
            	urls.add(line);
                line = bf.readLine();
            }

    		System.out.println("Building Trie....");
    		
    		// get title and words of each url and create trie nodes
            for(String url : urls){
            	Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                String[] words = doc.text().replaceAll("\\s*\\p{Punct}+\\s*$", "").toLowerCase().split(" ");
                
                Disk.urlTitleMap.put(url, doc.title());
                CreateTrie(words, url);
                System.out.println(title + " --- " + url);
            }
            System.out.println("=============== Trie Created ==============");
        }
        catch (Exception ex){

        }
	}
	
	// Access occurrence list during trie build process
	public int occurrencelistExec(String url, int index){
		if(index == -1){
			index = Disk.occurrenceLists.size();
			HashMap<String, Integer> occurrencelist = new HashMap<String, Integer>();
			occurrencelist.put(url, 1);
			Disk.occurrenceLists.add(occurrencelist);
		}else if(index >= 0){
			HashMap<String, Integer> occurrencelist = Disk.occurrenceLists.get(index);
			if(!occurrencelist.containsKey(url))
				occurrencelist.put(url,1);
			else
				occurrencelist.put(url, occurrencelist.get(url) +1);
			Disk.occurrenceLists.set(index, occurrencelist);
		}
		return index;
	}
	
	
	public void CreateTrie(String[] words, String url){
		for(String word : words){
			
			// eliminate stop words
			if(DataCleaning.isStopWords(word))
				continue;
			
			List<Trie> cur = root.next;
			for(int i=0; i< word.length(); i++){
				boolean isNewSymbol = true;
				for(Trie ari : cur){
					// if symbol is found
					if(ari.symbol == word.charAt(i)){
						
						// if we reach the last character
						if(i == word.length() -1){
							// Access occurrence list during trie build process
							ari.index = occurrencelistExec(url, ari.index);
						}
						
						cur = ari.next;
						isNewSymbol = false;
						break;
					}
				}
				
				// if symbol is not found at any of child node, we should create a new one
				if(isNewSymbol){
					Trie arity = new Trie(word.charAt(i));
					if(i == word.length() -1){
						// Access occurrence list during trie build process
						arity.index = occurrencelistExec(url, arity.index);
					}
					cur.add(arity);
					cur = arity.next;
				}
			}
		}
	}
    
    //return the index of the occurrence list 
    public int search(String word){
		List<Trie> trie = root.next;
		int i=0;
		
		// for each character in the word
		while(i < word.length()){
			
			// if a given character in the word is found in the trie
			boolean isFound = false;
			
			// for each node in a trie, find if any of those symbol match current word 
			for(Trie node : trie){
				if(node.symbol == word.charAt(i)){
					isFound = true;
					trie = node.next;
				}
				
				if(i == word.length()-1 && isFound){
					return node.index;
				}
				
				if(isFound) break;
			}
			if(!isFound) return -1;
			i++;
		}
		return -1;
    }
}