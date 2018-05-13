package chatbot;

import java.lang.reflect.Array;
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
    private static HashMap<String, Double> tfDoc = new HashMap<>();
    // 0 --> when
    // 1 --> what
    // 2 --> who
    // 3 --> why
    // 4 --> where
    // 5 --> how
    private static String object;
    private static String action;
    private static String target;
    private static String adjective;
    private static String mostReleventDoc;

    private static ArrayList<String>singularVerb = new ArrayList(){{
        add("is");
        add("was");
    }};

    private static ArrayList<String>pluralVerb = new ArrayList(){{
        add("are");
        add("were");
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
        object = null;
        action = null;
        adjective = null;
        target = null;

        System.out.println();
        System.out.println("Question type = " + questionType);
        calQueryTfIDF();
        identifyComponent();
        analysis();
        analysis_v2();

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

    public static void identifyComponent(){
        System.out.println("IDENTIFY COMPONENY ---------");
        int index = 0;
        int actionIndex = 0;
        for(String tag: tags){
            if(!ult.isCommonWord(tokens[index])){
                if(ult.properNounTags.contains(tag)){
                    object = tokens[index];
                }

                if(ult.firstPersonVerbTags.contains(tag)){
                    action = tokens[index];
                    actionIndex = index;
                }

                if(ult.adjectiveTags.contains(tag)){
                    adjective = tokens[index];
                }
            }
            index ++;
        }

        // Search for target
        if(action != null){
            for(int i = actionIndex + 1; i < tags.length; i++){
                if(ult.firstPersonVerbTags.contains(tags[i])){
                    target = tokens[index];
                }
            }
        }
        System.out.println("Question object is " + object);
        System.out.println("Question action is " + action);
        System.out.println("target is " + target);
        System.out.println("adjective is " + adjective);
    }

    // use top 5 cosine similarity to construct answer
    public static void analysis_v2(){
        int nTop = 10;
        double [] queryTfIDF = new double[tokens.length];
        double [][] TfIDFmatrix = tfIdf.calTfIDF(tokens);

        for(int indexToken = 0; indexToken < tokens.length; indexToken++){
            String t = tokens[indexToken];
            if(!ult.isCommonWord(t)){
                // calculate TfIDF for query
                queryTfIDF[indexToken] = TFIDF.calTfIDF(tfDoc.get(t), t);
            }
        }

        // calculate vector length for docs
        double [] docsVectorLength = new double[TFIDF.getDocsSize()];
        for(int i = 0; i < TfIDFmatrix.length; i ++){
            double sum = 0;
            for(int j = 0; j < TfIDFmatrix[0].length; j ++){
                if(TfIDFmatrix[i][j] >= 0){
                    sum += Math.pow(TfIDFmatrix[i][j], 2.0);
                }
            }
            docsVectorLength[i] = Math.sqrt(sum);
        }

        // calculate vector length for query
        double sum = 0;
        for(int i = 0; i < queryTfIDF.length; i ++){
            sum += Math.pow(queryTfIDF[i], 2);
        }
        double queryVectorLength = Math.sqrt(sum);

        int nTopCosineSimi = 10;
        double [] topCosineSimis = new double[nTopCosineSimi];
        int [] topCosineSdocsIndex = new int[nTopCosineSimi];
        Arrays.fill(topCosineSdocsIndex, -1);
        double [] cosineSimilarity = new double[TFIDF.getDocsSize()];

        for(int i = 0; i < TFIDF.getDocsSize(); i ++){
            cosineSimilarity[i] = ult.calCosineSimilarity(TfIDFmatrix[i], queryTfIDF, docsVectorLength[i], queryVectorLength);
        }

        double maxCosineSimi = 0.7;
        double [] copyCosineSimilarity = new double[cosineSimilarity.length];
        System.arraycopy(cosineSimilarity, 0, copyCosineSimilarity, 0, cosineSimilarity.length);
        int maxIndex = -1;
        for(int i = 0; i < nTopCosineSimi; i ++){
            for(int docI = 0; docI < cosineSimilarity.length; docI ++){
                double val = cosineSimilarity[docI];
//                System.out.println("cosine s value: " + Double.toString(val));
//                System.out.println("cosine similarity docs: " +  tfIdf.getDoc(docI));
                if(maxCosineSimi < val){
                    System.out.println("Higher");
                    System.out.println(val);
                    maxCosineSimi = val;
                    maxIndex = docI;
                }
            }

            System.out.println(maxCosineSimi);
            System.out.println(tfIdf.getDoc(maxIndex));
            if(maxIndex >= 0){
                topCosineSimis[i] = copyCosineSimilarity[maxIndex];
                topCosineSdocsIndex[i] = maxIndex;
                cosineSimilarity[maxIndex] = Double.MIN_VALUE;
            }
            maxCosineSimi = 0.7;
            maxIndex = -1;
        }

        System.out.println("Top 5 cosine similarity value: ");
        for(int i = 0; i < nTopCosineSimi; i ++){
            System.out.println("cosine similarity value : " + Double.toString(topCosineSimis[i]));
            System.out.println("cosine similarity docs: " +  tfIdf.getDoc(topCosineSdocsIndex[i]));
        }

        ArrayList<String> ans = new ArrayList<>();
        // What
        if(questionType == 1) {
            if (object == null) {
                if (action == null) {
                    // don't have object and action
                    // return "i do not understand your question"
                    ans.add("i do not understand your question");
                } else {
                    // What is Running?
                    // What is \VERB\?
                    // Search answer based on action
                    System.out.println("Searching defination for action ");
                    ArrayList<String>defs = new ArrayList<>();
                    for(int i = 0; i < topCosineSdocsIndex.length; i ++){
                        int docIndex = topCosineSdocsIndex[i];
                        String doc = tfIdf.getDoc(docIndex);
                        String [] docTag = tagger.tag(doc);
                        String [] tks = ult.tokenize(doc);
                        for(String d: findDef(tks, docTag, action, ult.nounTags)){
                            defs.add(d);
                        }
                    }

                    if(!defs.isEmpty()){
                        ans = defs;
                    }

                    //dont have object have this action
                    // return "do have people do this action"
                    if(ans.size() == 0){
                        ans.add("is any one " + action + "? Please tell me more :)");
                    }
                }
            } else {
                if(action == null){
                    // what is Kew
                    // What is \NOUN\
                    System.out.println("Searching defination for object ");
                    ArrayList<String>defs = new ArrayList<>();
                    for(int docIndex: topCosineSdocsIndex){
                        String doc = tfIdf.getDoc(docIndex);
                        String [] docTag = tagger.tag(doc);
                        String [] tks = ult.tokenize(doc);
                        for(String d: findDef(tks, docTag, object, ult.nounTags)){
                            defs.add(d);
                        }
                    }

                    if(!defs.isEmpty()){
                        ans = defs;
                    }

                    //dont have object have this action
                    // return "do have people do this action"
                    if(ans.size() == 0){
                        ans.add("is any one " + action + "? Please tell me more :)");
                    }

                } else {
                    // What is Kew eating
                    // What is \NOUN\ \VERB\
                    ArrayList<String> target = new ArrayList<>();
                    for(int docIndex: topCosineSdocsIndex){
                        String doc = tfIdf.getDoc(docIndex);
                        String [] docTag = tagger.tag(doc);
                        String [] tks = ult.tokenize(doc);
                        for(String d: findTarget(tks, docTag, action)){
                            target.add(d);
                        }
                    }

                    if(!target.isEmpty()){ ans = target; }
                }
            }
        // Who
        } else if(questionType == 2){
            if(object == null){
                if(action == null){
                    // don't have object and action
                    ans.add("i do not understand your question");
                } else {
                    // who is running
                    // who is \VERB\
                    System.out.println("Search object that doing that action");
                    for(int i = 0; i < topCosineSdocsIndex.length; i ++){
                        int docIndex = topCosineSdocsIndex[i];
                        if (Double.isNaN(topCosineSimis[i])){
                            break;
                        }
                        String doc = tfIdf.getDoc(docIndex);
                        ArrayList<String>object = findObj(ult.tokenize(doc), tagger.tag(doc), action);
                        if(object.size() != 0){
                            for(String o: object){
                                if(!ans.contains(o)){ ans.add(o); }
                            }
                        }
                    }



                }
            } else {
                if(action == null){
                    // who is Kew?
                    // who is \NOUN\
                    for(int docIndex: topCosineSdocsIndex){
                        String doc = tfIdf.getDoc(docIndex);
                        String [] docTag = tagger.tag(doc);
                        String [] tks = ult.tokenize(doc);
                        ArrayList<String>defs = findDef(tks, docTag, object, ult.nounTags);
                        if(!defs.isEmpty()){
                            ans = defs;
                        }
                    }
                } else {
                    // who is Kew running?
                    ans.add("I am not sure what you are trying to ask :(");
                }
            }
        // Why
        } else if(questionType == 3){
            System.out.println("Looking for reason");
            if(object == null){
                if(action == null){
                    ans.add("I am sorry, i can't understand your question");
                } else {
                    // Why running
                    // Why \VERB\
                    ans.add("I am sorry, who " + action);
                }
            } else {
                if(action == null){
                    // Why Kew
                    // Why \NOUN\
                    ans.add("What happen to " + object + "?");
                } else {
                    // Why is Kew running
                    // Why \NOUN\ \VERB\
                    ArrayList<String> reasons = new ArrayList<>();
                    for(int docIndex: topCosineSdocsIndex){
                        String doc = tfIdf.getDoc(docIndex);
                        String [] docTag = tagger.tag(doc);
                        String [] tks = ult.tokenize(doc);
                        String r = findReason(tks, docTag, action);
                        if(!r.isEmpty()) {reasons.add(r);}
                    }

                    if(reasons.size() > 0){ ans = reasons; }
                }
            }
        }

        System.out.println("ans for analysis v2:");
        for(String a: ans){
            System.out.println(a);
        }
    }

    public static void calQueryTfIDF(){
        HashMap<String, Integer> wordFeqDoc = new HashMap<>();
        for(String t: tokens){
            if(wordFeqDoc.containsKey(t)){
                wordFeqDoc.put(t, wordFeqDoc.get(t) + 1);
            } else {
                wordFeqDoc.put(t, 1);
            }
        }

        for(String word: wordFeqDoc.keySet()){
            tfDoc.put(word, (wordFeqDoc.get(word) / (double)tokens.length));
        }
    }

    public static void analysis(){
        int highestFeq = 1;
        String mostRelevent = "";
        for(String t: tokens){
            if(!ult.isCommonWord(t)){
                String [] doc = tfIdf.getDocs(tfIdf.getNHighestTFIDFIndex(t, 10));
                tokenToDocs.put(t, doc);

                // calculate doc fequency
                for(String d: doc){
                    if(docsFeq.containsKey(d)){
                        docsFeq.put(d, docsFeq.get(d) + 1);
                        if(highestFeq < docsFeq.get(d)){
                            highestFeq = docsFeq.get(d);
                        }
                    } else {
                        docsFeq.put(d, 1);
                    }
                }
            }
        }

        ArrayList<String> ans = new ArrayList<>();

        // What and who
        if(questionType == 1 || questionType == 2){
            if(object == null){
                if(action == null){
                    // don't have object and action
                    // return "i do not understand your question"
                    ans.add("i do not understand your question");
                } else {
                    // Search answer based on action
                    System.out.println("Searching object related with the action");
                    if(tokenToDocs.get(action).length > 0){
                        // get the object as answer
                        for(String ds: tokenToDocs.get(action)){
                            String [] dsTag = tagger.tag(ds);
                            String [] tks = ult.tokenize(ds);
                            ArrayList<Integer> objIndex = ult.findObjectIndex(dsTag, tks, action);
                            for(int i: objIndex){
                                if(!ans.contains(tks[i])){
                                    ans.add(tks[i]);
                                }
                            }
                        }

                    } else {
                        //dont have object have this action
                        // return "do have people do this action"
                        ans.add("i don't see any people is doing this");
                    }
                }
            } else {
                // if not action, look for action as answer
                if(action == null){
                    // have object, looking for action
                    // check if object present in document
                    if(tokenToDocs.get(object).length > 0){
                        // Look for what action the object done
                        for(String ds: tokenToDocs.get(object)){
                            System.out.println("ds : " + ds);
                            String [] dsTag = tagger.tag(ds);
                            String [] tks = ult.tokenize(ds);
                            ArrayList<Integer> verbIndex = ult.findActionIndex(dsTag, tks, object);
                            for(int i: verbIndex){
                                if(!ans.contains(tks[i])){
                                    System.out.println("token added to ans: " + tks[i]);
                                    ans.add(tks[i]);
                                }
                            }
                        }
                    } else {
                        // object is not present in document
                        // return "i dont know who / what u are referring to"
                        ans.add("i don't know who / what you are referring to");
                    }
                } else {
                    // have object and action, look for target as answer
                    System.out.println("Looking for target");
                    for(String ds: tokenToDocs.get(object)){
                        System.out.println("ds : " + ds);
                        String [] dsTag = tagger.tag(ds);
                        String [] tks = ult.tokenize(ds);
                        ArrayList<Integer>verbIndex = ult.findActionIndex(dsTag, tks, object);
                        for(int i: verbIndex){
                            System.out.println("token to match action: " + tks[i]);
                            if(tks[i].equals(action)){
                                ArrayList<Integer>targetIndex = ult.findTargetIndex(dsTag, tks, action);
                                for(int j: targetIndex){
                                    if(!ans.contains(tks[j])){
                                        System.out.println("Token added to ans: "  + tks[j]);
                                        ans.add(tks[j]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        // Why, Asking for reason
        } else if(questionType == 3){
            if(object == null){
                // return can't understand your question
                ans.add("I am sorry, i can't understand your question");
            } else {
                // if not action, look for action as answer
                if(action == null){
                    // return can't understand your question
                    ans.add("I am sorry, i can't understand your question");
                } else {
                    // have object and action
                    // looking for reason
                    for(String ds: tokenToDocs.get(object)){
                        System.out.println("ds : " + ds);
                        String [] dsTag = tagger.tag(ds);
                        String [] tks = ult.tokenize(ds);
                        ArrayList<Integer>verbIndex = ult.findActionIndex(dsTag, tks, object);
                        for(int i: verbIndex){
                            System.out.println("token to match action: " + tks[i]);
                            if(tks[i].equals(action)){
                                // look for 'because' for reason
                                for(int j = 0; j < tks.length; j++){
                                    if(tks[j].equalsIgnoreCase("because")){
                                        String [] targetArr = ult.cropArray(j, tks.length, tks);
                                        ans.add(ult.arrayToSring(targetArr));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        // When, asking for time
        } else if(questionType == 1){
            if(object == null || action == null){
                ans.add("I am sorry, i can't understand your question");
            } else {
                for(String docs: tokenToDocs.get(object)){

                }
            }
        }



        mostReleventDoc = formatAnswer(ans);
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
        } else if (ans.size() == 1){
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

    public static String answer(){
        return mostReleventDoc;
    }


    public static boolean isSingular(String[]tokens){
        for(String t: tokens){
            if(singularVerb.contains(t)){ return true; }
        }
        return false;
    }

    public static boolean isPlural(String [] tokens){
        for(String t: tokens){
            if(pluralVerb.contains(t)) { return true; }
        }
        return false;
    }

    public static ArrayList<String> findDef(String []tokens, String[]tags, String term, ArrayList<String >targetTags){
        ArrayList<String> defs = new ArrayList<>();
        for(int i = 0;i < tokens.length - 1; i++){
            String t = tokens[i];
            if(term.equalsIgnoreCase(t)){
                t = tokens[i + 1];
                if(singularVerb.contains(t) || pluralVerb.contains(t)){
                    String def = "";
                    for(int j = i + 2;j < tokens.length; j ++){
                        // after is/are/was/were should have have verb for definition
                        if(ult.verbTags.contains(tags[j])){ break; }

                        if(!ult.verbTags.contains(tags[j])){
                            def = ult.arrayToSring(ult.subArray(tokens, j, tokens.length));
                            break;
                        }
                    }
                    if(!def.isEmpty()){ defs.add(def); }
                }
            }
        }

        return defs;
    }

    public static ArrayList<String> findObj(String [] tokens, String [] tags, String action){
        ArrayList<String> obj = new ArrayList<>();
        for(int i = 0; i < tokens.length; i ++){
            String t = tokens[i];
            if(t.equalsIgnoreCase(action)){
                for(int j = i; j >= 0; j --){
                    if(ult.nounTags.contains(tags[j])){
                        obj.add(tokens[j]);
                        break;
                    }
                }
            }
        }

        return obj;
    }

    public static ArrayList<String> findTarget(String [] tokens, String [] tags, String action){
        ArrayList<String> targets = new ArrayList<>();
        for(int i = 0; i < tokens.length; i ++){
            String t = tokens[i];
            if(t.equalsIgnoreCase(action)){
                for(int j = i; j < tokens.length; j ++){
                    if(ult.nounTags.contains(tags[j])){
                        targets.add(tokens[j]);
                        break;
                    }
                }
            }
        }

        return targets;
    }

    public static String findReason(String [] tokens, String [] tags, String action){
        String reason = "";
        for(int i = 0; i < tokens.length; i ++){
            String t = tokens[i];
            if(t.equalsIgnoreCase("because")){
                reason = ult.arrayToSring(ult.subArray(tokens, i, tokens.length));
                break;
            }
        }

        return reason;
    }
}
