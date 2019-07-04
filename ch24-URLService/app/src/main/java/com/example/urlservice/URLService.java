package com.example.urlservice;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;


public class URLService extends IntentService {
    public URLService() {
        super("URLService");
    }
    private static final String DOWNLOAD_NOTIFICATION_ACTION
            = "download_notification";
    int notificationId = 1002;

    // this method extracts the web pages from the intent, gets the file location (directory)
    // to save the web pages in, and stores the URLs in an array of strings.
    // the file directory (to save web pages in) and the array of URLs is then passed
    // to the fetchPagesAndSave method
    // this method also checks to see if there are files in the directory,
    // if they are, send the broadcast and notification. If they are not, do nothing.
    @Override
    protected void onHandleIntent(Intent intent) {
        String urls = intent.getStringExtra("urls");
        if (urls == null) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(urls);
        int tokenCount = tokenizer.countTokens();
        int index = 0;
        String[] targets = new String[tokenCount];
        while (tokenizer.hasMoreTokens()) {
            targets[index++] = tokenizer.nextToken();
        }
        File saveDir = getFilesDir();
        fetchPagesAndSave(saveDir, targets);
        // get the file directory containing the web pages
        File dr = getApplicationContext().getFilesDir();
        // store the amount of files in that directory into a variable (integer)
        int fileCount = dr.list().length;
        // if the the files in the directory are greater than 0, proceed
        if(fileCount > 0){
            // call to send the broadcast
            sendBroadcast();
            // call to send the notification
            sendNotification();
        }
    }

    // this method takes the file directory (to save in) and the array of URL strings
    // and saves the web page html files to the file directory location
    private void fetchPagesAndSave(File saveDir, String[] targets) {
        for (String target : targets) {
            URL url = null;
            try {
                url = new URL(target);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String fileName = target.replaceAll("/", "-")
                    .replaceAll(":", "-");

            File file = new File(saveDir, fileName);
            PrintWriter writer = null;
            BufferedReader reader = null;
            try {
                writer = new PrintWriter(file);
                reader = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                }
            } catch (Exception e) {
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                    }
                }
            }


        }
    }
    public void sendBroadcast(){
        // a user-defined intent action called download_complete and the corresponding Intent
        Intent downloadIntent = new Intent("download_complete");

        // send the broadcast to the MainActivity
        sendBroadcast(downloadIntent);

    }
    //this method builds and sends the notification
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void sendNotification(){

        if(MainActivity.notify.isChecked()) {
            // this builds the notification message
            Notification notification = new Notification.Builder(this)
                    // title of the notification
                    .setContentTitle("Download Complete")
                    // message the notification displays under the title
                    .setContentText(
                            "Downloaded all the web page URLs")
                    // notification icon
                    .setSmallIcon(android.R.drawable.star_on)
                    // set auto cancel to true
                    // closes the notification when clicked
                    .setAutoCancel(true)
                    // when the notification is clicked, go to the ViewActivity
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, ViewActivity.class), 0))
                    // build the notification
                    .build();


            // display the notification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(
                            NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);



        }

    }
}