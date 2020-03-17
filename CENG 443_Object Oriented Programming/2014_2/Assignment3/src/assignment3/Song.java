/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3;

import java.io.Serializable;

/**
 *
 * @author Yamak
 */
public class Song implements Serializable{
    protected SongInfo info;
    protected byte[] data = null;
    
    public Song (String name, String artist, String genre, String user) {
        info.setName(name);
        info.setArtist(artist);
        info.setGenre(genre);
    }

    Song(SongInfo arg, byte[] data) {
         info = arg;
         this.data = data;
    }

    /**
     * @return the info
     */
    public SongInfo getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(SongInfo info) {
        this.info = info;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    
    
    
}
