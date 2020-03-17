/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal;

/**
 *
 * @author Yamak
 */
public class CaseRelevancyPair {
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
