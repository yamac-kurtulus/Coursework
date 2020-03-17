package hope.emsal;

import hope.emsal.IEmsalConnection.StatusCode;
import java.util.ArrayList;
import java.util.logging.Logger;


public class ResponseArgs {

    public static class CaseRelevancyPair {
        /**
         * The ID of the case that's returned
         */
        protected String caseID;
        /**
         * The degree of relevancy of the returned case to the original case
         */
        protected float relevancy;

        /**
         * Class constructor
         * @param caseID
         * @param relevancy
         */
        public CaseRelevancyPair(String caseID, float relevancy) {
            this.relevancy = relevancy;
            this.caseID = caseID;
        }

        /**
         * @return the relevancy
         * 
         */
        public float getRelevancy() {
            return relevancy;
        }

        /**
         * @param relevancy the relevancy to set
         */
        public void setRelevancy(float relevancy) {
            this.relevancy = relevancy;
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
        
        
        
    }  
    
   
    /**
     *The returned status code of the operation. 0 means success.
     * @see StatusCode
     */
    protected StatusCode status;
    
    /**
     * The list of relevant cases returned by fetch operation.
     */
    protected ArrayList<CaseRelevancyPair> relevantCases;
    
    /**
     * The count of relevant cases. Especially useful in a fetch count operation. 
     */
    protected int count;
    
    /**
     * Class constructor
     * @param status
     * @param relevantCases
     * @param count 
     */
    public ResponseArgs(StatusCode status, ArrayList<CaseRelevancyPair> relevantCases, int count) {
        this.status = status;
        this.relevantCases = relevantCases;
        this.count = count;
    }

    /**
     * Class Constructor with initialized list of relevantCases
     * @param status
     * @param count 
     */
    public ResponseArgs(StatusCode status, int count) {
        this.status = status;
        this.relevantCases = new ArrayList<>();
        this.count = count;
    }
    
    /**
     * Adds the relevant case and the relevancy information to the list
     * @param caseID to add
     * @param relevancy of the case
     */
    public void addRelevantCase (String caseID, float relevancy) {
        CaseRelevancyPair valToAdd = new CaseRelevancyPair(caseID, relevancy);
        this.getRelevantCases().add(valToAdd);
    }
    
    /**
     * @param index of the {@link CaseRelevancyPair}
     * @return the {@link CaseRelevancyPair} at the index
     */
    public CaseRelevancyPair getCase (int index) {
        return getRelevantCases().get(index);
    }
    
    /**
     * @return the status
     */
    public StatusCode getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(StatusCode status) {
        this.status = status;
    }

    /**
     * @return the relevantCases
     */
    public ArrayList<CaseRelevancyPair> getRelevantCases() {
        return relevantCases;
    }

    /**
     * @param relevantCases the relevantCases to set
     */
    public void setRelevantCases(ArrayList<CaseRelevancyPair> relevantCases) {
        this.relevantCases = relevantCases;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
    private static final Logger LOG = Logger.getLogger(ResponseArgs.class.getName());
    
    
    
    
    
    
}
