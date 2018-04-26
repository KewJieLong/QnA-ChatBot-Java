package chatbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Question {
    private static String [] tags;
    private static String [] tokens;
    private static HashMap<String, String[]> tokenToDocs = null;
    private static HashMap<String, Integer> docsFeq = null;
    private static TFIDF tfIdf;
    private static Utilities ult;
    private static NN_PosTagger tagger;
    private static ArrayList<String> obj = null;
    private static ArrayList<String> verb = null;
    private static int questionType = 0;
    // 0 --> when
    // 1 --> what
    // 2 --> who
    // 3 --> why
    // 4 --> where
    // 5 --> how
    private static String mostReleventDoc;

    private static ArrayList<String>singularVerb = new ArrayList(){{
        add("is");
        add("was");
    }};

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
    }};


    public Question(String[] t, String [] tks, TFIDF tfidf, Utilities u, NN_PosTagger posTagger,int qt){
        tokenToDocs = new HashMap<>();
        docsFeq = new HashMap<>();
        obj = new ArrayList<>();
        verb = new ArrayList<>();
        tags = t;
        tokens = tks;
        tfIdf = tfidf;
        ult = u;
        questionType = qt;
        tagger = posTagger;

        analysis();

        System.out.println("Token To docs:");
        for(Map.Entry<String, String[]> entry: tokenToDocs.entrySet()){
            System.out.println(entry.getKey() + " : " + Arrays.toString(entry.getValue()));
        }

        System.out.println();
        System.out.println("docs feq");
        for(Map.Entry<String, Integer> entry: docsFeq.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }

    public static void analysis(){
        int highestFeq = 0;
        String mostRelevent = "";
        for(String t: tokens){
            if(!commomWord.contains(t)){
                String [] doc = tfIdf.getDocs(tfIdf.get3HighestTFIDFIndex(t));
                tokenToDocs.put(t, doc);

                // calculate doc fequency
                for(String d: doc){
                    if(docsFeq.containsKey(d)){
                        docsFeq.put(d, docsFeq.get(d) + 1);
                    } else {
                        docsFeq.put(d, 1);
                    }
                    // get the highest fequency docs
                    if(highestFeq < docsFeq.get(d)){
                        highestFeq = docsFeq.get(d);
                        mostRelevent = d;
                    }
                }
            }
        }

        // if result multiple match...
        // extract the higest match to further processing
        System.out.println("HIGHEST FEQ  : " + highestFeq);
        ArrayList<String> MostReleventDocs = new ArrayList<>();
        for (Map.Entry<String, Integer> map : docsFeq.entrySet()) {
            System.out.println(map.getKey() + " : " + map.getValue());
            System.out.println(map.getValue() == highestFeq);
            if (map.getValue() == highestFeq) {
                MostReleventDocs.add(map.getKey());
            }
        }

        ArrayList<String>ans = new ArrayList<>();
        int mostDocsNum = 0;
        String mostDocsToken = "";
        for(Map.Entry<String, String[]> entry: tokenToDocs.entrySet()){
            if(mostDocsNum < entry.getValue().length){
                mostDocsNum = entry.getValue().length;
                mostDocsToken = entry.getKey();
            }
        }

        System.out.println("Most num Docs:" + mostDocsNum);
        System.out.println("Most docs token: " + mostDocsToken);

        for(String ds: MostReleventDocs){
            System.out.println("ds: " + ds);
            String[]docTag = tagger.tag(ds);
            ArrayList<Integer>tagIndex;
            if(ult.getTokenTags(tags, tokens, mostDocsToken).equals("VERB")){
                System.out.println("Looking for noun");
                tagIndex = ult.findTagIndex(docTag, ult.properNountTags);
            }else{
                System.out.println("Looking for verb");
                tagIndex = ult.findTagIndex(docTag, ult.firstPersonVerbTags);
            }
            System.out.println("Doc Tag: " + Arrays.toString(docTag));
            System.out.println("Tag Index: " + tagIndex.toString());
            String [] tks = ult.tokenize(ds);
            for(int i = 0; i < tks.length; i++){
                if(tks[i].equals(mostDocsToken)){
                    for(int j = 0 ; j < tagIndex.size() ; j ++){
                        if(!ans.contains(tks[tagIndex.get(j)])){
                            ans.add(tks[tagIndex.get(j)]);
                        }
                    }
                    break;
                }
            }
        }


        System.out.println("ans: " + ans.toString());
        mostRelevent = formatAnswer(ans);

        mostReleventDoc = mostRelevent;
    }

    public static String formatAnswer(ArrayList<String> ans){
        String fAns = "";
        if(ans.size() > 1){
            for(int i = 0; i < ans.size(); i ++){
                if(i == ans.size() - 2) {
                    fAns += (ans.get(i) + " and ");
                } else if(i == ans.size() - 1) {
                    fAns += (ans.get(i));
                } else {
                    fAns += (ans.get(i) + ',');
                }
            }
        } else {
            fAns = ans.get(0);
        }

        return fAns;
    }


    public static void answerFormating(String[] tags, String [] tokens, HashMap<String, String[]>tokenToDocs){
        ArrayList <Integer> targetIndex = null;
        ArrayList <String> targetNoun = new ArrayList<>();
        if(questionType == 0){

        } else if(questionType == 1){

        } else if(questionType == 2){
            // Look for Noun as answer
            for(Map.Entry<String, String[]>entry: tokenToDocs.entrySet()){

            }


        } else if(questionType == 3){

        } else if(questionType == 4){

        }


    }



    public static boolean isSingular(String[]tokens){
        for(String t: tokens){
            if(singularVerb.contains(t)){ return true; }
        }

        return false;
    }

    public static String answer(){
        return mostReleventDoc;
    }


}
