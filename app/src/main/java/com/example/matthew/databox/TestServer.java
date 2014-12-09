package com.example.matthew.databox;

/**
 * Created by Ji on 12/8/14.
 */
//package com.example.matthew.databox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**import android.app.Activity;
 import android.os.Bundle;**/
import java.sql.*;
public class TestServer {
    private static ServerSocket serverSocket;
    Thread serverThread = null;
    public static final int SERVERPORT = 6000;


    protected void onStop() {
        // super.onStop();
        try {
            serverSocket.close();
        } catch( IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        try{
            ServerSocket s = new ServerSocket(SERVERPORT);
            while(true){
                Socket incoming = s.accept();
                System.out.println("Here\n");
                Runnable r = new ServerThread(incoming);
                Thread t = new Thread(r);
                t.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class ServerThread implements Runnable{
    private Socket incoming;
    private BufferedReader input;
    private BufferedWriter output;
    String drivers = null;
    public ServerThread(Socket oneSocket){
        incoming = oneSocket;
        try{
            this.input = new BufferedReader(new InputStreamReader(this.incoming.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(this.incoming.getOutputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("database.properties"));
        drivers = props.getProperty("jdbc.drivers");
        if(drivers != null){
            System.setProperty("jdbc.drivers", drivers);
        }
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        return DriverManager.getConnection(url,username,password);
    }
    String executeRequest(String msg) {
        String ret;
        if(msg.substring(0, 6).equals("USERID")){
            ret = checkUserID(msg);
        }
        else if(msg.substring(0, 7).equals("GETFILES")){
            ret = addUser(msg);
        }
        if (msg.substring(0, 8).equals("GETFILES")) {
            ret = getFiles(msg);
        }
        // TODO add other messages
        else {
            ret = "FAILURE";
        }

        return ret;
    }
    String checkUserID(String read){
        // TODO have database check for user
        return "SUCCESS";
    }
    String addUser(String read){
        // TODO have database add user
        return "SUCCESS";
    }
    String getFiles(String read){
        // TODO have database retrieve user's files
        return "File 1\nFile 2\nFile 3\n";
    }
    public void run(){
        try
        {
            try
            {
                String read = input.readLine();
                String returnString = executeRequest(read);
                output.write(returnString, 0, returnString.length());

                output.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                incoming.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

