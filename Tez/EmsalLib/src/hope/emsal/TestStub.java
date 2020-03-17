/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hope.emsal;

import hope.emsal.IEmsalConnection.StatusCode;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yamak
 */
public class TestStub {
   
    
    EmsalStub stub;
    
    public static String info = "usage:\n"
            + "count <dosyaID>\n"
            + "fetch <dosyaID>\n"
            + "deleteCase <dosyaID>\n"
            + "deleteDoc <dosyaID> <docID>\n"
            + "createCase <dosyaID> <davaTipi> <davaTuru> <davaAltTuru>\n"
            + "insertDoc <dosyaID> <docID> <content>\n"
            + "updateDoc <dosyaID> <docID> <newContent>\n";

    
    public static void main (String args[]) {
        TestStub testStub;
        try {
            testStub = new TestStub();
        } catch (IOException ex) {
            Logger.getLogger(TestStub.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            try {
                testStub.input ();
            } catch (IOException ex) {
                Logger.getLogger(TestStub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void input() throws IOException {
        System.out.print(info);
        Scanner reader = new Scanner (System.in);
        String input = reader.nextLine();
        String[] args = input.split(" ");
        StatusCode s ;
        long dosyaID, docID;
        int davaTipi, davaTuru, davaAltTuru;
        String content;
        switch (args[0]) {
            case "createCase":
                dosyaID = Long.parseLong(args[1]);
                davaTipi = Integer.parseInt(args[2]);
                davaTuru = Integer.parseInt(args[3]);
                davaAltTuru = Integer.parseInt(args[4]);
                s = stub.createCase(dosyaID, davaTipi, davaTuru, davaAltTuru);
                System.out.println(s.toString());
                break;
            case "insertDoc":
                dosyaID = Long.parseLong(args[1]);
                docID = Long.parseLong(args[2]);
                content = args[3];
                stub.insertDocument(dosyaID, docID, 3, content);
                        
        }
        
    }
    
    public TestStub () throws IOException {
        stub = new EmsalStub();
    }
    
    
}
