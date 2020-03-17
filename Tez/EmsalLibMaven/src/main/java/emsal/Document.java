package emsal;

import java.io.IOException;
import java.util.UUID;

public class Document {
    private String id;
    private UUID belgeID;
    private UUID dosyaID;
    private int belgeTipi;
    private String content;
    private DocumentRepresentation representation;

    public Document(UUID dosyaID, UUID belgeID, int belgeTipi, String content) throws IOException {
        this.dosyaID = dosyaID;
        this.belgeID = belgeID;
        this.belgeTipi = belgeTipi;
        representation = RepresentationManager.instance().createRepresentation(content);
        RepresentationManager.instance().incrementIDFs(representation.getLemmaCounts());
    }

    public Document() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getBelgeID() {
        return belgeID;
    }

    public void setBelgeID(UUID belgeID) {
        this.belgeID = belgeID;
    }

    public UUID getDosyaID() {
        return dosyaID;
    }

    public void setDosyaID(UUID dosyaID) {
        this.dosyaID = dosyaID;
    }

    public int getBelgeTipi() {
        return belgeTipi;
    }

    public void setBelgeTipi(int belgeTipi) {
        this.belgeTipi = belgeTipi;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DocumentRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(DocumentRepresentation representation) {
        this.representation = representation;
    }
}
