/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal.reasoner;
import hope.emsal.data.DocumentRepresentation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.Token;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;
import zemberek.tokenizer.ZemberekLexer;

/**
 *
 * @author Yamak
 */
public class ThesisProj {

    /**
     * @param args the command line arguments
     */
    public static FileWriter writer;
    public static String inputFolder = "C:\\Users\\Yamak\\Documents\\TezDataOutput";
    public static String docsFile = "docs.txt";
    public static ZemberekLexer lexer;
    public static TurkishMorphParser parser;
    public static HashMap <String, Double> idfMap;
    public static ArrayList <DocumentRepresentation> docs ;
    public static int docCnt;
    
    public static void writeMatrixToCSV (String fname, double mat[][]) throws IOException {
        PrintWriter w = new PrintWriter (fname); 
        w.print(",");
        for (int i = 0; i<docs.size(); i++) {
            w.print(docs.get(i).fileName + ",");
        }
        w.append('\n');
        for (int i = 0; i<docs.size(); i++) {
            w.print(docs.get(i).fileName + ","); 
            for (int j = 0; j<docs.size(); j++) {
                w.print(mat[i][j] + ",");                        
            }
            w.print(docs.get(i).getDocClass() + "\n");
        }
        w.close();
    }
    
    public static ArrayList<String> detectSentences(String input) {
        SentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector();
        ArrayList<String> sentences = (ArrayList<String>) detector.getSentences(input);
        return sentences;
    }
    
    //find cosine similarity between 2 documents
    public static double cosineSimiliarity (DocumentRepresentation d1, DocumentRepresentation d2) {
        Iterator iterator = idfMap.entrySet().iterator();
        
        double dot = 0.0, d1L = 0.0, d2L = 0.0, tmp1, tmp2;
        while (iterator.hasNext()) {
            Map.Entry<String, Double> e = (Map.Entry<String, Double>) iterator.next();
            tmp1 = d1.vectorRepresentation.get(e.getKey());
            tmp2 = d2.vectorRepresentation.get(e.getKey());
            dot += tmp1 * tmp2;
            d1L += tmp1 * tmp1;
            d2L += tmp2 * tmp1;
        }
        
        d1L = Math.sqrt(d1L);
        d2L = Math.sqrt(d2L);
        return dot / (d1L * d2L);
    }
    
    public static double pearsonSimilarity (DocumentRepresentation d1, DocumentRepresentation d2) {
        Iterator iterator = idfMap.entrySet().iterator();
        double sum1 = 0.0, sum2 = 0.0, sqsum1 = 0.0, sqsum2 = 0.0, t1, t2, prod = 0.0;
        while (iterator.hasNext()) {
            Map.Entry<String, Double> e = (Map.Entry<String, Double>) iterator.next();
            t1 = d1.vectorRepresentation.get(e.getKey());
            t2 = d2.vectorRepresentation.get(e.getKey());
            sum1 += t1;
            sum2 += t2;
            sqsum1 += t1 * t1;
            sqsum2 += t2 * t2;
            prod += t1 * t2;
            
        }
        double n = (double) idfMap.size();
        double denom = (sqsum1 - (sum1*sum1) / n) * (sqsum2 - (sum2*sum2) / n);
        double nom = prod - (sum1 * sum2) / n;
        if (denom == 0)
            return 0.0;
        else 
            return nom / Math.sqrt(denom);
    }
    
    public static double jacardSimilarity (DocumentRepresentation d1, DocumentRepresentation d2) {
        Iterator iterator = idfMap.entrySet().iterator();
        boolean tmp1, tmp2;
        double m11 = 0.0, notM11 = 0.0;
        while (iterator.hasNext()) {
            Map.Entry<String, Double> e = (Map.Entry<String, Double>) iterator.next();
            tmp1 = d1.lemmaCounts.containsKey(e.getKey());
            tmp2 = d2.lemmaCounts.containsKey(e.getKey());
            if (tmp1 && tmp2)
                m11+=1.0;
            else if (tmp1 || tmp2)
                notM11+=1.0;
        }
        return m11/(notM11 + m11);
        
    }
           

