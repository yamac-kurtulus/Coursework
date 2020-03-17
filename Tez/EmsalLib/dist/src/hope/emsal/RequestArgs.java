package hope.emsal;

import java.util.logging.Logger;


/**
 * The class used as input to the methods in the IEmsalConnection interface
 * @author Yamaç Kurtuluş
 * @see IEmsalConnection
 */
public class RequestArgs {
    /**
     * The ID of the targeted case
     */
    protected String caseID;
    
    /**
     * The content of the relevant documents supplied in the case
     */
    protected String content;
    
    /**
     * Top level type of the case
     */
    protected String davaTipi;
    
    /**
     * Second level type of the case
     */
    protected String davaTuru;
    
    /**
     * Third and bottom level type of the case
     */
    protected String davaAltTuru;

    /**
     * Class Constructor
     * @param dosyaID
     * @param content
     * @param davaTipi
     * @param davaTuru
     * @param davaAltTuru 
     */
    public RequestArgs(String dosyaID, String content, String davaTipi, String davaTuru, String davaAltTuru) {
        this.caseID = dosyaID;
        this.content = content;
        this.davaTipi = davaTipi;
        this.davaTuru = davaTuru;
        this.davaAltTuru = davaAltTuru;
    }

    /**
     * @return the caseID
     */
    public String getCaseID() {
        return caseID;
    }

    /**
     * @param caseID the caseID to set
     */
    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the davaTipi
     */
    public String getDavaTipi() {
        return davaTipi;
    }

    /**
     * @param davaTipi the davaTipi to set
     */
    public void setDavaTipi(String davaTipi) {
        this.davaTipi = davaTipi;
    }

    /**
     * @return the davaTuru
     */
    public String getDavaTuru() {
        return davaTuru;
    }

    /**
     * @param davaTuru the davaTuru to set
     */
    public void setDavaTuru(String davaTuru) {
        this.davaTuru = davaTuru;
    }

    /**
     * @return the davaAltTuru
     */
    public String getDavaAltTuru() {
        return davaAltTuru;
    }

    /**
     * @param davaAltTuru the davaAltTuru to set
     */
    public void setDavaAltTuru(String davaAltTuru) {
        this.davaAltTuru = davaAltTuru;
    }
    private static final Logger LOG = Logger.getLogger(RequestArgs.class.getName());

    
    
    
}
