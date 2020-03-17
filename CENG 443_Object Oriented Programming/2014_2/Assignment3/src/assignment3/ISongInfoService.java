/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Yamak
 */
public interface ISongInfoService extends Remote {
    
  // public Song[] process (String cmd, ArgObject arg) throws RemoteException, PeerToPeerException;
    public void addSong (SongInfo arg) throws RemoteException, PeerToPeerException;
 //   public void removeSong (ArgObject arg) throws RemoteException, PeerToPeerException
    public SongInfo getSong (SongInfo arg) throws RemoteException, PeerToPeerException;
    
}
