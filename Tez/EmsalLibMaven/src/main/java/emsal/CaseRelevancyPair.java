package emsal;

import java.util.UUID;

public class CaseRelevancyPair {

    protected UUID targetCaseID;
    protected UUID comparedCaseID;
    protected float relevancy;

    public CaseRelevancyPair(UUID targetCaseID, UUID comparedCaseID, float relevancy) {
        this.relevancy = relevancy;
        this.targetCaseID = targetCaseID;
        this.comparedCaseID = comparedCaseID;
    }

    public float getRelevancy() {
        return relevancy;
    }

    public void setRelevancy(float relevancy) {
        this.relevancy = relevancy;
    }

    public UUID getTargetCaseID() {
        return targetCaseID;
    }

    public void setTargetCaseID(UUID caseID) {
        this.targetCaseID = caseID;
    }

    public UUID getComparedCaseID() {
        return comparedCaseID;
    }

    public void setComparedCaseID(UUID caseID) {
        this.comparedCaseID = caseID;
    }

}