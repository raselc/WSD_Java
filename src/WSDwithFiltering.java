import static java.lang.System.out;
import java.io.*;
import java.util.*;

/**
 * @author Rasel
 *	last modified: 06/06/2013
 */

//C:\Users\Rasel\workspace\regExp\src\accident.cor
//C:\Users\Rasel\workspace\regExp\src\train.txt
//C:\Users\Rasel\workspace\regExp\src\accident-n.eval
//C:\Users\Rasel\workspace\regExp\src\output.txt

/*
 * program starts here :)
 */

public class WSDwithFiltering {
	
	/**
	 * Initialization
	 */
	
    public static Scanner fin,fin2;
    public static PrintWriter fout,fsorted;
    public static int leftToPrint;
    public static boolean sawTag;
    public static String tag, lastWord = "DOESNOTEXIST", secondLastWord = "DOESNOTEXIST";
    public static Hashtable<Object, Object> trainingData = new Hashtable<Object, Object>();
    public static Hashtable<Object, Object> wordMean= new Hashtable<Object, Object>();
    public static Object item,tagCounter;
    public static Enumeration<Object> key,data,iterator;
    public static  wordSense[] result,result1 ;
    public static WSDwithFiltering wsd = new WSDwithFiltering();
    public static ArrayList<String> testWord = new ArrayList<String>();
    public static String finalWords="word not found",trainInput,testInput,trainOutput,testOutput;
    static BufferedReader inBuf = new BufferedReader(new InputStreamReader(System.in));
    public static int l=0;
    public static int totalWord=0;
    
    /*
     * Class for storing word and count   
     */
    class wordSense{
		int count;
		String sense;
		
		wordSense(int count, String bigram){
			this.count = count;
		 	this.sense = bigram;
		}
	}
    
    /*
     * Converts a string into lower case
     */
    public static String clean(String w) {
        while(w.length()>0 && !Character.isLetter(w.charAt(w.length()-1))) {
            w = w.substring(0, w.length()-1);
        }
        return w.toLowerCase();
    }
 
    /*
     * Finds the 2 words before and after the tag of the training
     */
    public static void trainRecord(String w) {
        w = clean(w);
        if (sawTag) {
            sawTag=false;
            putInTable(tag+" "+secondLastWord);                        
            putInTable(tag+" "+lastWord);
            leftToPrint = 2;
        }
        secondLastWord = lastWord;
        lastWord = w;
        if(leftToPrint>0) {
            --leftToPrint;
            putInTable(tag+" "+w);
         }
        
   }
    /*
     * Finds the 2 words before and after the tag of the testing
     */
    public static void testRecord(String w) {
        w = clean(w);
        if (sawTag) {
            sawTag=false;
            //out.println(secondLastWord);
            finalWords = secondLastWord;
           // out.println(lastWord);
            finalWords = finalWords + " "+ lastWord;
            leftToPrint = 2;
        }
        secondLastWord = lastWord;
        lastWord = w;
        if(leftToPrint>0) {
            --leftToPrint;
           // out.println(w);
            finalWords = finalWords + " "+ w;
            
        }
        
   }
    /*
     * Copies the word of the training and counts them 
     */
    void trainCopy(){
    	key = trainingData.keys();
		data= trainingData.elements();
		int index = 0;
		result = new wordSense[trainingData.size()];
						
		while(key.hasMoreElements()){
			data.hasMoreElements();
			result[index++] = new wordSense(((int[])data.nextElement())[0],(String)key.nextElement());
		}
	} 
    /*
     * Copies the tag of the training and counts them 
     */
    void tagCopy(){
    	key = wordMean.keys();
		data= wordMean.elements();
		int index = 0;
		result1 = new wordSense[wordMean.size()];
						
		while(key.hasMoreElements()){
			data.hasMoreElements();
			result1[index++] = new wordSense(((int[])data.nextElement())[0],(String)key.nextElement());
		}
    }
    
