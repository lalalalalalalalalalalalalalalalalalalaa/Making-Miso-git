package com.example.miso;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class main extends Fragment {

    private static final String TAG = "tag";

    public main() {}


    View view;
    SQLiteDatabase db;
    TextView countday;

    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8:00")); //取得時間

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        Button complete = view.findViewById(R.id.complete);
        Button start = view.findViewById(R.id.start);
        countday = view.findViewById(R.id.countday);
        String a = "";
        int days = 0;
        //開啟資料庫，查詢是否有開始的時間，若是查詢結果為1999/12/31代表尚未開始製作味增
        db = getActivity().openOrCreateDatabase("Miso.db", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT _id, date FROM Date", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                a = cursor.getString(1);
                days = (int)findTime(a);
                if( a.equals("1999/12/31") ){
                    start.setVisibility(View.VISIBLE);
                    complete.setVisibility(View.INVISIBLE);
                    countday.setText(getString(R.string.before_start));
                }else {
                    start.setVisibility(View.INVISIBLE);
                    complete.setVisibility(View.VISIBLE);
                    String senttext =  getString(R.string.starting1) + " " + days + " " + getString(R.string.starting2);
                    countday.setText(senttext);
                }
            }
        }
        //設定完成，將資料庫的時間重製為1999/12/31，並且關閉伺服器
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:建立確認
                String restart = "1999/12/31";
                String str = "UPDATE Date SET date='" + restart + "' WHERE _id=" + 1;
                db.execSQL(str);
                start.setVisibility(View.VISIBLE);
                complete.setVisibility(View.INVISIBLE);
                countday.setText(getString(R.string.before_start));
                cancel_alarm(getContext(),cal);

                ComponentName receiver = new ComponentName(getContext(), MyReceiver.class);
                PackageManager pm = getContext().getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

            }
        });
        //設定開始，並將當下時間記錄到資料庫，並且開啟伺服器
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateformat = "yyyy/MM/dd";
                SimpleDateFormat df = new SimpleDateFormat(dateformat);
                Date now = new Date();
                String Insertstartdate;
                Insertstartdate = df.format(now.getTime());
                String str = "UPDATE Date SET date='" + Insertstartdate + "' WHERE _id=" + 1;
                db.execSQL(str);
                start.setVisibility(View.INVISIBLE);
                complete.setVisibility(View.VISIBLE);
                countday.setText(getString(R.string.btn_starting));

                cal.add(Calendar.MONTH, 1);    //加一個月
                cal.set(Calendar.SECOND, 0);    //設定秒數為0
                add_alarm(getContext(), cal);        //註冊鬧鐘

                ComponentName receiver = new ComponentName(getContext(), MyReceiver.class);
                PackageManager pm = getContext().getPackageManager();
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

            }
        });

        return view;
    }

    //計算天數的方法
    private long findTime(String a){
        long temp = 0;
        try{
            SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");//定義日期時間格式，一定要進行ParseException的例外處理
            Date f = sim.parse(a);
            long firstmeet = f.getTime();//取得時間的unix時間
            Date now = new Date();//取得目前即時的時間
            long nowtime = now.getTime();//取得時間的unix時間
            temp = (nowtime-firstmeet)/(1000*60*60*24);
        }catch(ParseException e){
        }
        return temp;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

    public static void add_alarm(Context context, Calendar cal) {
        Log.d(TAG, "alarm add time: " + String.valueOf(cal.get(Calendar.MONTH)) + "." + String.valueOf(cal.get(Calendar.DATE)) + " " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

        Intent intent = new Intent(context, AlarmManager.class);
        // 以日期字串組出不同的 category 以添加多個鬧鐘
        intent.addCategory("ID." + String.valueOf(cal.get(Calendar.MONTH)) + "." + String.valueOf(cal.get(Calendar.DATE)) + "-" + String.valueOf((cal.get(Calendar.HOUR_OF_DAY) )) + "." + String.valueOf(cal.get(Calendar.MINUTE)) + "." + String.valueOf(cal.get(Calendar.SECOND)));
        String AlarmTimeTag = "Alarmtime " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cal.get(Calendar.MINUTE)) + ":" + String.valueOf(cal.get(Calendar.SECOND));

        intent.putExtra("title", "activity_app");
        intent.putExtra("time", AlarmTimeTag);

        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        android.app.AlarmManager am = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);       //註冊鬧鐘
    }

    /***    取消(與系統註冊的)鬧鐘    ***/
    private static void cancel_alarm(Context context, Calendar cal) {
        Log.d(TAG, "alarm cancel time: " + String.valueOf(cal.get(Calendar.MONTH)) + "." + String.valueOf(cal.get(Calendar.DATE)) + " " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

        Intent intent = new Intent(context, AlarmManager.class);
        // 以日期字串組出不同的 category 以添加多個鬧鐘
        intent.addCategory("ID." + String.valueOf(cal.get(Calendar.MONTH)) + "." + String.valueOf(cal.get(Calendar.DATE)) + "-" + String.valueOf((cal.get(Calendar.HOUR_OF_DAY) )) + "." + String.valueOf(cal.get(Calendar.MINUTE)) + "." + String.valueOf(cal.get(Calendar.SECOND)));
        String AlarmTimeTag = "Alarmtime " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cal.get(Calendar.MINUTE)) + ":" + String.valueOf(cal.get(Calendar.SECOND));

        intent.putExtra("title", "activity_app");
        intent.putExtra("time", AlarmTimeTag);

        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);    //取消鬧鐘，只差在這裡
    }
}