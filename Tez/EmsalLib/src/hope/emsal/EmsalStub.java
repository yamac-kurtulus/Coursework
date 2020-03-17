/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import hope.emsal.data.Case;
import hope.emsal.data.Document;
import hope.emsal.data.DocumentRepresentation;
import hope.emsal.data.RepresentationManager;
import java.io.IOException;
import java.util.ArrayList;
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
    }
    
    @Override
    public int fetchRelevantCount(long dosyaID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<CaseRelevancyPair> fetchRelevantDocuments(long dosyaID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode deleteDocument(long dosyaID, long documentID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode insertDocument(long dosyaID, long documentID, int belgeTipi, String content) throws IOException {
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
        return StatusCode.SUCCESS;
    }

    @Override
    public StatusCode updateDocument(long dosyaID, long documentID, String content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode createCase(long dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        Case c = new Case (dosyaID, davaTipi, davaTuru, davaAltTuru);
        DBCollection caseCollection = db.getCollection("CASES");
        JacksonDBCollection<Case, Long> coll = JacksonDBCollection.wrap(caseCollection, Case.class, Long.class);

        //do nothing if given case exists
        org.mongojack.DBCursor<Case> find = coll.find(DBQuery.is("dosyaID", dosyaID));
        if (find.hasNext()) 
            return StatusCode.ALREADY_EXISTS;
        //create a case with no documents
//        dbo = new BasicDBObject("_id", dosyaID)
//                .append("davaTipi", davaTipi)
//                .append("davaTuru", davaTuru)
//                .append("davaAltTuru", davaAltTuru);
        coll.insert(c);
        return StatusCode.SUCCESS;
    }

    @Override
    public StatusCode deleteCase(long dosyaID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusCode changeCaseType(String dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    
    
}
