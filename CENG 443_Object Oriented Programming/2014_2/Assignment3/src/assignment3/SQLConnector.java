/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3;

import java.sql.*;
import java.util.Random;


/**
 *
 * @author Yamak
 */
public class SQLConnector {
    protected String driver   ;
    protected String url      ;
    protected String database ;
    protected String username ;
    protected String password ;
    protected Random rng      ;
    
    protected Driver d;
    protected Connection conn;
    protected Statement stmt;
    protected PreparedStatement getSong;
    
    public SQLConnector () {
        initialize();
        //Connect to the database
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url+database,username,password);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Could not connect to database");
            System.exit(1);
        }
    }
    
    public final void initialize ()
    {
        setDriver       ("com.mysql.jdbc.Driver")        ;
        setUrl          ("jdbc:mysql://localhost:3306/") ;
        setDatabase     ("peertopeer")                   ;
        setUsername     ("root")                         ;
        setPassword     ("1234")                         ;
        setRng          (new Random ())                  ;
    }
    
    SongInfo getSong (SongInfo arg) throws PeerToPeerException {
        SongInfo rVal = null;
        ResultSet rs;
       
        try {   
            //stmt.executeQuery(String.format("select * from songs where hash =", arg.hash));
            PreparedStatement ps = getConn().prepareStatement("select * from songs where hash = ?;");
            ps.setString(1, arg.getHash());
            rs = ps.executeQuery();
            if (!rs.next())
                throw new PeerToPeerException("The song does not exist.");
            else
            {
                rVal = new SongInfo();
                rVal.setName(rs.getString("name"));
                rVal.setArtist(rs.getString("artist"));
                rVal.setAlbum(rs.getString("album"));
                rVal.setYear(rs.getInt("year"));
                rVal.setGenre(rs.getString("genre"));
                rVal.setHash(arg.getHash());
                return rVal;
            }
        } catch (SQLException ex) {
            throw new PeerToPeerException("Unable to connect to database");
        }
        
                    
    }

    void insertSong(SongInfo arg) throws PeerToPeerException {
        try {
            PreparedStatement insertStatement = getConn().prepareStatement("insert into peertopeer.songs values (?, ?, ?, ?, ?, ?);");
            PreparedStatement selectSongStatement = getConn().prepareStatement("select * from songs where hash = ?;");
            selectSongStatement.setString(1, arg.getHash());
            if (selectSongStatement.executeQuery().next()) {
                String update = "update songs\nset ";
                if (!"".equals(arg.name))
                    update += " name = '" + arg.getName() + "',";
                if (!"".equals(arg.artist))
                    update += " artist = '" + arg.getArtist() + "',";
                if (!"".equals(arg.album))
                    update += " album = '" + arg.getAlbum() + "',";
                if (0 != arg.getYear())
                    update += " year = '" + arg.getYear() + "',";
                if (!"".equals(arg.genre))
                    update += " genre = '" + arg.getGenre() + "',";
                update = update.substring(0, update.lastIndexOf(','));
                update += " where hash = ? ;";
                insertStatement = getConn().prepareStatement(update);
                insertStatement.setString(1, arg.getHash());
                insertStatement.executeUpdate();
            }
            else
                insertStatement.setString(1, arg.getName());
                insertStatement.setString(2, arg.getArtist());
                insertStatement.setString(3, arg.getAlbum());
                insertStatement.setInt(4, arg.getYear());
                insertStatement.setString(5, arg.getGenre());
                insertStatement.setString(6, arg.getHash());
                insertStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new PeerToPeerException("Could not connect to database");
        }
        
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the d
     */
    public Driver getD() {
        return d;
    }

    /**
     * @param d the d to set
     */
    public void setD(Driver d) {
        this.d = d;
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
     * @return the stmt
     */
    public Statement getStmt() {
        return stmt;
    }

    /**
     * @param stmt the stmt to set
     */
    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    /**
     * @return the getSong
     */
    public PreparedStatement getGetSong() {
        return getSong;
    }

    /**
     * @param getSong the getSong to set
     */
    public void setGetSong(PreparedStatement getSong) {
        this.getSong = getSong;
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
   
    
    
}


