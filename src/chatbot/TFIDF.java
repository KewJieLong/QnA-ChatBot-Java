/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author kewjielong
 */
public class TFIDF {
    private static final String DOCUMENTSPATH = "documents.csv";
    private static ArrayList<String> docs = new ArrayList<>();
    private static HashMap<String, Double> wordIDF = new HashMap<>();             
    private static HashMap<String, Integer> wordFeq = new HashMap<>();
    private static ArrayList <HashMap<String, Double>>collectionTFDoc = new ArrayList();
    private static Utilities ult = new Utilities();
    private static Core core = new Core();
    
    private static ArrayList <String> commomWord = new ArrayList(){{
        add("is");
        add("are");
        add("was");
        add("were");        
        add("when");
        add("what");
        add("who");
        add("why");
        add("where");
        add("how");              
    }};;
    private static int line = 0;
    
    public TFIDF(){        
        initSetup(DOCUMENTSPATH);
        System.out.println("Docs = " + docs.toString());
    }
    
    public void initSetup(String path){
        File file = new File(path);        
        if(file.exists()){
            try{
                Scanner inputStream = new Scanner(file);                
                while(inputStream.hasNext()){                    
                    String data = inputStream.nextLine();
//                    System.out.println(data);
                    addDoc(data, false, false);
                    line ++ ;                    
                }            
            } catch (FileNotFoundException e){
                System.out.println(e.getMessage());
            }
                        
            // calculate all the IDF for each word
            for(String key: wordFeq.keySet()){
                wordIDF.put(key, Math.log((double)line / (double)wordFeq.get(key)));
            }                       
        }                      
    }
            
    public static int getHighestTFIDFIndex(String term){
        Double maxTfIdf = 0.0;
        int maxIndex = 0;
        int index = 0;
        if(collectionTFDoc.isEmpty()){
            return -1;
        }
        
        for(HashMap hm: collectionTFDoc){               
            if(hm.containsKey(term)){
                System.out.println("calculating " + term);
                System.out.println("TFDoc: " + hm.get(term) );
                System.out.println("IDFWord: " + wordIDF.get(term));
                double TfIdf = (double)hm.get(term) * wordIDF.get(term);
                if(maxTfIdf < TfIdf){
                    maxTfIdf = TfIdf;
                    maxIndex = index;
                }
            }         
            index ++;
        }
        
        return maxIndex;
    }
    
    public static String getHighestTFIDFDoc(String term){
        Double maxTfIdf = 0.0;
        int maxIndex = 0;
        int index = 0;
        for(HashMap hm: collectionTFDoc){                        
            if(hm.containsKey(term)){
                double TfIdf = (double)hm.get(term) * wordIDF.get(term);
                if(maxTfIdf < TfIdf){
                    maxTfIdf = TfIdf;
                    maxIndex = index;
                }
            }         
            index ++;
        }
        
        return docs.get(maxIndex);
    }
    
    public static String getDoc(int index){
        return docs.get(index);
    }
    
    public static String getMostReleventDoc(String[]tokens){        
        ArrayList <Integer> docsIndex = new ArrayList<>();        
        for(String t:tokens){
            if(!commomWord.contains(t)){
                System.out.println("");
                System.out.println("no common word: " + t);
                int hindex = getHighestTFIDFIndex(t);
                if(hindex >= 0){
                    docsIndex.add(hindex);                          
                }                
            }            
        }
        
        System.out.println("docsIndex: " );
        for(int i: docsIndex){
            System.out.println(docs.get(i));
        }
        
//        ult.printArray(docsIndex);

        if(docsIndex.isEmpty()){
            return "";
        }
        int [] dIndex = core.arrayListToArray(docsIndex);
        int mostFeqI = core.mostFrequent(dIndex);
        
        return docs.get(mostFeqI);                        
    }
    
    public static void addDoc(String sentence, boolean calIDF, boolean saveFile){
        // add doc only when docs do not have this sentence 
        if(!docs.contains(sentence)){
            line++;
            try{
                sentence = sentence.replaceAll("[^A-Za-z]+", " ");
                String [] words = sentence.split(" ");
                docs.add(sentence);

                HashMap<String, Integer> wordFeqDoc = new HashMap<>();
                HashMap<String, Double> TFDoc = new HashMap<>();
                for(String s: words){
                    String w = s.toLowerCase();

                    // WordFeq for ALL Document
                    if(wordFeq.containsKey(w)){                        
                        wordFeq.put(w, wordFeq.get(w) + 1);                                                
                    }else{
                        wordFeq.put(w, 1);
                    }

                    // WordFeq for CURRENT Document
                    if(wordFeqDoc.containsKey(w)){
                        wordFeqDoc.put(w, wordFeqDoc.get(w) + 1);
                    }else{
                        wordFeqDoc.put(w, 1);
                    }

                }  

                for(String key: wordFeqDoc.keySet()){
    //                System.out.println("key = " + key);
    //                System.out.println((wordFeqDoc.get(key) / (double)words.length));
                    TFDoc.put(key, (wordFeqDoc.get(key) / (double)words.length));
                }                                
                collectionTFDoc.add(TFDoc);

                // calculate all the IDF for each word
                if(calIDF){        
                    System.out.println("Cal IDF word");
                    for(String key: wordFeq.keySet()){
//                        System.out.println(key);                        
//                        System.out.println((double)line);                        
//                        System.out.println("wordfeq : " + wordFeq.get(key));
                        wordIDF.put(key, Math.log((double)line / (double)wordFeq.get(key)));
                    }                          
                }            

                if(saveFile){
                    PrintWriter pw = new PrintWriter(new FileOutputStream(DOCUMENTSPATH, true));
                    pw.write(sentence.toLowerCase());
                    pw.write("\n");
                    pw.close();
                }            

            } catch (FileNotFoundException e){
                System.out.println(e.getMessage());
            }
            
            System.out.println("Docs = " + docs.toString());
        }
        
        System.out.println("total line:" + line);
    }
}
