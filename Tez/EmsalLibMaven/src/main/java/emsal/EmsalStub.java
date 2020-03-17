/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emsal;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient; 
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.stream.JacksonDBObject;

/**
 * Stub class that implements the IEmsalConnection for test purposes
 * @author Yamaç Kurtuluş
 */
public class EmsalStub implements IEmsalConnection{
    
    RepresentationManager representationManager;
    MongoClient mongoClient;
    DB db;
    public EmsalStub () throws IOException {
        representationManager = RepresentationManager.instance();
        mongoClient = new MongoClient();
        db = mongoClient.getDB("emsal");
        representationManager.setDB(db);
    }
    
    @Override
    public int fetchRelevantCount(java.util.UUID dosyaID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<CaseRelevancyPair> fetchRelevantDocuments(UUID dosyaID) {
        int totalDocs;
        Reasoner reasoner = new Reasoner (representationManager);
        ArrayList<CaseRelevancyPair> rVal = new ArrayList <CaseRelevancyPair>();
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, Long> coll = JacksonDBCollection.wrap(caseCollection, Case.class, Long.class);
        org.mongojack.DBCursor<Case> find = coll.find(DBQuery.is("dosyaID", dosyaID));
//        totalDocs = find.count();
//        representationManager.setTotalDocs(totalDocs);
        if (!find.hasNext()) 
            return null;
         Case targetCase = find.next(), nextCase;
         HashMap belgeler = targetCase.getBelgeMap();
         find = coll.find();
         while (find.hasNext ()) {
             nextCase = find.next();
             rVal.add(reasoner.getSimilarCase(targetCase, nextCase));
         }
         //sort the map
         rVal.sort(new Comparator<CaseRelevancyPair>() {

            @Override
            public int compare(CaseRelevancyPair o1, CaseRelevancyPair o2) {
                if (o1.getRelevancy() > o2.getRelevancy())
                    return 1;
                else if (o1.getRelevancy() < o2.getRelevancy())
                    return -1;
                return 0;
            }
        });
        return rVal;
         
    }

    @Override
    public StatusCode deleteDocument(UUID dosyaID, UUID documentID) {
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, String> coll = JacksonDBCollection.wrap(caseCollection, Case.class, String.class);
        org.mongojack.DBCursor<Case> find = coll.find(DBQuery.is("dosyaID", dosyaID));
        if (!find.hasNext())
            return StatusCode.ID_NOT_FOUND;
        Case c = find.next();
        Document d = c.removeBelge(documentID);
        try {
            RepresentationManager.instance().decrementIDFs(d.getRepresentation().getLemmaCounts());
        } catch (IOException ex) {
            Logger.getLogger(EmsalStub.class.getName()).log(Level.SEVERE, null, ex);
            return StatusCode.DELETE_FAILED;
        }
        representationManager.decrementTotalDocs(1);
        return StatusCode.SUCCESS;
        
        
    }

    @Override
    public StatusCode insertDocument(UUID dosyaID, UUID documentID, int belgeTipi, String content) throws IOException {
        //DocumentRepresentation dRep = representationManager.createRepresentation(content);
        Document doc = new Document (dosyaID, documentID, belgeTipi, content);
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, Long> coll = JacksonDBCollection.wrap(caseCollection, Case.class, Long.class);
        org.mongojack.DBCursor<Case> find = coll.find(DBQuery.is("dosyaID", dosyaID));
        if (!find.hasNext()) 
            return StatusCode.ID_NOT_FOUND;
        Case c = find.next();
        c.addBelge(doc);
        coll.update(DBQuery.is("dosyaID", dosyaID), c);
        representationManager.incrementTotalDocs(1);
        return StatusCode.SUCCESS;
    }

    @Override
    public StatusCode updateDocument(UUID dosyaID, UUID documentID, String content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode createCase(UUID dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        Case c = new Case (dosyaID, davaTipi, davaTuru, davaAltTuru);
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, String> coll = JacksonDBCollection.wrap(caseCollection, Case.class, String.class);

        //do nothing if given case exists
        org.mongojack.DBCursor<Case> find = coll.find(DBQuery.is("dosyaID", dosyaID));
        if (find.curr() != null || find.hasNext()) 
                return StatusCode.ALREADY_EXISTS;
        coll.insert(c);
        return StatusCode.SUCCESS;
    }

    @Override
    public StatusCode deleteCase(UUID dosyaID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode changeCaseType(String dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ArrayList<ArrayList<CaseRelevancyPair>> fetchAll() {
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, String> coll = JacksonDBCollection.wrap(caseCollection, Case.class, String.class);
        org.mongojack.DBCursor<Case> find = coll.find();
        ArrayList <ArrayList<CaseRelevancyPair>> allRelevant = new ArrayList<>();
        while (find.hasNext()) {
            Case c = find.next();
            allRelevant.add(fetchRelevantDocuments(c.getDosyaID()));
        }
        return allRelevant;
    }
    
    
    
}
