/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Stub class that implements the IEmsalConnection for test purposes
 * @author Yamaç Kurtuluş
 */
public class EmsalStub implements IEmsalConnection{

    
    /**
     * Stub method for count
     * @param requestArgs the case information
     * @return empty {@link ResponseArgs} with 3 as count
     */
    @Override
    public ResponseArgs fetchRelevantCount(RequestArgs requestArgs) {
        return new ResponseArgs (StatusCode.SUCCESS, 3);
    }

    /**
     * Stub method for fetch
     * @param requestArgs the case information
     * @return {@link ResponseArgs} with a non-empty list of {@link ResponseArgs.CaseRelevancyPair}
     */
    @Override
    public ResponseArgs fetchRelevantDocuments(RequestArgs requestArgs) {
        ArrayList <ResponseArgs.CaseRelevancyPair> cases = new ArrayList<> ();
        cases.add(new ResponseArgs.CaseRelevancyPair ("c1", 0.5f));
        cases.add(new ResponseArgs.CaseRelevancyPair ("c2", 0.3f));
        cases.add(new ResponseArgs.CaseRelevancyPair ("c3", 0.7f));
        return new ResponseArgs(StatusCode.SUCCESS, cases, cases.size());
    }
 
    /**
     * Stub method for delete
     * @param requestArgs the case information
     * @return {@link ResponseArgs} with only a status code
     */
    @Override
    public ResponseArgs deleteDocument(RequestArgs requestArgs) {
        return new ResponseArgs (StatusCode.SUCCESS, 0);
    }

    /**
     * Stub method for insert
     * @param requestArgs the case information
     * @return {@link ResponseArgs} with only a status code
     */
    @Override
    public ResponseArgs insertDocument(RequestArgs requestArgs) {
        return new ResponseArgs (StatusCode.SUCCESS, 0);
    }

    /**
     * Stub method for update
     * @param requestArgs the case information
     * @return {@link ResponseArgs} with only a status code
     */
    @Override
    public ResponseArgs updateDocument(RequestArgs requestArgs) {
        return new ResponseArgs (StatusCode.SUCCESS, 0);
    }
    private static final Logger LOG = Logger.getLogger(EmsalStub.class.getName());
    
}