    /*
     * stores the word and the counts in the arraylist 
     */
    public static void putInTable(String data){
    	item = trainingData.get(data);
    	if (item != null)
			  ((int[])item)[0]++;
		else{
			int[] count = {1};
			trainingData.put(data,count);
		}
    	
    }
    /*
     * calculates the probability of a word along with the sense and generates the output
     * also applies the stop words
     */
    public static void calculateResult() throws Exception{
    	 	
    	String temp = null, temp1,answer = null;
    	int i=0;
    	double value,newValue = 0,totalSent=0; 
    	StringTokenizer token =new StringTokenizer(finalWords);
    	ArrayList<String> tokens = new ArrayList<String>();
    	while(token.hasMoreTokens()){
    		temp=token.nextToken();
    		tokens.add(temp);    		
    	}
    	while(i<result1.length){
    		totalSent += result1[i].count;
    		i++;
    	}
    	
    	if(finalWords != null){
    		
    		for(i=0;i<result1.length;i++ ){
    			value = (result1[i].count/totalSent);
    			for(int k=0;k<tokens.size();k++){
    				temp1 = result1[i].sense;
    				temp = tokens.get(k);
    				if((!temp.equals("and"))&&(!temp.equals("of")&&(!temp.equals("a")&&(!temp.equals("the")))))
    				{
    					//out.println(temp);
    				temp1= temp1 +" "+temp;
    				for(int j=0;j<result.length;j++){
    					
    					if(temp1.equals(result[j].sense)){
    						 value = value * ((result[j].count+0.5)/((result1[i].count*4)+(0.5*totalWord)));
    					}
    				}
    				value = value * 0.5/(0.5*totalWord);
    			}
    			out.println("Test sentence: "+l+" the probability of " +result1[i].sense +" is "+value);
    			if(value>newValue){
    				newValue=value;
    				answer = result1[i].sense;
    			}
    			}
    		}
    		l++;
    		fout.println(l+" Meaning of the word is: " + answer + " and the probability is " + newValue);
    		out.println(l+" Meaning of the word is: " + answer + " and the probability is " + newValue);
    		//out.println(answer);
    		
    		}
    	
    }
    /*
     * Takes test input, searches for the <tag, and passes the words to the calculate result
     */
    public static void testing() throws Exception{
	
    	System.out.println("Enter Testing input File Path:");
		testInput = inBuf.readLine();
		System.out.println("Enter Testing output File Path:");
		testOutput = inBuf.readLine();
		fout = new PrintWriter( new File( testOutput));
		
    	//fin = new Scanner( new File( "C:\\Users\\Rasel\\workspace\\regExp\\src\\accident-n.eval" ));
		fin = new Scanner( new File( testInput ));
        String[] splittedWords;
        String token;
        int i, tagLocation, count;
        
        while(fin.hasNext()) {
        	
            token = fin.next().trim();
            //out.println("READ: " + token);
            if (token.contains("<tag")) {
                if(!token.startsWith("<tag")) {
                    tagLocation = token.indexOf("<tag");
                    testRecord( token.substring(0, tagLocation) );
                }
               // out.println("Finally the testing Token: " +token);
                splittedWords = token.split("\"'|>|<|/"); 
                count = 0;
                for(i=0; i<splittedWords.length; ++i) {
                    if(splittedWords[i].length()>0) {
                        ++count;
                        if(count == 1) {
                            tag = splittedWords[i];
                            sawTag = true;
                        } else if (count > 3) { // skip the tag word
                            testRecord( splittedWords[i] );
                        }
                    }
                }
                calculateResult();
           } 
         else {
        	  testRecord( token );
        }
        }
        fin.close();
   }
    /*
     * Takes training input, searches for the <tag, finds the sense, generates the train output 
     */

    public static void training() throws Exception{
		System.out.println("Enter Training input File Path:");
		trainInput = inBuf.readLine();
		
		
		//fin = new Scanner( new File( "C:\\Users\\Rasel\\workspace\\regExp\\src\\accident.cor" ));
	    //fout = new PrintWriter( new File( "C:\\Users\\Rasel\\workspace\\regExp\\src\\Training_out.txt" ));
		fin = new Scanner( new File( trainInput ));
	    
	    String[] splittedWords;
	    String token;
	    int i, tagLocation, count;
	    
	    while(fin.hasNext()) {
	        token = fin.next().trim();
	        //out.println("READ: " + token);
	        totalWord++;
	        if (token.contains("<tag")) {
	            if(!token.startsWith("<tag")) {
	                tagLocation = token.indexOf("<tag");
	                trainRecord( token.substring(0, tagLocation) );
	            }
	            token = fin.next().trim();
	            
	            //out.println("Finally the Token: " +token);
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
	                        trainRecord( splittedWords[i] );
	                    }
	                }
	            }
	            //out.println("the tag: "+tag);
	            
	            tagCounter = wordMean.get(tag);
	            if(tagCounter!= null)
	            	((int[])tagCounter)[0]++;
	            else{
	    			int[] counter = {1};
	    			wordMean.put(tag,counter);
	    		}
	            	
	       } 
	       else {
	    	   trainRecord( token );
	       }
	        
	        
	    }
	    wsd.trainCopy();
	    /*for(i=0; i < result.length; i++){
			fout.println(result[i].sense +" "+ result[i].count );
			
		}*/
	    wsd.tagCopy();
	    /*for(i=0; i < result1.length; i++){
			out.println(result1[i].sense +" "+ result1[i].count );
		}*/
	    fin.close();
	   
	}

    //main of the program
    public static void main (String[] args) throws Exception {
     training();
     out.println("Training Done");
     testing();
     fout.close();
     out.println("Output generated");
     
     }
}
