package com.example.matthew.databox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.protocol.RequestContent;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static Button upload_b, download_b;
    private static List<String> files = new ArrayList<String>();
    private static ListView list;
    public static long selectedItem=-1;
    private static String username="";
    private Context context=this;
    private static Client client;
    private String chosenFile;
    private String fileList[];
    private File mPath;

    //Initialization, One for testing, the other for default application authentication system
    public MainActivity(String uname){
        username=uname;
    }
    public MainActivity(){
        username="littleJi";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Databox");
        client = new Client(username);
        list= (ListView)findViewById(R.id.listView);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        upload_b = (Button) findViewById(R.id.upload_b);
        download_b = (Button) findViewById(R.id.download_b);
        this.updateFiles();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String uname="temp";
                //Client client = new Client(username);
                int success=client.download(files.get(position));
                if(success!=0){
                    AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Whoops!");
                    alertDialogBuilder.setMessage("Cannot download file.  File either does not exist... or we messed up :/.\nSowwy!!!");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok...",new DialogInterface.OnClickListener(){ public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    updateFiles();
                    //print an error message on the screen
                }

            }
        });
        upload_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create Dialogue for the user to select a file from android
                /*Intent medIntent = new Intent(Intent.ACTION_GET_CONTENT);
                medIntent.setType("all"); //
                //startActivityForResult(medIntent, );
                */
                //String[] fileList;
                mPath= Environment.getExternalStorageDirectory();

                try{
                    mPath.mkdirs();
                }
                catch(SecurityException e){
                    //error message needs to be here
                }
                if(mPath.exists()){
                    fileList=mPath.list();

                }
                else{
                    fileList=new String[0];
                }

                //create dialog for listing files
                Dialog dialog=null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose a file");
                builder.setNegativeButton("Go Up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goUp();
                    }
                });
                builder.setPositiveButton("Cancel",new DialogInterface.OnClickListener(){ public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
                if(fileList==null){
                    dialog=builder.create();
                }
                else{
                    builder.setItems(fileList,new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            chosenFile=fileList[which];
                            //Conditions for dealing with different file types defined in recursive function
                            recursiveSelect(chosenFile);
                        }
                    });
                }
                dialog=builder.show();

            }
        });
    }
    private void goUp() {
        //create new dialog of upper directory if possible
        if ((Environment.getExternalStorageDirectory().getName()).length()< mPath.getName().length()) {
            mPath = mPath.getParentFile();
            displayNewFileList();
        }
    }
    private void displayNewFileList(){
        Dialog dialog = null;
        fileList=null;
        try{
            mPath.mkdirs();
        }
        catch(SecurityException e){
            //Something went wrong
            return;
        }
        if(mPath.exists()){
            fileList=mPath.list();

        }
        else{
            fileList=new String[0];
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a file");
        builder.setNegativeButton("Go Up", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goUp();
            }
        });
        builder.setPositiveButton("Cancel",new DialogInterface.OnClickListener(){ public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
        if(fileList==null){
            dialog=builder.create();
        }
        else{
            builder.setItems(fileList,new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    chosenFile=fileList[which];
                    //Conditions for dealing with different file types defined in recursive function
                    recursiveSelect(chosenFile);
                }
            });
        }
        dialog=builder.show();
    }
    private void recursiveSelect(String f){
        String fil=mPath.getAbsolutePath();
        fil.concat(f);
        File fi = new File(fil);
        if(fi.isDirectory()){
            //create new dialog for the subdirectory
            fileList=null;
            Dialog dialog=null;
            mPath=null;
            mPath=fi;
            try{
                mPath.mkdirs();
            }
            catch(SecurityException e){
                //Something went wrong
                //return;
            }
            if(mPath.exists()){
                fileList=mPath.list();

            }
            else{
                fileList=new String[0];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose a file");
            builder.setNegativeButton("Go Up", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    goUp();
                }
            });
            builder.setPositiveButton("Cancel",new DialogInterface.OnClickListener(){ public void onClick(DialogInterface dialog, int id) {dialog.cancel();}});
            if(fileList==null){
                //dialog=builder.create();
                return;
            }
            else{
                builder.setItems(fileList,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        chosenFile=fileList[which];
                        //Conditions for dealing with different file types defined in recursive function
                        recursiveSelect(chosenFile);
                    }
                });
            }
            dialog=builder.show();

        }
        /*else if(f.equals("Go Up")){
            //if name is our option to go up to parent directory
        }*/
        else{
            //We have the file that we want to upload to the server
            String path= fi.getAbsolutePath();
            client.upload(path);

        }
    }

    public void addFile(String file) {
        files.add(file);
    }

    public void updateFiles() {
        // TODO display all of the files in the files ArrayList
        files.add("Test1");
        files.add("Test2");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,files);
        list.setAdapter(adapter);

    }

   /* public void sendMessage(View view)
    {
        Intent intent = new Intent(FromActivity.this, ToActivity.class);
        startActivity(intent);
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
