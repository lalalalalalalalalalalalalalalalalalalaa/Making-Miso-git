package com.example.miso;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmManager extends BroadcastReceiver {

    SQLiteDatabase db;
    NotificationManager notificationManager;
    final int flags = PendingIntent.FLAG_CANCEL_CURRENT;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences("times", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int count = preferences.getInt("times", 0);

        db = context.openOrCreateDatabase("Miso.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT _id, date FROM Date", null);

        String gettime = "";
        int days = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                gettime = cursor.getString(1);
                days = (int)findTime(gettime);
            }
        }

        Notification.Builder builder = new Notification.Builder(context);
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent push =new Intent(context,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,push,flags);
        builder
                .setContentTitle(context.getString(R.string.notify_ttitle))
                .setContentText(context.getString(R.string.notify_1_content) + days + context.getString(R.string.notify_2_content))
                .setContentIntent(contentIntent)
                .setTicker("新訊息")
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_foreground))
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setPriority(Notification.PRIORITY_DEFAULT)//設定該通知優先順序
                .setCategory(Notification.CATEGORY_MESSAGE)//設定通知類別
                .setSmallIcon(R.drawable.home)
                .setFullScreenIntent(contentIntent, true)//將Notification變為懸掛式Notification
                .setAutoCancel(true);

        Notification notify = builder.build();

        count += 1;
        editor.putInt("times", count);

        if(count <= 6) {
            notificationManager.notify(0, notify);
        }else if(count > 6 && count%6 == 0) {
            notificationManager.notify(0, notify);
        }
        cursor.close();
        db.close();

    }


    private long findTime(String a){
        long temp = 0;
        try{
            SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");//定義日期時間格式，一定要進行ParseException的例外處理
            Date f = sim.parse(a);
            long firstmeet = f.getTime();
            Date now = new Date();
            long nowtime = now.getTime();
            temp = (nowtime-firstmeet)/(1000*60*60*24);
            //Toast.makeText(getContext(), "time= " + f, Toast.LENGTH_SHORT).show();
        }catch(ParseException e){
            //Toast.makeText(getContext(),"Something is wrong", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(),"date0:" + temp , Toast.LENGTH_SHORT).show();
        }
        return temp;
    }



}
