/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3datagen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yamak
 */
public class Assignment3DataGen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random rng = new Random();
        int size;
        byte[] s;
        byte[] dig;
        FileOutputStream f1;
        FileWriter f2 = null;    
        MessageDigest d;
        BigInteger bi;
        try {
            f2 = new FileWriter("hashes.txt");
            for (int i = 0; i<10; i++) {
                size = rng.nextInt(9240) + 1024;
                s = new byte[size];
                f1 = new FileOutputStream("input"+i+".audio");
                d = MessageDigest.getInstance("MD5");
                rng.nextBytes(s);
                dig = d.digest(s);
                f1.write(s);
                bi = new BigInteger (1, dig);
                
                f2.append(bi.toString(16) + '\n');
            }
            f2.flush();
            f2.close();
        } catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(Assignment3DataGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
