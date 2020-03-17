/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yamak
 */
public class Server implements ISongInfoService{

    /**
     * @param args the command line arguments
     * 
     */
    
    protected SQLConnector c;
    protected Connection conn;
    protected Random rng;
    protected String name;
    
    public Server () {
        super ();
        c = new SQLConnector();
        rng = new Random();
        name = "peertopeer";
    }
    
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname","localhost");
            System.setProperty("java.rmi.server.codebase","file:./");
            Server s = new Server ();
            ISongInfoService stub = (ISongInfoService) UnicastRemoteObject.exportObject(s, 0);
            
            Registry registry = LocateRegistry.createRegistry(1200);
            registry.bind(s.getName(), stub);
            
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
  
    @Override
    public void addSong(SongInfo arg) throws RemoteException, PeerToPeerException {
     {
            getC().insertSong(arg);
     }
    }

//    @Override
//    public void removeSong(ArgObject arg) throws RemoteException, PeerToPeerException {
//        if (arg.artist == "" || arg.genre =="" || arg.name == "")
//            throw new PeerToPeerException( "All fields must be entered to add a song" );
//    }


    @Override
    public SongInfo getSong(SongInfo arg) throws RemoteException, PeerToPeerException {
        
        return getC().getSong(arg);
        
    }

    /**
     * @return the c
     */
    public SQLConnector getC() {
        return c;
    }

    /**
     * @param c the c to set
     */
    public void setC(SQLConnector c) {
        this.c = c;
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * @param conn the conn to set
     */
    public void setConn(Connection conn) {
        this.conn = conn;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
            
