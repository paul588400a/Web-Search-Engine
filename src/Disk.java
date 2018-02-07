import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/// to imitate data on the disk
class Disk{
	// store the occurrence list of all words
	public static ArrayList<HashMap<String, Integer>> occurrenceLists = new ArrayList<HashMap<String, Integer>>();

	// url and title mapping
	public static HashMap<String, String> urlTitleMap = new HashMap<String, String>();
	
	static HashMap sortDescByValues(HashMap map) { 
		List list = new LinkedList(map.entrySet());
	    // Defined Custom Comparator here
	    Collections.sort(list, new Comparator() {
	    	public int compare(Object o1, Object o2) {
	    		return ((Comparable) ((Map.Entry<String, Integer>) (o2)).getValue())
	            .compareTo(((Map.Entry<String, Integer>) (o1)).getValue());
	    	}
	    });
	    // Here I am copying the sorted list in HashMap
	    // using LinkedHashMap to preserve the insertion order
	    HashMap<String, Integer> sortedHashMap = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	    	Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)it.next();
	    	sortedHashMap.put(entry.getKey(), entry.getValue());
	    } 
	    return sortedHashMap;
	}
	
	/// sort result list which to be displayed on the search result
	static HashMap SortResultListByValues(HashMap map) { 
		List list = new LinkedList(map.entrySet());
	    // Defined Custom Comparator here
	    Collections.sort(list, new Comparator() {
	    	public int compare(Object o1, Object o2) {
	    		
	    		 if(((Comparable) ((Map.Entry<String, UrlAndWordCount>) (o2)).getValue().wordAppear)
	            .compareTo(((Map.Entry<String, UrlAndWordCount>) (o1)).getValue().wordAppear) == 0){
	    			 return ((Comparable) ((Map.Entry<String, UrlAndWordCount>) (o2)).getValue().wordCount)
	    			            .compareTo(((Map.Entry<String, UrlAndWordCount>) (o1)).getValue().wordCount);
	    		 }else{
	    			 return ((Comparable) ((Map.Entry<String, UrlAndWordCount>) (o2)).getValue().wordAppear)
	 	            .compareTo(((Map.Entry<String, UrlAndWordCount>) (o1)).getValue().wordAppear);
	    		 }
	    	}
	    });
	    // Here I am copying the sorted list in HashMap
	    // using LinkedHashMap to preserve the insertion order
	    HashMap<String, UrlAndWordCount> sortedHashMap = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	    	Map.Entry<String, UrlAndWordCount> entry = (Map.Entry<String, UrlAndWordCount>)it.next();
	    	sortedHashMap.put(entry.getKey(), entry.getValue());
	    } 
	    return sortedHashMap;
	}
}