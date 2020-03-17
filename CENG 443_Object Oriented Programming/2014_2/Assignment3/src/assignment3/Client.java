/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.security.provider.MD5;

/**
 *
 * @author Yamak
 */
public class Client {
    protected String workingDirectory;
    protected SongInfo arg;
    protected ISongInfoService service;
    protected Scanner scanner;
    protected Random rng;
    protected MessageDigest md;
    
    
    public static void main(String[] args) {
    Client self = new Client ();
         
        try {
            self.setService((ISongInfoService) Naming.lookup("rmi://localhost:1200/peertopeer"));
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        self.processInput ();
    }

    public Client() {
        this.arg = new SongInfo();
        scanner = new Scanner(System.in);
        rng = new Random();
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void processInput () {

    String cmd = ""; 
    String input;
    String tmp;
    boolean fail = true;
    do {
        
            if (fail)
                System.out.println("Usage: \n"
                        + "get <filename>: Read the audio file and combine it with the info\n"
                        + "read <filename> | *: read the serialized song and print its info\n"
                        + "update <filename>: Update the song info\n");
            
            System.out.print("> ");
            input = getScanner().nextLine();
            if (input=="") 
                input = getScanner().nextLine();
            cmd = input.substring(0, input.indexOf(" ")).trim();
            input = input.replaceFirst(cmd, "");
            input = input.trim();
            Song s;
            try {
                switch (cmd) {
                case "get":
                    byte[] b;
                    File f1 = new File (input);
                    b = new byte[(int) f1.length()];
                    FileInputStream f = new FileInputStream(input);
                    f.read(b);
                    md = MessageDigest.getInstance("MD5");
                    BigInteger bigInt = new BigInteger(1, md.digest(b));
                    SongInfo inf = new SongInfo();
                    inf.setHash(bigInt.toString(16));
                    inf = this.getService().getSong(inf);
                    Song sng = new Song (inf, b);
                    System.out.println("song is ready");
                    System.out.println (sng.getInfo().toString());
                    System.out.print("\n");
                    System.out.print("Filename to save the song to: ");
                    String output = getScanner().next();
                    this.writeSong(sng, output);
                    break;
                case "read":
                    if (!"*".equals(input)) {
                        s = readSong(input);
                        System.out.printf("%20s %20s %20s %6s %20s\n", "name", "artist", "album", "year", "genre");
                        System.out.println(s.getInfo().toString());
                    }
                    else {
                        File dir = new File (System.getProperty("user.dir"));
                        File [] list = dir.listFiles(new FilenameFilter() {
                            
                            @Override
                            public boolean accept(File dir, String name) {
                                 // get last index for '.' char
                                int lastIndex = name.lastIndexOf('.');
                  
                                  // get extension
                                if (lastIndex > 0) {
                                 String str = name.substring(lastIndex);
                  
                                  // match path name extension  
                                if(str.equals(".ser"))
                                {
                                   return true;
                                } }
                            return false;
                            }
                        });
                        System.out.printf("%20s %20s %20s %6s %20s\n", "name", "artist", "album", "year", "genre");
                        for (File tempfile : list) {
                        s = readSong(tempfile.getName());
                        System.out.println(s.getInfo().toString());
                        }
                    }
                    break;
                case "update":
                    s = readSong(input);
                    System.out.println("Leave empty to skip");
                    setArg(s.getInfo());
                    System.out.printf("Name (%s): ", getArg().getName());
                    tmp = getScanner().nextLine();
                    arg.setName("".equals(tmp) ? getArg().getName() : tmp);
                    System.out.printf("Artist (%s): ", getArg().getArtist());
                    tmp = getScanner().nextLine();
                    arg.setArtist("".equals(tmp) ? getArg().getArtist() : tmp);
                    System.out.printf("Album (%s): ", getArg().getAlbum());
                    tmp = getScanner().nextLine();
                    arg.setAlbum("".equals(tmp) ? getArg().getAlbum() : tmp);
                    System.out.printf("Year (%d): ", getArg().getYear());
                    tmp = getScanner().nextLine();
                    arg.setYear("".equals(tmp) ? getArg().getYear() : Integer.parseInt(tmp));
                    System.out.printf("Genre (%s): ", getArg().getGenre());
                    tmp = getScanner().nextLine();
                    arg.setGenre("".equals(tmp) ? getArg().getGenre() : tmp);
                    s.setInfo(getArg());
                    this.writeSong(s, input);
                    getService().addSong(getArg());
                    break;
            }
        } catch (RemoteException |  PeerToPeerException | NumberFormatException | FileNotFoundException ex) {
           System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }while (!"exit".equals(cmd));
    
    }
    
    void writeSong (Song s, String fileName) throws PeerToPeerException {
        FileOutputStream fileOut = null;
        try {
            //fileName = this.workingDirectory + "/" + fileName;
            fileOut = new FileOutputStream (fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream (fileOut);
            objectOut.writeObject(s);
            objectOut.close();
            fileOut.close();
        } catch (FileNotFoundException ex) {
            throw new PeerToPeerException("File Cannot be created");
        } catch (IOException ex) {
            throw new PeerToPeerException("Serialisation Error");
        } 
        
    }
    
    Song readSong(String fileName) throws PeerToPeerException {
        FileInputStream fileIn = null; 
        Song rv = null;
        try {
            fileIn = new FileInputStream (fileName);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            rv = (Song) objIn.readObject();
            objIn.close();
            fileIn.close();
           
        } catch (FileNotFoundException ex) {
            throw new PeerToPeerException("File not found");
        } catch (IOException ex) {
            throw new PeerToPeerException("Deserialisation Error");
        } catch (ClassNotFoundException ex) {
            throw new PeerToPeerException("Class not found");
        }
        return rv;
    }

    /**
     * @return the workingDirectory
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * @return the arg
     */
    public SongInfo getArg() {
        return arg;
    }

    /**
     * @param arg the arg to set
     */
    public void setArg(SongInfo arg) {
        this.arg = arg;
    }

    /**
     * @return the service
     */
    public ISongInfoService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(ISongInfoService service) {
        this.service = service;
    }

    /**
     * @return the scanner
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * @param scanner the scanner to set
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * @return the rng
     */
    public Random getRng() {
        return rng;
    }

    /**
     * @param rng the rng to set
     */
    public void setRng(Random rng) {
        this.rng = rng;
    }

    /**
     * @return the md
     */
    public MessageDigest getMd() {
        return md;
    }

    /**
     * @param md the md to set
     */
    public void setMd(MessageDigest md) {
        this.md = md;
    }
    
}
