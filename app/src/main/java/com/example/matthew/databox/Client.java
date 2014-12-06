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

    private String username = "";

    public Client(String username) {
        this.username = username;
    }

    public int checkUserID(String userID, String password) {
        Socket socket = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        return 0;
    }

    public void send(String msg) throws IOException {
        Socket socket = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            // Set up connection with server
            socket = new Socket(SERVER, SERVERPORT);
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);

            // Send the message to the server
            bw.write(msg, 0, msg.length());
            bw.flush();

            if (msg.substring(0, 8).equals("GETFILES")) {
                // If the message is requesting files read what the server returns
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);

                // Read the files the server writes back
                String file = br.readLine();
                while (file.length() != 0) {
                    MainActivity.addFile(file);
                    file = br.readLine();
                }

                // Tell MainActivity to update its TextView with the files
                MainActivity.updateFiles();
            }
        }
        finally {
            // do some cool stuff or something
            if (osw != null)
                osw.close();
            if (bw != null)
                bw.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
            if (socket != null)
                socket.close();
        }
    }

    public static int setUsername(String username, String password) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
