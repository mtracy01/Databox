package com.example.matthew.databox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static Button upload_b, download_b;
    private static List<String> files = new ArrayList<String>();
    private static ListView list;
    public static long selectedItem=-1;
    private static String username="";
    private Context context=this;
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
        list= (ListView)findViewById(R.id.listView);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        upload_b = (Button) findViewById(R.id.upload_b);
        download_b = (Button) findViewById(R.id.download_b);
        this.updateFiles();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String uname="temp";
                Client client = new Client(username);
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
               /* if(view.getDrawingCacheBackgroundColor()!=0){
                    list.setItemChecked(position,true);
                    selectedItem=-1;

                    view.setBackgroundColor(0);//getResources().getColor(R.color.common_signin_btn_default_background));
                }
                else {
                    for(int i=0;i<files.size();i++){
                        list.setItemChecked(i,false);
                        View x = (View)list.getItemAtPosition(position);
                        x.setBackgroundColor(getResources().getColor(R.color.default_color));

                    }
                    list.setItemChecked(position, false);

                    selectedItem=position;
                    view.setBackgroundColor(getResources().getColor(R.color.pressed_color));//getResources().getColor(R.color.background_material_dark));

                }*/
                //list.setSelected(true);
                //view.setBackgroundColor(Color.BLUE);
                //conversationAdapter.notifyDataSetChanged();
                //list.setSelection(position);
            }
        });
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