    public static void main(String[] args) {
        try {
            parser = TurkishMorphParser.createWithDefaults();
            lexer = new ZemberekLexer();
            File dir = new File (inputFolder);
            File[] dirList = dir.listFiles();
            BufferedReader reader;
            docs = new ArrayList<>();
            docCnt = dirList.length;
            idfMap = new HashMap<>();
            //process documents;
            for (File f : dirList) {
               if (!f.isDirectory()) {
               String text = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8); 
               ArrayList<String> sentences = detectSentences(text);
               DocumentRepresentation d = new DocumentRepresentation (sentences, f.getName());
               countDocumentWords(d);
               docs.add(d);
               Files.write(Paths.get("txt/output/all"+ f.getName()), d.toString().getBytes());  }
            }
            PrintWriter docsWriter = new PrintWriter (docsFile);
            docsWriter.append("-------------------------IDFs--------------------------\n\n");
            for (Map.Entry<String, Double> entrySet : idfMap.entrySet()) {
                String key = entrySet.getKey();
                Double value = entrySet.getValue();
                entrySet.setValue(zemberek.core.math.LogMath.log10ToLog(1.0 + ((double) docCnt) / value)); //normalization
                docsWriter.append(key + ": " + value + " , norm: " + entrySet.getValue() );
            }
            docsWriter.append("-------------------------Documents--------------------------\n\n");
            for (DocumentRepresentation doc : docs){
                doc.prepareRepresentation(idfMap);
                docsWriter.append (doc + "\n---------------------o-----------------------\n");
            }
            docsWriter.close();
            
            //calculate similarities
            double [][] cosSims = new double [docs.size()][docs.size()];
            double [][] jacSims = new double [docs.size()][docs.size()];
            double [][] pearsonSims = new double [docs.size()][docs.size()];
            PrintWriter matchFileCosine = new PrintWriter ("cosMatch.txt");
            PrintWriter matchFilePearson = new PrintWriter ("peaMatch.txt");            
            PrintWriter matchFileJaccard = new PrintWriter ("jacMatch.txt");
            for (int i = 0; i<docs.size(); i++)
                for (int j = i; j<docs.size(); j++) {
                    double d = cosineSimiliarity(docs.get(i), docs.get(j));
                    if (d>= 0.8&&d<0.99)
                        matchFileCosine.append(docs.get(i).fileName + " - " + docs.get(j).fileName + " - dist: " + d + "\n");
                 //   System.out.println("cos:" + docs.get(i).fileName + " - " + docs.get(j).fileName + ": " + d);
                    cosSims[i][j] = d;
                    cosSims[j][i] = d;
                    d = jacardSimilarity(docs.get(i), docs.get(j));
                    if (d>= 0.8&&d<0.99)
                        matchFileJaccard.append(docs.get(i).fileName + " - " + docs.get(j).fileName + " - dist: " + d + "\n");
                //    System.out.println("jac:" + docs.get(i).fileName + " - " + docs.get(j).fileName + ": " + d + "\n");
                    jacSims[i][j] = d;
                    jacSims[j][i] = d;
                    d = pearsonSimilarity(docs.get(i), docs.get(j));
                    if (d>= 0.8&&d<0.99)
                        matchFilePearson.append(docs.get(i).fileName + " - " + docs.get(j).fileName + " - dist: " + d + "\n");
                //    System.out.println("pearsons:" + docs.get(i).fileName + " - " + docs.get(j).fileName + ": " + d + "\n");
                    pearsonSims[i][j] = d;
                    pearsonSims[j][i] = d;
                }
            matchFileCosine.close();
            matchFileJaccard.close();
            matchFilePearson.close();
            writeMatrixToCSV("jacSims.csv", jacSims);
            writeMatrixToCSV("cosSims.csv", cosSims);
            writeMatrixToCSV("pearsonSims.csv", pearsonSims);
            Evaluator e = new Evaluator(docs, "cosSims");
            e.distMatrix = cosSims;
            //e.leaveOneOutEvaluate_kNN();
        }
        
        
        catch (FileNotFoundException ex) {
                System.out.println("File not found");
            } 
        catch (UnsupportedEncodingException ex) {
            System.out.println("No Such Encoding");
        } catch (IOException ex) {
            Logger.getLogger(ThesisProj.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
}
    
    

