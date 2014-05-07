import static java.lang.System.out;
import java.io.*;
import java.util.*;


public class WSD_old {
    public static Scanner fin,fin2;
    public static PrintWriter fout,fsorted;
    public static int leftToPrint;
    public static boolean sawTag;
    public static String tag, lastWord = "DOESNOTEXIST", secondLastWord = "DOESNOTEXIST";
    public static Hashtable<Object, Object> table = new Hashtable<Object, Object>();
    public static Object item;
    public static Enumeration<Object> key,data;
    public static  wordSense[] result ;
    public static WSD_old wsd = new WSD_old();
    public static ArrayList<String> rows = new ArrayList<String>();
    
    class wordSense{
		int count;
		String sense;
		
		wordSense(int count, String bigram){
			this.count = count;
		 	this.sense = bigram;
		}
	}
    
    public static String clean(String w) {
        while(w.length()>0 && !Character.isLetter(w.charAt(w.length()-1))) {
            w = w.substring(0, w.length()-1);
        }
        return w.toLowerCase();
    }
 
    public static void record(String w) {
        w = clean(w);
         
        if (sawTag) {
            sawTag=false;
         //   fout.println(tag+" "+secondLastWord);
            out.println(tag+" "+secondLastWord+" ");
            putInTable(tag+" "+secondLastWord);                        
         //   fout.println(tag+" "+lastWord);
            out.println(tag+" "+lastWord);
            putInTable(tag+" "+lastWord);
            
            leftToPrint = 2;
        }
        secondLastWord = lastWord;
        lastWord = w;
        if(leftToPrint>0) {
            --leftToPrint;
         //   fout.println(tag+" "+w);
            putInTable(tag+" "+w);
            out.println(tag+" "+w);
        }
    }
    
    void copyValue(){
    	key = table.keys();
		data= table.elements();
		int index = 0;
		result = new wordSense[table.size()];
						
		while(key.hasMoreElements()){
			data.hasMoreElements();
			result[index++] = new wordSense(((int[])data.nextElement())[0],(String)key.nextElement());
		}
	} 
    
    public static void putInTable(String data){
    	item = table.get(data);
    	if (item != null)
			  ((int[])item)[0]++;
		else{
			int[] count = {1};
			table.put(data,count);
		}
    	
    }
    
    public static void tagFinder() throws Exception{
    	fin = new Scanner( new File( "C:\\Users\\Rasel\\workspace\\regExp\\src\\b.txt" ));
        fout = new PrintWriter( new File( "C:\\Users\\Rasel\\workspace\\regExp\\src\\outt.txt" ));
        String[] splittedWords;
        String token;
        int i, tagLocation, count;
        
        while(fin.hasNext()) {
            token = fin.next().trim();
            out.println("READ: " + token);
            if (token.contains("<tag")) {
                if(!token.startsWith("<tag")) {
                    tagLocation = token.indexOf("<tag");
                    record( token.substring(0, tagLocation) );
                }
                token = fin.next().trim();
                out.println("Finally the Token: " +token);
                splittedWords = token.split("\"|>|<|/"); 
                tag = "tag";
                count = 0;
                for(i=0; i<splittedWords.length; ++i) {
                    if(splittedWords[i].length()>0) {
                        ++count;
                        if(count == 1) {
                            tag = splittedWords[i];
                            sawTag = true;
                        } else if (count > 2) { // skip the tag word
                            record( splittedWords[i] );
                        }
                    }
                }
           } 
           else {
        	   record( token );
           }
        }
        wsd.copyValue();
        
        for(i=0; i < result.length; i++){
			fout.println(result[i].sense +" "+ result[i].count );
			
		}
        fin.close();
        fout.close();
    }
    public static void sort() throws Exception{
    	ArrayList<String> rows = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Rasel\\workspace\\regExp\\src\\out.txt"));
        StringTokenizer token;
        String delims = "< .,\" :;'`" , token1 = null, token2 = null, token3 = null;
        String s;
        s= reader.readLine();
        int num=1;
        while(s != null){
        	/*token = new StringTokenizer(s,delims );
        	if(token.hasMoreTokens())
				  token1 = token.nextToken();
			  if(token.hasMoreTokens())
				  token2 = token.nextToken();
			  if(token.hasMoreTokens())
				  token3 = token.nextToken();
			  s = token1+" "+ token2+" "+ token3;*/
        	rows.add(s);
        	s= reader.readLine();
        	System.out.println(num);
        	num++;
        }

        Collections.sort(rows);
        num =1;
        FileWriter writer = new FileWriter("C:\\Users\\Rasel\\workspace\\regExp\\src\\sortedout.txt");
        BufferedWriter bw = new BufferedWriter(writer);
        for(int a=0;a<rows.size();a++){
        	bw.write(rows.get(a));
        	bw.newLine();
        	out.println(num);
        	num++;
        }

        reader.close();
        writer.close();
    }
    
    
    public static void main (String[] args) throws Exception {
     tagFinder();  
     //sort();
     
     
    }
}
