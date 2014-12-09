package com.example.matthew.databox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.sql.*;

/**
 * Created by Cris on 12/8/2014.
 *
 * Databox - Server
 *
 * Server for Databox. It does stuff.
 */
public class DataboxServer {
    private static ServerSocket serverSocket;
    public static final int SERVERPORT = 6000;

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    private static ServerSocket ss = null;
    private static Socket socket = null;
    private static InputStreamReader isr = null;
    private static BufferedReader br = null;
    private static OutputStreamWriter osw = null;
    private static BufferedWriter bw = null;

    public static void main(String[] args) {

        try {
            ss = new ServerSocket(SERVERPORT);

            // Continually wait for connections
            while (true) {
                socket = ss.accept();
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                osw = new OutputStreamWriter(socket.getOutputStream());
                bw = new BufferedWriter(osw);

                String msg = br.readLine();

                executeRequest(msg);
            }
        }
        catch (IOException e) {}
        catch (Exception e) {}
    }

    private static void executeRequest(String msg) {
        if (msg.substring(0, 6).equals("USERID"))
            checkUserID(msg);
        if (msg.substring(0, 7).equals("ADDUSER"))
            addUser(msg);
    }

    private static void checkUserID(String msg){
        try {
            bw.write(SUCCESS, 0, SUCCESS.length());
        }
        catch (IOException e) {}
        catch (Exception e) {}
    }

    private static void addUser(String msg) {
        try {
            bw.write(SUCCESS, 0, SUCCESS.length());
        }
        catch (IOException e) {}
        catch (Exception e) {}
    }
}
