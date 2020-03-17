/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import org.mongojack.ObjectId;
/**
 *
 * @author Yamak
 */
public class Case {
    private String id;
    private long dosyaID;
    private int davaTipi;
    private int davaTuru;
    private int davaAltTuru;
    
    private HashMap<Long, Document> belgeMap;
    private HashMap<Long, Hareket> hareketMap;
    
    public Case(long dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        this.dosyaID = dosyaID;
        this.davaAltTuru = davaAltTuru;
        this.davaTuru = davaTuru;
        this.davaTipi = davaTipi;
        belgeMap = new HashMap<>();
        hareketMap = new HashMap<>();
    }
    
    public Case() {
        
    }
    
    public void addBelge(Document belge) {
        getBelgeMap().put(belge.belgeID, belge);
    }
    
    public void addHareket (Hareket hareket) {
        getHareketMap().put(hareket.hareketID, hareket);
    }
    
    public void removeBelge (long belge_id) {
        Document remove = getBelgeMap().remove(belge_id);
    }
    
    public void removeHareket (long hareket_id) {
        Hareket remove = getHareketMap().remove(hareket_id);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    

    /**
     * @return the dosyaID
     */
    public long getDosyaID() {
        return dosyaID;
    }

    /**
     * @param dosyaID the dosyaID to set
     */
    public void setDosyaID(long dosyaID) {
        this.dosyaID = dosyaID;
    }

    /**
     * @return the davaTipi
     */
    public int getDavaTipi() {
        return davaTipi;
    }

    /**
     * @param davaTipi the davaTipi to set
     */
    public void setDavaTipi(int davaTipi) {
        this.davaTipi = davaTipi;
    }

    /**
     * @return the davaTuru
     */
    public int getDavaTuru() {
        return davaTuru;
    }

    /**
     * @param davaTuru the davaTuru to set
     */
    public void setDavaTuru(int davaTuru) {
        this.davaTuru = davaTuru;
    }

    /**
     * @return the davaAltTuru
     */
    public int getDavaAltTuru() {
        return davaAltTuru;
    }

    /**
     * @param davaAltTuru the davaAltTuru to set
     */
    public void setDavaAltTuru(int davaAltTuru) {
        this.davaAltTuru = davaAltTuru;
    }

    /**
     * @return the belgeMap
     */
    public HashMap<Long, Document> getBelgeMap() {
        return belgeMap;
    }

    /**
     * @param belgeMap the belgeMap to set
     */
    public void setBelgeMap(HashMap<Long, Document> belgeMap) {
        this.belgeMap = belgeMap;
    }

    /**
     * @return the hareketMap
     */
    public HashMap<Long, Hareket> getHareketMap() {
        return hareketMap;
    }

    /**
     * @param hareketMap the hareketMap to set
     */
    public void setHareketMap(HashMap<Long, Hareket> hareketMap) {
        this.hareketMap = hareketMap;
    }
    
    @ObjectId
    @JsonProperty("_id")
    public String getId() {
      return id;
    }
    
    @ObjectId
    @JsonProperty("_id")
    public void setId(String id) {
      this.id = id;
    }
     //</editor-fold>
}
