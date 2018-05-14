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
import java.util.*;

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

    public static int getDocsSize(){
        return collectionTFDoc.size();
    }

    public static int [] getNHighestTFIDFIndex(String term, int top){
        int [] large = new int[top];
        if(collectionTFDoc.isEmpty()){
            return large;
        }

        HashMap<Integer, Double> termTf = new HashMap<>();
        for(int i = 0; i < collectionTFDoc.size(); i ++){
            if(collectionTFDoc.get(i).containsKey(term)){
                termTf.put(i, collectionTFDoc.get(i).get(term));
            }
        }

        if(termTf.isEmpty()){
            Arrays.fill(large, -1);
            return large;
        }

        double max = 0;
        int maxIndex = -1;
        for(int i = 0; i < large.length; i ++){
            for(Map.Entry<Integer, Double> entry: termTf.entrySet()){
//                System.out.println("calculating " + term);
//                System.out.println("TFDoc: " + entry.getValue());
//                System.out.println("IDFWord: " + getIDF(term));
//                System.out.println("TFIDF: " + Double.toString(entry.getValue() * getIDF(term)));
//                System.out.println("doc: " + getDoc(entry.getKey()));
//                System.out.println();
                double tfIdf = entry.getValue() * wordIDF.get(term);
                if(max < tfIdf){
                    max = tfIdf;
                    maxIndex = entry.getKey();
                }
            }

            large[i] = maxIndex;
            termTf.put(maxIndex, Double.MIN_VALUE);
            maxIndex = -1;
            max = Double.MIN_VALUE;
        }

        System.out.println(Arrays.toString(large));
        return large;
    }

    public static Double getIDF(String term){
        if(wordIDF.containsKey(term)){
            return wordIDF.get(term);
        }
        return 0.0;
    }

    public static Double calTfIDF(int docsIndex, String term){
        return collectionTFDoc.get(docsIndex).get(term) * getIDF(term);
    }

    public static Double calTfIDF(double tf, String term){
        return tf * getIDF(term);
    }

    public static double [][] calTfIDF(String [] tokens) {
        // if dont have any docs
        if (collectionTFDoc.isEmpty()) {
            return new double[0][0];
        }

        double[][] TfIDFmatrix = new double[collectionTFDoc.size()][tokens.length];

        int tokenI = 0;
        for (String t : tokens) {
            HashMap<Integer, Double> termTf = new HashMap<>();
            for (int i = 0; i < collectionTFDoc.size(); i++) {
                if (collectionTFDoc.get(i).containsKey(t)) {
                    termTf.put(i, collectionTFDoc.get(i).get(t));
                }
            }

            if (termTf.isEmpty()) {
                tokenI++;
                continue;
            }

            for (Map.Entry<Integer, Double> entry : termTf.entrySet()) {
                TfIDFmatrix[entry.getKey()][tokenI] = entry.getValue() * wordIDF.get(t);
            }

            tokenI++;
        }

        return TfIDFmatrix;
    }
    
    public static String [] getDocs(int[] docsIndex){
        System.out.println(Arrays.toString(docsIndex));
        ArrayList<String> d = new ArrayList<>();
        int index = 0;
        for(int i: docsIndex){
            if(i >= 0){
                System.out.println("check i : " + i);
                if(!d.contains(docs.get(i))){
                    d.add(docs.get(i));
                    index ++;
                }
            }
        }

        return d.toArray(new String[index]);
    }
    
    public static String getDoc(int index){
        if(index >= 0){
            return docs.get(index);
        }
        return "";
    }

    
    public static void addDoc(String sentence, boolean calIDF, boolean saveFile){
        // add doc only when docs do not have this sentence 
        if(!ult.containsCaseInsensitive(sentence, docs)){
            line++;
            try{
                sentence = sentence.replaceAll("[^A-Za-z0-9]+", " ");
                String [] words = sentence.split(" ");
                docs.add(sentence);

                HashMap<String, Integer> wordFeqDoc = new HashMap<>();
                HashMap<String, Double> TFDoc = new HashMap<>();
                for(String w: words){
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
                    pw.write(sentence);
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
