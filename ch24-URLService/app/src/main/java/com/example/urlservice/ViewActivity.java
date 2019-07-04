package com.example.urlservice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        File saveDir = getFilesDir();

        // the directory exists, proceed
        if (saveDir.exists()) {
            // assign the files of the directory to variable File called dir
            File dir = new File(saveDir, "URLService");
            // variable dir = saveDir
            dir = saveDir;

            // if dir exists (if there is any values in this variable), proceed
            if (dir.exists()) {
                // store all the values within dir to an array of strings
                String[] files = dir.list();
                // set up the drop down list that displays the web pages to select and display
                ArrayAdapter<String> dataAdapter =
                        new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, files);
                dataAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                spinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            // method for the web page selected from the drop down list
                            // this method displays the selected web page in the WebView
                    @Override
                    public void onItemSelected(AdapterView<?>
                                adapterView, View view, int pos, long id) {
                        //open file
                        Object itemAtPosition = adapterView
                                .getItemAtPosition(pos);
                        File file = new File(getFilesDir(),
                                itemAtPosition.toString());
                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;
                        try {
                            fileReader = new FileReader(file);
                            bufferedReader =
                                    new BufferedReader(fileReader);
                            StringBuilder sb = new StringBuilder();
                            String line = bufferedReader.readLine();
                            while (line != null) {
                                sb.append(line);
                                line = bufferedReader.readLine();
                            }
                            WebView webView = (WebView)
                                    findViewById(R.id.webview);
                            webView.loadData(sb.toString(),
                                    "text/html", "utf-8");
                        } catch (FileNotFoundException e) {
                        } catch (IOException e) {
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?>
                            adapterView) {
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if the clear button is selected, proceed
        if (id == R.id.action_clear) {
            // execute the AsyncTask to perform the deletion
            new ClearAsyncTasks().execute();
            // finish the activity
            finish();
            return true;
        }

            return super.onOptionsItemSelected(item);
    }
    public class ClearAsyncTasks extends AsyncTask<Void, Void, Void> {
        // initialize variables to be used in the Async Task
        Notification notification;
        int notificationId = 1002;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPreExecute() {

            // this builds the notification message
            notification = new Notification.Builder(getApplicationContext())
                    // title of the notification
                    .setContentTitle("Deletion Complete")
                    // message the notification displays under the title
                    .setContentText(
                            "Deleted all the web pages")
                    .setSmallIcon(android.R.drawable.star_on)
                    // set the Auto Cancel option to true
                    // when the notification is clicked, the notification closes
                    .setAutoCancel(true)
                    // creates an empty intent in the notification to Auto Cancel when the
                    // notification is tapped
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0))
                    // build notification
                    .build();
        }


        @Override
        protected Void doInBackground(Void... params) {

            // gets the files in the local directory and stores them in a File variable
            File dr = getApplicationContext().getFilesDir();
            // if dr is a directory, proceed
            if(dr.isDirectory()){
                // store the files in the directory into an array of strings
                String[] files = dr.list();
                // iterate through the array
                for(int i=0;i<files.length;i++){
                    // delete the files
                    new File(dr, files[i]).delete();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // display the notification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(
                            NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);

        }
    }
}
