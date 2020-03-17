package hope.emsal;

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
     * @param requestArgs the input case information of which the relevant cases are to be fetched
     * @return the ResponseArgs object that has an empty {@link ResponseArgs.CaseRelevancyPair} list, status code and count of the relevant cases in the database
     */
    public ResponseArgs fetchRelevantCount(RequestArgs requestArgs);
    
    /**
     * The method to fetch the relevant cases from the database. Returns cases in the {@link ResponseArgs} object sorted from most relevant to the least relevant.
     * @param requestArgs the input case information of which the relevant cases are to be fetched
     * @return the ResponseArgs object that has all the related cases, their relevancy, status code and count
     */
    public ResponseArgs fetchRelevantDocuments(RequestArgs requestArgs);
    
    /**
     * The method to delete the given case from the database. 
     * @param requestArgs the input case information of the case to be deleted. The ID would be sufficient, but subject to change
     * @return the ResponseArgs object that has an empty {@link ResponseArgs.CaseRelevancyPair} list, status code and 0 as count
     */
    public ResponseArgs deleteDocument(RequestArgs requestArgs);
    
    /**
     * The method to insert the given case into the database. 
     * @param requestArgs the input case information of the case to be inserted. 
     * @return the ResponseArgs object that has an empty {@link ResponseArgs.CaseRelevancyPair} list, status code and 0 as count
     */
    public ResponseArgs insertDocument(RequestArgs requestArgs);
    /**
     * The method to update the given case into the database. 
     * @param requestArgs the input case information of the case to be updated. The new content should be in the content field, along with the case id. 
     * @return the ResponseArgs object that has an empty {@link ResponseArgs.CaseRelevancyPair} list, status code and 0 as count
     */
    public ResponseArgs updateDocument(RequestArgs requestArgs);
    
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
        UPDATE_FAILED
    }
}
