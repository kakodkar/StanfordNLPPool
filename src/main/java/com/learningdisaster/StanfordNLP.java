package com.learningdisaster;

/**
 * Created by michael_kelso
 */

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class StanfordNLP {
    private static Logger log = Logger.getLogger(StanfordNLP.class);
    private double defaultScore = 1.0;
    Properties properties;
    StanfordCoreNLP pipeline;
    Annotation document;
    private HashMap<String, HashSet<String>> keywordsWeightageByPOSTags;
    private HashMap<String, HashSet<String>> keywordsWeightageByNEs;

    private String previousNERString;
    private String previousLemmaString;

    public StanfordNLP() {
        properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        pipeline = new StanfordCoreNLP(properties);
        log.info("Create NLP Object : " + this.hashCode());
    }


    public void process(String paragraph) {
        keywordsWeightageByNEs = new HashMap<>();
        keywordsWeightageByPOSTags = new HashMap<>();
        document = new Annotation(paragraph);
        pipeline.annotate(document);
        previousNERString = previousLemmaString = null;
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                addKeywordFromToken(token);
            }
        }
    }

    public HashSet<String> getHashMapkeywordsWeightageByNE(String type) {
        if (keywordsWeightageByNEs.get(type) != null)
            return keywordsWeightageByNEs.get(type);
        return new HashSet<>();
    }


    private void addKeywordFromToken(CoreLabel token) {
        String posString = token.get(CoreAnnotations.PartOfSpeechAnnotation.class).toLowerCase();
        String lemmaString = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
        String NERString = token.get(CoreAnnotations.NamedEntityTagAnnotation.class).toLowerCase();

        if (NERString.equals(previousNERString) && !NERString.equalsIgnoreCase("o")) {
            addKeywordToNEs(NERString, previousLemmaString + " " + lemmaString);
        }

        if (!NERString.equalsIgnoreCase("o"))
            addKeywordToNEs(NERString, lemmaString);
        addKeywordToPOSTags(posString, lemmaString);
    }

    private void addKeywordToDict(HashMap<String, HashSet<String>> keywordsWeightageByType, String type, String value) {
        if (keywordsWeightageByType.containsKey(type)) {
            HashSet<String> keywords = keywordsWeightageByType.get(type);
            if (!keywords.contains(value))
                keywords.add(value);
            keywordsWeightageByType.put(type, keywords);
        } else {
            HashSet<String> keywords = new HashSet<>();
            keywords.add(value);
            keywordsWeightageByType.put(type, keywords);
        }
    }

    private void addKeywordToPOSTags(String posString, String lemmaString) {
        addKeywordToDict(keywordsWeightageByPOSTags, posString, lemmaString);
    }

    private void addKeywordToNEs(String nerString, String lemmaString) {
        addKeywordToDict(keywordsWeightageByNEs, nerString, lemmaString);
    }

    public static void main(String[] args) throws Exception {
        StanfordNLP stanfordNLP = new StanfordNLP();
    }

}
