package com.example.matthew.databox;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Cris on 12/6/2014.
 */
public class Client {
    public static final int SERVERPORT = 6000;
    public final static String SERVER = "PUT THE SERVER ADDRESS HERE";

    public static void send(String msg) throws IOException {
        Socket socket = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            // Set up connection with server
            socket = new Socket(SERVER, SERVERPORT);
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);

            // Send the message to the server
            bw.write(msg, 0, msg.length());
            bw.flush();
        }
        finally {
            // do some cool stuff or something
            if (osw != null)
                osw.close();
            if (bw != null)
                bw.close();
            if (socket != null)
                socket.close();
        }
    }
}
