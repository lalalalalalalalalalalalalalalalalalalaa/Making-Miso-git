package com.example.miso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* 收到廣播後要做的事 */

            //建立通知發布鬧鐘
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8:00")); //取得時間
            cal.add(Calendar.MONTH, 1);    //加一個月
            cal.set(Calendar.SECOND, 0);    //設定秒數為0
            main.add_alarm(context, cal);        //註冊鬧鐘

        }
    }
}