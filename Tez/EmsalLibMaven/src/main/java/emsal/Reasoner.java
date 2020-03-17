/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emsal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Yamak
 */
public class Reasoner {
    
    RepresentationManager representationManager;
    
    
    Reasoner(RepresentationManager representationManager) {
        this.representationManager = representationManager;
    }
    
    public CaseRelevancyPair getSimilarCase (Case targetCase, Case comparedCase) {
        HashMap<String, Double> targetV = representationManager.getVector(targetCase.getAllDocs());
        HashMap<String, Double> comparedV = representationManager.getVector(comparedCase.getAllDocs());
        double relevancy = cosineSimiliarity(targetV, comparedV);
        CaseRelevancyPair rVal = new CaseRelevancyPair(targetCase.getDosyaID(), comparedCase.getDosyaID(), (float) relevancy);
        return rVal;
                 
    }
    
    public double cosineSimiliarity (HashMap<String, Double> d1, HashMap<String, Double> d2) {
        Iterator iterator = representationManager.getIdfMap().entrySet().iterator();
        
        double dot = 0.0, d1L = 0.0, d2L = 0.0, tmp1, tmp2;
        while (iterator.hasNext()) {
            Map.Entry<String, Double> e = (Map.Entry<String, Double>) iterator.next();
            tmp1 = d1.get(e.getKey());
            tmp2 = d2.get(e.getKey());
            dot += tmp1 * tmp2;
            d1L += tmp1 * tmp1;
            d2L += tmp2 * tmp2;
        }
        
        d1L = Math.sqrt(d1L);
        d2L = Math.sqrt(d2L);
        return dot / (d1L * d2L);
    }
    
    
}
