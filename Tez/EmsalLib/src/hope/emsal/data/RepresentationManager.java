/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.Token;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.tokenizer.*;

/**
 * Singleton RepresentationCreator Class
 * 
 */

/**
 * Singleton RepresentationManager Class
 */
public class RepresentationManager {
    private static RepresentationManager instance;
    private final ZemberekLexer lexer;
    private final TurkishMorphParser parser;
    private final HashMap<String, Integer> idfMap;
    
    /**
     * Returns a list of sentences in the input.
     * @param input
     * @return 
     */
    public ArrayList<String> detectSentences(String input) {
       SentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector();
       ArrayList<String> sentences = (ArrayList<String>) detector.getSentences(input);
       return sentences;
    }
    
    private RepresentationManager () throws IOException {
        parser = TurkishMorphParser.createWithDefaults();
        lexer = new ZemberekLexer();
        idfMap = new HashMap<>();
    }
    
    /** 
     * Singleton implementation
     * @return
     * @throws IOException 
     */
    public static RepresentationManager instance() throws IOException {
        if (instance == null)
            instance = new RepresentationManager();
        return instance;
                    
    }
    /**
     * returns a DocumentRepresentation created from the contentText
     * @param contentText
     * @return 
     */
    public DocumentRepresentation createRepresentation(String contentText) {
        ArrayList <String> sentences = detectSentences(contentText);
        DocumentRepresentation rVal = new DocumentRepresentation();
        sentences.stream().map((s) -> lexer.getTokenIterator(s)).forEach((Iterator<Token> tokenIterator) -> {
            while (tokenIterator.hasNext()) {
                Token token = tokenIterator.next();
                //type 7 is a known word. 
                if (token.getType() == 7) {
                    String t = token.getText();
                    t = t.toLowerCase();
                    rVal.totalWords++;
                    rVal.wordCounts.put(t, rVal.wordCounts.getOrDefault(t, 0) + 1);
                    List<MorphParse> parses = parser.parse(t); 
                    if (parses.size()>0) {
                        t = parses.get (0).getLemma();
                        rVal.lemmaCounts.put(t, rVal.lemmaCounts.getOrDefault(t, 0) + 1);
                        idfMap.put(t, idfMap.getOrDefault(t, 0) +1 );
                    }
                    
                }
                
            }
        });
        return rVal;
    }
    
    /**
     * This method decrements the count of lemmas of the deleted document from the global idfMap. It removes the lemma when it is equal to 0
     * @param contentText 
     */
    private void deleteFromIdfMap (String contentText) {
        ArrayList <String> sentences = detectSentences(contentText);
        sentences.stream().map((s) -> lexer.getTokenIterator(s)).forEach((Iterator<Token> tokenIterator) -> {
            while (tokenIterator.hasNext()) {
                Token token = tokenIterator.next();
                //type 7 is a known word. 
                if (token.getType() == 7) {
                    String t = token.getText();
                    t = t.toLowerCase();
                    List<MorphParse> parses = parser.parse(t);
                    if (parses.size()>0) {
                        t = parses.get (0).getLemma();
                        idfMap.put(t, idfMap.getOrDefault(t, 0) - 1 );
                        if (idfMap.get (t) <= 0) 
                            idfMap.remove(t);
                    }
                }
            }
        });
    }
    
    /**
     * Returns a DocumentRepresentation from the given file
     * @param fileName
     * @return
     * @throws IOException 
     */
    public DocumentRepresentation createRepresentationFromFile (String fileName) throws IOException {
        File f = new File (fileName);
        String text = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);
        return createRepresentation(text);
    }
    
    public HashMap<String, Integer> getIdfMap () {
        return idfMap;
    }
    
    public int getIDF(String key) {
        return idfMap.getOrDefault(key, 0);
    }
    
    public void setIDF(String key, int val) {
        idfMap.put(key, val);
    }
    
}
