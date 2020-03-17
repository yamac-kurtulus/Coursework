package emsal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongojack.ObjectId;

import java.util.HashMap;
import java.util.UUID;

public class Case {
    private String _id;
    private UUID dosyaID;

    private HashMap<UUID, Document> belgeMap;
    private HashMap<Long, Hareket> hareketMap;
    private int davaTipi;
    private int davaTuru;
    private int davaAltTuru;
    private int totalLemmas;
    private DocumentRepresentation allDocs;

    public Case(UUID dosyaID, int davaTipi, int davaTuru, int davaAltTuru) {
        this.dosyaID = dosyaID;
        this.davaAltTuru = davaAltTuru;
        this.davaTuru = davaTuru;
        this.davaTipi = davaTipi;
        belgeMap = new HashMap<>();
        hareketMap = new HashMap<>();
        allDocs = new DocumentRepresentation();
    }

    public Case() {
    }

    public Document addBelge(Document belge) {
        Document doc;
        belgeMap.put(belge.getBelgeID(), belge);
        doc = belgeMap.get(belge.getBelgeID());
        belge.getRepresentation().getLemmaCounts().forEach((String k, Integer v) -> allDocs.getLemmaCounts().merge(k, v, Integer::sum));
        totalLemmas += belge.getRepresentation().getTotalWords();
        return doc;
    }

    public Hareket addHareket(Hareket hareket) {
        return getHareketMap().put(hareket.hareketID, hareket);
    }

    public Document removeBelge(UUID belge_id) {
        return getBelgeMap().remove(belge_id);
    }

    public Hareket removeHareket(long hareket_id) {
        return getHareketMap().remove(hareket_id);
    }

    public UUID getDosyaID() {
        return dosyaID;
    }

    public void setDosyaID(UUID dosyaID) {
        this.dosyaID = dosyaID;
    }

    public int getDavaTipi() {
        return davaTipi;
    }

    public void setDavaTipi(int davaTipi) {
        this.davaTipi = davaTipi;
    }

    public int getDavaTuru() {
        return davaTuru;
    }

    public void setDavaTuru(int davaTuru) {
        this.davaTuru = davaTuru;
    }

    public int getDavaAltTuru() {
        return davaAltTuru;
    }

    public void setDavaAltTuru(int davaAltTuru) {
        this.davaAltTuru = davaAltTuru;
    }

    public HashMap<UUID, Document> getBelgeMap() {
        return belgeMap;
    }

    public void setBelgeMap(HashMap<UUID, Document> belgeMap) {
        this.belgeMap = belgeMap;
    }

    public HashMap<Long, Hareket> getHareketMap() {
        return hareketMap;
    }

    public void setHareketMap(HashMap<Long, Hareket> hareketMap) {
        this.hareketMap = hareketMap;
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

    public DocumentRepresentation getAllDocs() {
        return allDocs;
    }

    public void setAllDocs(DocumentRepresentation allDocs) {
        this.allDocs = allDocs;
    }

    public int getTotalLemmas() {
        return totalLemmas;
    }

    public void setTotalLemmas(int totalLemmas) {
        this.totalLemmas = totalLemmas;
    }
}
