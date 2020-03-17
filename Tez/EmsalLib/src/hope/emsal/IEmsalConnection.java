package hope.emsal;

import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *The interface that implements the required operations of the Emsal module. Uses a single class for all inputs and another class for retur n type. They are subject to change as required. 
 * 
 * 
 * @author Yamaç Kurtuluş
 * @see ResponseArgs
 * @see RequestArgs
 */
public interface IEmsalConnection {
    
     /**
     * The method to fetch the count of the relevant cases from the database.
     * @param dosyaID ID of the case of which the relevant cases are to be fetched
     * @return the ResponseArgs object that has an empty {@link ResponseArgs.CaseRelevancyPair} list, status code and count of the relevant cases in the database
     */
    public int fetchRelevantCount(long dosyaID);
    
    /**
     * The method to fetch the relevant cases from the database. Returns cases in the {@link ResponseArgs} object sorted from most relevant to the least relevant.
     * @param dosyaID ID of the case of which the relevant cases are to be fetched
     * @return a list of {@link ResponseArgs.CaseRelevancyPair} object that has the related cases and the relevancy of these cases
     */
    public ArrayList<CaseRelevancyPair> fetchRelevantDocuments(long dosyaID);
    
    /**
     * The method to delete the specified document from the database. 
     * @param dosyaID the ID of the case to delete the document from
     * @param documentID the ID of the document to delete
     * @return the status code of the operation
     */
    public StatusCode deleteDocument(long dosyaID, long documentID);
    
    /**
     * The method to insert the given document into the database. 
     * @param dosyaID the ID of the case to insert the document into.
     * @param documentID the ID of the document to be inserted
     * @param content the content in the document in form of a string
     * @return the status code of the operation
     */
    public StatusCode insertDocument(long dosyaID, long documentID, int belgeTipi, String content) throws IOException;
    
    /**
     * The method to update the given document in the database
     * @param dosyaID the ID of the case to be updated
     * @param documentID the ID of the document to be updated
     * @param content the new content to be changed
     * @return the status code of the operation
     */
    public StatusCode updateDocument(long dosyaID, long documentID, String content);
    
     /**
     * The method to create a new case in the database
     * @param dosyaID the ID of the case to be created
     * @param davaTipi 
     * @param davaTuru
     * @param davaAltTuru
     * @return the status code of the operation
     */
    public StatusCode createCase(long dosyaID, int davaTipi, int davaTuru, int davaAltTuru);
    
         /**
     * The method to insert the given case into the database. 
     * @param dosyaID the ID of the case to be deleted
     * @return the status code of the operation
     */
    public StatusCode deleteCase(long dosyaID);
    
         /**
     * The method to change the type of the given case
     * @param dosyaID
     * @param davaTipi
     * @param davaTuru
     * @param davaAltTuru
     * @return the status code of the operation
     */
    public StatusCode changeCaseType(String dosyaID, int davaTipi, int davaTuru, int davaAltTuru);
    /**
     * Status codes of the operation. More is to be added if required
     */
    public enum StatusCode {
        /**
         * 0 means successful operation
         */
        SUCCESS,
        /**
         * 1 means the supplied ID is not found
         */
        ID_NOT_FOUND,
        /**
         * 2 means no relevant documents were found in the database
         */
        NO_RELEVANT_DOCUMENTS,
        /**
         * 3 means there was an error during insert operation
         */
        INSERT_FAILED,
        /**
        * 4 means there was an error during delete operation
        */
        DELETE_FAILED,
        /**
        * 5 means there was an error during update operation
        */
        UPDATE_FAILED,
        /**
         * 6 means there's already an object with given ID
         */
        ALREADY_EXISTS
        
    }
}
