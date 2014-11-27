package com.example.matthew.databox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Server extends Activity{
    private ServerSocket serverSocket;
    Handler updateHandler;
    Thread serverThread = null;
    private TextView text;
    public static final int SERVERPORT = 6000;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textView2);
        updateHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());

    }
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch( IOException e) {
            e.printStackTrace();
        }
    }
    class ServerThread implements Runnable{
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try{
                    socket = serverSocket.accept();
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();
                } catch( IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    class CommunicationThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;
        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                    updateHandler.post(new updateUIThread( read ));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class updateUIThread implements Runnable{
        private String msg;
        public updateUIThread(String str){
            this.msg = str;
        }
        public void run() {
            text.setText(text.getText().toString()+ " " +msg);
        }

    }
 }
