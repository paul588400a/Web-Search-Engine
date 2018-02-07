import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.awt.*;       // Using AWT layouts
import java.awt.event.*; // Using AWT event classes and listener interfaces
import javax.swing.*;    // Using Swing components and containers
import java.net.*;
import java.io.*;

public class SearchEngine extends JFrame{	   
	
	private JTextField txtSearch;  // Use Swing's JTextField instead of AWT's TextField
	private JFrame mainFrame;
	private JButton btnSearch;    // Using Swing's JButton instead of AWT's Button
	private int count = 0;
	
	public static void main(String args[]) throws IOException{
		
      // Run the GUI construction
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run(){
			 try{
            new SearchEngine(); // Let the constructor do the job
			 }catch(URISyntaxException e){
				 
			 }
         }
      });
	}
	
   public SearchEngine() throws URISyntaxException{
		class OpenUrlAction implements ActionListener {
		   URI uri;
		  @Override public void actionPerformed(ActionEvent e) {
			open(uri);
		  }
		  
		  public OpenUrlAction(String uri) throws URISyntaxException{
			  this.uri = new URI(uri);
		  }
		}
	   
      // Retrieve the content-pane of the top-level container JFrame
      // All operations done on the content-pane
      Container cont = getContentPane();
      cont.setLayout(new FlowLayout());   // The content-pane sets its layout
  	  
      txtSearch = new JTextField("");
      txtSearch.setPreferredSize(new Dimension(150, 30));
      txtSearch.setEditable(true);
      cont.add(txtSearch);
  	  
      btnSearch = new JButton("Search");
      btnSearch.setSize(20, 15);
      cont.add(btnSearch);
 
      // Allocate an anonymous instance of an anonymous inner class which
      // implements ActionListener as ActionEvent listener
      btnSearch.addActionListener(new ActionListener() {
      TrieExec TrieExec = new TrieExec();
    	  
         @Override
         public void actionPerformed(ActionEvent evt) {
        	mainFrame = new JFrame("Search Result");
        	mainFrame.setSize(800,600);
        	//mainFrame.setLayout(new GridLayout(0, 1));
        	if(txtSearch.getText().isEmpty()) return;
        	String line = txtSearch.getText().toLowerCase();
    		String[] searchWords = line.split(" ");
    		
    		// Create a trie based on company name and synonyms
    		HashMap<String, UrlAndWordCount> resultList = new HashMap<String, UrlAndWordCount>();
    		
    		// For each search word, get an occurrence list
    		for(String word : searchWords){
    			int index=  TrieExec.search(word);
    			HashMap<String, Integer> olist = null;
    			if(index != -1)
    				olist = Disk.occurrenceLists.get(index);
    			else
    				System.out.print("No result was found.");
    			
    			if(olist == null) continue;
    			
    			olist = Disk.sortDescByValues(olist);
    			for(Map.Entry<String, Integer> entry : olist.entrySet()){
    				if(!resultList.containsKey(entry.getKey())){
    					resultList.put(entry.getKey(), new UrlAndWordCount(1, entry.getValue()));
    				}else{
    					UrlAndWordCount wordAppearAndCount = resultList.get(entry.getKey());
    					wordAppearAndCount.wordAppear = resultList.get(entry.getKey()).wordAppear +1;
    					wordAppearAndCount.wordCount = resultList.get(entry.getKey()).wordCount + entry.getValue();
    					resultList.put(entry.getKey(), wordAppearAndCount);
    				}
    			}
    		}
    		resultList = Disk.SortResultListByValues(resultList);
        	
    		JPanel j = new JPanel();
    		JScrollPane jsp = new JScrollPane(j);
    		jsp.getVerticalScrollBar().setUnitIncrement(20);
    		j.setLayout(new GridLayout(0, 1));
    		mainFrame.add(jsp);
    		
    		// per
    		for(Map.Entry<String, UrlAndWordCount> entry : resultList.entrySet()){
    			String url = entry.getKey();
    			String title = Disk.urlTitleMap.get(url);
    			int wordAppear = entry.getValue().wordAppear;
    			int wordCount = entry.getValue().wordCount;
    			JButton jb = new JButton();
    			jb.setText("<HTML><FONT color=\"#000099\" size=\"4\">" + title + "</FONT>"
    					+ "<br><FONT color=\"#000099\" size=\"2\">" + url + "</FONT>"
    			        + "<br>Search Words Appear: " + wordAppear + "; Total Hits: " + wordCount + "</HTML>");
    			jb.setHorizontalAlignment(SwingConstants.LEFT);
    			jb.setBorderPainted(false);
    			jb.setOpaque(false);
    			jb.setBackground(Color.WHITE);
    			jb.setToolTipText(url.toString());
    			jb.setPreferredSize(new Dimension(300, 80));
    			try {
					jb.addActionListener(new OpenUrlAction(url));
				} catch (URISyntaxException e) {
					System.out.println("Error occured when open url!");
					e.printStackTrace();
				}
    			j.add(jb);
    		}
    		j.setVisible(true);
			mainFrame.setVisible(true);
         }
      });
 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close-window button clicked
      setTitle("Po-Hsun's search engine"); // "super" JFrame sets title
      setSize(400, 100);         // "super" JFrame sets initial size
      setVisible(true);          // "super" JFrame shows
   }
   
   private static void open(URI uri) {
	    if (Desktop.isDesktopSupported()) {
	      try {
	        Desktop.getDesktop().browse(uri);
	      } catch (IOException e) { /* TODO: error handling */ }
	    } else { /* TODO: error handling */ }
   }
}

///Used by occurrence list merge and the result list
class UrlAndWordCount{
	Integer wordAppear;
	Integer wordCount;
	public UrlAndWordCount(int u, int w){
		wordAppear = u;
		wordCount = w;
	}
}

/// Trie structure
class Trie{
	public Trie(char symbol){
		this.symbol = symbol;
		next = new ArrayList<Trie>();
		index = -1;
	}
	ArrayList<Trie> next;
	char symbol;
	int index;
}