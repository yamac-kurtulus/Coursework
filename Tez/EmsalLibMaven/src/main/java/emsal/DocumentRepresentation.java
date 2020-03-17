/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emsal;

import java.io.FileWriter; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import zemberek.tokenizer.ZemberekLexer;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.parser.MorphParse;


/**
 *
 * @author Yamak
 */
public class DocumentRepresentation {
    private HashMap <String, Integer> wordCounts;
    private HashMap <String, Integer> lemmaCounts;
    private HashMap <String, Double> vectorRepresentation;
    private double length;
    private int totalWords = 0;
    
    public DocumentRepresentation () {
            wordCounts = new HashMap();
            lemmaCounts = new HashMap();
            vectorRepresentation = new HashMap();
    }
    
    @Override
    public String toString () {
        String s = "";
        s +=    "Word Counts:\n";
        Set set = getWordCounts().entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
             Map.Entry me2 = (Map.Entry)iterator.next();
             s += me2.getKey() + ": ";
             s += me2.getValue() + "\n";
        }
        s += "Lemma Counts:\n";
        set = getLemmaCounts().entrySet();
        iterator = set.iterator();
        while(iterator.hasNext()) {
             Map.Entry me2 = (Map.Entry)iterator.next();
             s += me2.getKey() + ": ";
             s += me2.getValue() + "\n";
        }
        return s;
    }

    public HashMap <String, Double> prepareRepresentation (HashMap<String, Double> terms) {
        Iterator iterator = terms.entrySet().iterator();
        while(iterator.hasNext()) {
             Map.Entry<String, Double> me = (Map.Entry)iterator.next();
             double weight = getLemmaCounts().getOrDefault(me.getKey(), 0) / (double) getTotalWords() * me.getValue();
             getVectorRepresentation().put(me.getKey(), weight);
        }
        
        return getVectorRepresentation();
    }
    
    public String toVectorString() {
        Iterator iterator = getVectorRepresentation().entrySet().iterator();
        String rval="";
        while(iterator.hasNext()) {
             Map.Entry<String, Double> me = (Map.Entry)iterator.next();
             rval += me.getValue() + ",";
             }
        rval = rval.substring(0, rval.lastIndexOf(','));
        return rval;
    }

    //trims the lemma list to the most relevant n items
    //does nothing if 0 is given
    void trimVector(int n) {
        
    }

       // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    
    /**
     * @return the wordCounts
     */
    public HashMap <String, Integer> getWordCounts() {
        return wordCounts;
    }

    /**
     * @param wordCounts the wordCounts to set
     */
    public void setWordCounts(HashMap <String, Integer> wordCounts) {
        this.wordCounts = wordCounts;
    }

    /**
     * @return the lemmaCounts
     */
    public HashMap <String, Integer> getLemmaCounts() {
        return lemmaCounts;
    }

    /**
     * @param lemmaCounts the lemmaCounts to set
     */
    public void setLemmaCounts(HashMap <String, Integer> lemmaCounts) {
        this.lemmaCounts = lemmaCounts;
    }

    /**
     * @return the vectorRepresentation
     */
    public HashMap <String, Double> getVectorRepresentation() {
        return vectorRepresentation;
    }

    /**
     * @param vectorRepresentation the vectorRepresentation to set
     */
    public void setVectorRepresentation(HashMap <String, Double> vectorRepresentation) {
        this.vectorRepresentation = vectorRepresentation;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * @return the totalWords
     */
    public int getTotalWords() {
        return totalWords;
    }

    /**
     * @param totalWords the totalWords to set
     */
    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }
    
    // </editor-fold>
    
}
