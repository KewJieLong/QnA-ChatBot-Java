/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author kewjielong
 */
public class NN_PosTagger {
    private static final String parentWeightFolder = "NN_tagger_weights/";
    private static final String wordToIdPath = parentWeightFolder + "word_to_id.csv";
    private static final String posToIdPath = parentWeightFolder + "pos_to_id.csv";
    private static final String idToWordPath = parentWeightFolder + "id_to_word.csv";
    private static final String idToPosPath = parentWeightFolder + "id_to_pos.csv";
    private static final String embeddingPath = parentWeightFolder + "embedding.csv";
    private static final String w1Path = parentWeightFolder + "w1.csv";
    private static final String w2Path = parentWeightFolder + "w2.csv";
    private static final int nPastWord = 3;
    private static final int UNKNOWNWORDID = 0;
    private static final String UNKNOWNWORD = "<UNKNOW_WORD>";
    
    public static HashMap<String, String> wordToId;
    public static HashMap<String, String> posToId; 
    public static HashMap<String, String> idToWord;
    public static HashMap<String, String> idToPos;
    public static double [][]embedding = new double[50000][50];
    public static double [][]w1 = new double[200][100];
    public static double [][]w2 = new double[100][44];
    
    
    public NN_PosTagger(){
        System.out.println("Setting up NN pos tagger");
        wordToId = loadCsv(wordToIdPath);
        posToId = loadCsv(posToIdPath);
        idToWord = loadCsv(idToWordPath);
        idToPos = loadCsv(idToPosPath);
        embedding = loadMatrix(embeddingPath, 50000, 50);
        w1 = loadMatrix(w1Path, 200, 100);
        w2 = loadMatrix(w2Path, 100, 44);                
    }
    
    public HashMap loadCsv(String path){
        HashMap <String, String> content = new HashMap<>();
        System.out.println("Loading " + path);
        File file = new File(path);
        try{
            Scanner inputStream = new Scanner(file);
            inputStream.nextLine();
            while(inputStream.hasNext()){
                String data = inputStream.nextLine();               
                String [] split = data.split(",");                                          
                if (split.length > 1){
                    content.put(split[0], split[1]);
                } else {
                    content.put(split[0], ",");
                } 
            }
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        
        return content;
    }
    
    public double[][] loadMatrix(String path, int nrow, int ncolumn){
        System.out.println("Loading " + path);
        File file = new File(path);
        double [][] matrix = new double [nrow][ncolumn];
        try{
            int row = 0;
            int column = 0;
            Scanner inputStream = new Scanner(file);            
            while(inputStream.hasNext()){
                String data = inputStream.nextLine();                
                String [] split = data.split(",");                
                if(split.length> 0){
                    for(String s: split){                        
                        matrix[row][column] = Double.parseDouble(s);
                        column ++;
                    }                                        
                }
                row ++;
                column = 0;
            }
            
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        
        return matrix;
    }
    
    public ArrayList generateFeature(String sentence){        
        String [] words = sentence.split(" ");
        ArrayList sentenceFeature = new ArrayList();
        
        for(int i = 0; i < words.length; i++){
            ArrayList pastWordId = new ArrayList<>();
            for(int j = 0; j < nPastWord + 1; j ++){
                if(i - j < 0){
                    pastWordId.add(UNKNOWNWORDID);
                }else if(wordToId.containsKey(words[i - j])){
                    pastWordId.add(Integer.parseInt(wordToId.get(words[i - j])));
                }else{
                    pastWordId.add(UNKNOWNWORDID);
                }
            }
            sentenceFeature.add(pastWordId);
        }    
        
        return sentenceFeature;
    }      
    
    public static double[][] multiplicar(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
    
    public double[] flattenMatrix(ArrayList <double[]> matrix){
        double [] flattenM = new double[50 * (nPastWord + 1)];
        int index = 0;
        for(int i = 0; i < matrix.size(); i ++){
            double [] m = matrix.get(i);
            for(double d: m){
                flattenM[index] = d;
                index ++;
            }
        }
        
        return flattenM;
    }
    
    public int argmax(double [] output){
        double max = 0;
        int index = 0;        
        for(int i = 0; i < output.length; i ++){
            if(max < output[i]){
                max = output[i];
                index = i;
            }
        }
        
        return index;
    }
    
    public double [][] relu(double [][] matrix){
        for(int i = 0; i < matrix.length; i ++){
            for(int j = 0; j < matrix[i].length; j ++){
                if(matrix[i][j] < 0){
                    matrix[i][j] = 0;
                }
            }
        }
        
        return matrix;
    }
    
    
    public String [] tag(String sentence){        
        ArrayList features = generateFeature(sentence);  
        String [] tags = new String[features.size()];
        // load each word feature
        for(int i = 0; i < features.size(); i ++){
            ArrayList<double[]> wordMatrix = new ArrayList();
            ArrayList<Integer>feature = (ArrayList<Integer>)features.get(i);            
            // load embedding for each past word ID 
            for(int j = 0; j < feature.size(); j ++){                       
                wordMatrix.add(embedding[feature.get(j)]);
            }            
            
            double [][] flattenWordMatrix = new double[1][200];
            flattenWordMatrix[0] = flattenMatrix(wordMatrix);
            
            double [][] h = relu(multiplicar(flattenWordMatrix, w1));            
            double [][] logits = multiplicar(h, w2);
            int predict = argmax(logits[0]);            
            tags[i] = idToPos.get(Integer.toString(predict));                                    
        }
        
        return tags;
    }
}
