/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author kewjielong
 */
public class questionLibrary {
    public static ArrayList <String> question = new ArrayList();
    public static ArrayList <String> answer = new ArrayList();
    private static final String SOURCE_FILE = "hardcore_answer.csv";
    
    public questionLibrary(){
        System.out.println("question library is initialed");
        File file = new File(SOURCE_FILE);
        try{
            Scanner inputStream = new Scanner(file);
            inputStream.nextLine(); //skip the header
            while(inputStream.hasNext()){
                String data = inputStream.nextLine();
                String[]split = data.split(",");
                question.add(split[0]);
                answer.add(split[1]);
//                System.out.println(data);
            }
            
        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        
//        System.out.println(question);
//        System.out.println(answer);
    }
    
    public String answer(String question_from_user){
        for(int i = 0; i < question.size(); i++){
            if(question.get(i).equalsIgnoreCase(question_from_user)){
                return answer.get(i);
            }
        }
        
        return "I can't answer your question";        
    }
    
}
