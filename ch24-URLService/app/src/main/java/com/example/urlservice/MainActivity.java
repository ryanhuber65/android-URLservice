package com.example.urlservice;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.Activity;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    // initialize variables
    private static final String DOWNLOAD_NOTIFICATION_ACTION
            = "download_complete";
    int notificationId = 1002;
    static CheckBox notify;
    boolean received = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // initialize the receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            // onReceive method if the broadcast was received
            @Override
            public void onReceive(Context context, Intent intent) {
                // if the action in the intent being passed from the broadcast is
                // "download_complete" (DOWNLOAD_NOTIFICATION_ACTION), proceed
                if(intent.getAction().equals(DOWNLOAD_NOTIFICATION_ACTION)){
                    // if the broadcast was received display this message:
                    Toast.makeText(context, "Web Pages Have Been Fetched", Toast.LENGTH_SHORT).show();
                    // set the boolean variable "received" to true
                    received = true;
                }

            }
        };

        // add the download notification action and register the receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(DOWNLOAD_NOTIFICATION_ACTION);
        this.registerReceiver(receiver, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view) {
            // get the file directory containing the web pages
            File dr = getApplicationContext().getFilesDir();
            // store the amount of files in that directory into a variable (integer)
            int fileCount = dr.list().length;
            // if the broadcast was received and the files
            // in the directory are greater than 0, proceed
            if (received == true && fileCount > 0 ){
                // create the intent
                Intent intent = new Intent(this, ViewActivity.class);
                // start the second activity
                startActivity(intent);
                 }
                 // if a broadcast hasn't been received and there's no files in the directory
            else{
                    // display the following message
                    Toast.makeText(this, "No Files Available to View", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchWebPages(View view) {
        // initialize variables and map to their appropriate checkboxes and textfields
        EditText editText = (EditText) findViewById(R.id.urlsEditText);
        notify = (CheckBox) findViewById(R.id.notifyCheckBox);

        //create an intent
        Intent intent = new Intent(this, URLService.class);
        // add the urls in the EditText to the intent
        intent.putExtra("urls", editText.getText().toString());

        // start the service
        startService(intent);
    }

}