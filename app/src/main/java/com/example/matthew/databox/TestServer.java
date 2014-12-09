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
    public ServerThread(Socket oneSocket){
        incoming = oneSocket;
        try{
            this.input = new BufferedReader(new InputStreamReader(this.incoming.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(this.incoming.getOutputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    String  checkUserID(String read){
        String write = "FAILURE";

        if(read.equals("USERID")){
            return "SUCCESS";
        }
        if(read.equals("GETFILES")){
            return "SUCCESS";
        }
        return write;
    }
    public void run(){
        try
        {
            try
            {
                String read = input.readLine();
                String returnString = checkUserID(read);
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

