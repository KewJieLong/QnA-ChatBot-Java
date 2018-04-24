package chatbot;

import java.util.ArrayList;

public class Question {
    private static String sentence;
    private static String [] tags;
    private static String [] tokens;

    public Question(String s, String[] t, String [] tks){
        sentence = s;
        tags = t;
        tokens = tks;
    }


}
