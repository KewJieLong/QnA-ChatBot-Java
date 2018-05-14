/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbot;

import javax.script.ScriptException;
import java.util.Scanner;

/**
 *
 * @author kewjielong
 */
public class Chatbot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ScriptException {
        Scanner kb = new Scanner(System.in);
//        NN_PosTagger posTagger = new NN_PosTagger();
//        TFIDF tfidf = new TFIDF();
//        Utilities ult = new Utilities();
        Core core = new Core();
        System.out.println("Hello, I am a chatbot, what can i help you?");             
        
        
        while(true){
            String input_kb = kb.nextLine();
            System.out.println();
            System.out.println("bot: " + core.reply(input_kb));
            
            
//            for(int i: ult.nounIndex(tags)){
//                System.out.println(token[i]);                
//            }                        
        }           
        
    }
    
}
