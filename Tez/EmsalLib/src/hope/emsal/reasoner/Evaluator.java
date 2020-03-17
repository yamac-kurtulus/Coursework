/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.reasoner;

import hope.emsal.data.DocumentRepresentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import zemberek.core.math.DoubleArrays;

/**
 *
 * @author Yamak
 */
public class Evaluator {
    ArrayList <DocumentRepresentation> documents;
    HashMap <String, String> confusion;
    HashMap <String, Integer> classCounts;
    String evaluatorName;
    double accuracy;
    double FP, FN, TP, TN, tot;
    double[][] distMatrix; 
    
    public Evaluator(ArrayList <DocumentRepresentation> documents, String evaluatorName) {
        this.documents = documents;
        accuracy = 0;
        FP = FN = TP = TN = 0;
        this.evaluatorName = evaluatorName;
        confusion = new HashMap<>();
        classCounts = new HashMap<>();
        Iterator it = documents.iterator();
        while (it.hasNext()) {
            DocumentRepresentation d = (DocumentRepresentation) it.next();
            if (!classCounts.containsKey(d.getDocClass()))
                classCounts.put (d.getDocClass(), 0);
        }


    }
    
    public void leaveOneOutEvaluate_kNN () {
        accuracy = 0.0;
        FP = FN = TP = TN = tot = 0.0;
        confusion.clear();
        Iterator it = classCounts.keySet().iterator();
        while (it.hasNext()) {
            Map.Entry e =  (Map.Entry) it.next();
            e.setValue(0);
        }
        //create confusion matrix
        for (DocumentRepresentation d : documents) {
            confusion.put (d.getDocClass(), kNN(3, d));
        }
        
        it = confusion.keySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            if (e.getKey().equals(e.getValue())) {
                TP++;
                tot++;
            }
            else 
                tot++;
        }
        accuracy = TP / tot;
    }
    
    public String kNN(int k, DocumentRepresentation d) {
        int idx = documents.indexOf(d); 
        ArrayList<Double> tmpDist = new ArrayList<>();
        ArrayList<DocumentRepresentation> kdocs = new ArrayList<>(documents);
        for (int i = 0; i<distMatrix.length; i++)
            tmpDist.add(distMatrix[idx][i]);
        
        //reset class counts
        Iterator it = classCounts.keySet().iterator();
        while (it.hasNext()) {
            Map.Entry e =  (Map.Entry) it.next();
            e.setValue(0);
        }
        int n = 0;
        return documents.get(n).getDocClass();
    }
}
    