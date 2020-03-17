 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emsal;
 
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.Token;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.ObjectId;
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
    
    public static class IDFWrapper {
        private HashMap<String, Integer> idfMap;
        private String _id;

        public HashMap<String, Integer> getIdfMap() {
            return idfMap;
        }

        public void setIdfMap(HashMap<String, Integer> idfMap) {
            this.idfMap = idfMap;
        }

        public int getTotalDocs() {
            return totalDocs;
        }

        public void setTotalDocs(int totalDocs) {
            this.totalDocs = totalDocs;
        }
        private int totalDocs;

        public IDFWrapper() {
        }

        private IDFWrapper(HashMap<String, Integer> idfMap) {
            this.idfMap = idfMap;
        }
        
        @ObjectId
        @JsonProperty("_id")
        public String getId() {
          return _id;
        }

        @ObjectId
        @JsonProperty("_id")
        public void setId(String id) {
          this._id = id;
        }   
    }
    
    private static RepresentationManager instance;
    private final ZemberekLexer lexer;
    private final TurkishMorphParser parser;
    private HashMap<String, Integer> idfMap;
    private DB db;
    private IDFWrapper idfWrapper;
    
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
                    rVal.setTotalWords(rVal.getTotalWords() + 1);
                    rVal.getWordCounts().put(t, rVal.getWordCounts().getOrDefault(t, 0) + 1);
                    List<MorphParse> parses = parser.parse(t); 
                    if (parses.size()>0) {
                        t = parses.get (0).getLemma();
                        rVal.getLemmaCounts().put(t, rVal.getLemmaCounts().getOrDefault(t, 0) + 1);
                    }
                }
            }
        });
        
        return rVal;
    }
    
    /**
     * This method increments the count of lemmas of the deleted document from the global idfMap. It removes the lemma when it is equal to 0
     * @param vals 
     */
    public void incrementIDFs (HashMap<String, Integer> vals) {
        fetchIDFMap ();
        vals.forEach((k, v) -> {
            idfMap.put (k, idfMap.getOrDefault(k, 0) + 1);
        });
        updateIDFMap ();
        
    }
    
    
    /**
     * This method increments the count of lemmas of the deleted document from the global idfMap. It removes the lemma when it is equal to 0
     * @param vals 
     */
    public void decrementIDFs (HashMap<String, Integer> vals) {
        fetchIDFMap ();
         vals.forEach((k, v) -> {
            idfMap.put (k, idfMap.getOrDefault(k, 1) - 1);
            if (idfMap.get (k) <= 0)
                idfMap.remove(k);
        });
         
        updateIDFMap ();
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
        fetchIDFMap ();
        return idfMap;
    }
    
    public int getIDF(String key) {
        fetchIDFMap ();
        return idfMap.getOrDefault(key, 0);
    }
    
    public void setIDF(String key, int val) {
        fetchIDFMap ();
        idfMap.put(key, val);
        updateIDFMap();
    }
    
    public void setDB (DB db) {
        this.db = db;
    }
    
    public HashMap <String, Double> getVector (DocumentRepresentation d) {
        fetchIDFMap();
        Iterator iterator = idfMap.entrySet().iterator();
        HashMap <String, Double> vector = new HashMap<>();
        while(iterator.hasNext()) {
             Map.Entry<String, Integer> me = (Map.Entry)iterator.next();
             double m = (double) (d.getLemmaCounts().getOrDefault(me.getKey(), 0));
             double n = (double) d.getLemmaCounts().size();
             double val = (double) me.getValue();
             double weight = (m / n) * Math.log(1.0 + (double) (idfWrapper.totalDocs) / val);
             vector.put(me.getKey(), weight);
        }
        return vector;
    }

//    private void fetchIDFMap() {
//        DBCollection idfCollection = db.getCollection("IDFMap");
//        JacksonDBCollection<HashMap, Long> coll = JacksonDBCollection.wrap(idfCollection, HashMap.class, Long.class);
//        //org.mongojack.DBCursor<HashMap> find = coll.findOne();
//        idfMap = coll.findOne();
//        //idfMap = find.curr();
//        if (idfMap == null)
//            idfMap = new HashMap <>();
//        idfMap.remove("_id");
//        
//    }
//
//    private void updateIDFMap() {
//        DBCollection idfCollection = db.getCollection("IDFMap");
//        JacksonDBCollection<HashMap, Long> coll = JacksonDBCollection.wrap(idfCollection, HashMap.class, Long.class);
//        org.mongojack.DBCursor<HashMap> find = coll.find();
//        if (!find.hasNext())
//            coll.insert(idfMap);
//        else 
//            coll.update(DBQuery.empty(), idfMap);
//    }

    private void fetchIDFMap() {
        DBCollection idfCollection = db.getCollection("IDFMap");
        JacksonDBCollection<IDFWrapper, Long> coll = JacksonDBCollection.wrap(idfCollection, IDFWrapper.class, Long.class);
        org.mongojack.DBCursor<IDFWrapper> find = coll.find();
        if (!find.hasNext()){
            idfMap = new HashMap <>();
            idfWrapper = new IDFWrapper(idfMap);
        }
        else {
            idfWrapper = find.next();
            idfMap = idfWrapper.idfMap;
        }
        
        
    }

    private void updateIDFMap() {
        DBCollection idfCollection = db.getCollection("IDFMap");
        JacksonDBCollection<IDFWrapper, Long> coll = JacksonDBCollection.wrap(idfCollection, IDFWrapper.class, Long.class);
        org.mongojack.DBCursor<IDFWrapper> find = coll.find();
         if (!find.hasNext()){
            coll.insert(idfWrapper);
        }
        else {
            coll.update(DBQuery.empty(), idfWrapper);
        }
    }

    
   
    public void incrementTotalDocs(int cnt) {
        fetchIDFMap();
        idfWrapper.totalDocs += cnt;
        updateIDFMap();
    }
    
    public void decrementTotalDocs(int cnt) {
        fetchIDFMap();
        idfWrapper.totalDocs -= cnt;
        if (idfWrapper.totalDocs < 0)
            idfWrapper.totalDocs = 0;
        updateIDFMap();
    }
    
}
