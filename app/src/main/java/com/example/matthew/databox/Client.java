package com.example.matthew.databox;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by Cris on 12/6/2014.
 */
public class Client {
    private static final int SERVERPORT = 6000;
    private final static String SERVER = "PUT THE SERVER ADDRESS HERE";
    private final static int CHUNK = 4096;

    // Different messages to send to server :)
    private final static String USERID = "USERID";
    private final static String ADDUSER = "ADDUSER";
    private final static String GETFILES = "GETFILES";
    private final static String UPLOAD = "UPLOAD";
    private final static String DOWNLOAD = "DOWNLOAD";

    private String username = "";

    private Socket socket;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private InputStreamReader isr;
    private BufferedReader br;

    public Client(String username, String password) {
        this.username = username;
    }

    private int initSocket() {
        try {
            socket = new Socket(SERVER, SERVERPORT);
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);
            isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int checkUserID(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        // Send userID + password to server so it can check if that pair exists
        try {
            bw.write(USERID, 0, USERID.length());
            bw.write(" " + userID + " " + password, 0, userID.length() + password.length() + 2);
            bw.flush();

            // TODO wait for the server to do its thing?

            // TODO read back some stuff from server

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int addUser(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        try {
            bw.write(ADDUSER, 0, ADDUSER.length());
            bw.write(" " + userID + " " + password, 0, userID.length() + password.length() + 2);
            bw.flush();

            // TODO wait for the server to do its thing?

            // TODO read back some stuff from server
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    public int send(String msg) {
        if (initSocket() == 1)
            return 1;

        // Send our msg to the server
        try {
            bw.write(msg, 0, msg.length());
            bw.flush();

            if (msg.substring(0, 8).equals("GETFILES")) {
                // If the message is requesting files read what the server returns

                // Read the files the server writes back
                String file = br.readLine();
                while (file.length() != 0) {
                    MainActivity.addFile(file);
                    file = br.readLine();
                }

                // Tell MainActivity to update its TextView with the files
                MainActivity.updateFiles();
            }

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String getUsername() {
        return username;
    }
}
