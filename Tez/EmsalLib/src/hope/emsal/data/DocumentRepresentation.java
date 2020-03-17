/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.data;

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
    HashMap <String, Integer> wordCounts;
    HashMap <String, Integer> lemmaCounts;
    HashMap <String, Double> vectorRepresentation;
    double length;
    int totalWords = 0;
    
    DocumentRepresentation () {
            wordCounts = new HashMap();
            lemmaCounts = new HashMap();
            vectorRepresentation = new HashMap();
            
    }
    
    @Override
    public String toString () {
        String s = "";
        s +=    "Word Counts:\n";
        Set set = wordCounts.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
             Map.Entry me2 = (Map.Entry)iterator.next();
             s += me2.getKey() + ": ";
             s += me2.getValue() + "\n";
        }
        s += "Lemma Counts:\n";
        set = lemmaCounts.entrySet();
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
             double weight = lemmaCounts.getOrDefault(me.getKey(), 0) / (double) totalWords * me.getValue();
             vectorRepresentation.put(me.getKey(), weight);
        }
        
        return vectorRepresentation;
    }
    
    public String toVectorString() {
        Iterator iterator = vectorRepresentation.entrySet().iterator();
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
    
    
    
}
