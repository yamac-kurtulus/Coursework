/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.data;

import java.io.IOException;

/**
 *
 * @author Yamak
 */
public class Hareket {
    long hareketID;
    long caseID;
    int hareketTipi;
    String content;
    Document document;
    DocumentRepresentation representation;
    
    public Hareket (long caseID, long hareketID, int hareketTipi, String content, Document document) throws IOException {
        this.caseID = caseID;
        this.hareketID = hareketID;
        this.hareketTipi = hareketTipi;
        this.content = content;
        this.document = document;
        representation = RepresentationManager.instance().createRepresentation(content);
    }
    
}
