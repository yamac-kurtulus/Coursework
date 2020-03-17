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
public class Document {
    long belgeID;
    long dosyaID;
    int belgeTipi;
    String content;
    DocumentRepresentation representation;
    
    public Document (long dosyaID, long belgeID, int belgeTipi, String content) throws IOException {
        this.dosyaID = dosyaID;
        this.belgeID = belgeID;
        this.belgeTipi = belgeTipi;
        this.content = content;
        representation = RepresentationManager.instance().createRepresentation(content);
    }
    
}
