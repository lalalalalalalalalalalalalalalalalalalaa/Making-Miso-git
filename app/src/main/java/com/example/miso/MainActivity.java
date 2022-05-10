package com.example.miso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String DB_FILE = "Miso.db", DB_TABLE = "Formula", DB_TABLE2 = "Date";
    private BottomNavigationView navigation;
    private SQLiteDatabase db;
    private Button setting;
    private Dialog dlg_settings;

    Button dlg_btn_back, dlg_btn_twch, dlg_btn_chch, dlg_btn_eng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //讀取主畫面的fragment
        replaceFragment(new main());
        //更新為上次設定的語言
        setLanguage();
        initdb();

        navigation = findViewById(R.id.navigation);
        setting = findViewById(R.id.setting);

        navigation.getMenu().clear();
        navigation.inflateMenu(R.menu.menu_resource);
        navigation.setOnItemSelectedListener(navigationListener);


        setting.setText(R.string.setting);
        //設定"設定鍵"的傾聽
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //設定Dialog，讀取自訂的設定畫面、設定畫面的按鍵
                dlg_set();
                dlg_btn_findviewby();

                dlg_btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg_settings.dismiss();
                    }
                });
                //設定語言按鍵的傾聽
                dlg_btn_eng.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences preferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("Language", 1);
                        editor.apply();
                        dlg_settings.dismiss();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dlg_btn_twch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences preferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("Language", 2);
                        editor.apply();
                        dlg_settings.dismiss();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dlg_btn_chch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences preferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("Language", 0);
                        editor.apply();
                        dlg_settings.dismiss();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

    }
    //設定主選單按鍵傾聽
    private NavigationBarView.OnItemSelectedListener navigationListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.formula) {
                setting.setVisibility(View.INVISIBLE);
                replaceFragment(new Formula());
                return true;
            }
            else if (id == R.id.main) {
                setting.setVisibility(View.VISIBLE);
                replaceFragment(new main());
                return true;
            }
            else if (id == R.id.teach) {
                setting.setVisibility(View.INVISIBLE);
                replaceFragment(new teach());
                return true;
            }
            else if (id == R.id.photo) {
                setting.setVisibility(View.INVISIBLE);
                //askCameraPermissions();
                replaceFragment(new capture());
                return true;
            }
            return false;
        }
    };
    //切換fragment的方法
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainpart,fragment);
        fragmentTransaction.commit();

    }

    private void dlg_set(){
        dlg_settings = new Dialog(MainActivity.this);
        dlg_settings.setCancelable(false);
        dlg_settings.setContentView(R.layout.setting);
        dlg_settings.setTitle(getString(R.string.setting));
        dlg_settings.show();
    }

    private void dlg_btn_findviewby(){
        dlg_btn_back = dlg_settings.findViewById(R.id.dlg_btn_back);
        dlg_btn_twch = dlg_settings.findViewById(R.id.dlg_btn_twch);
        dlg_btn_chch = dlg_settings.findViewById(R.id.dlg_btn_chch);
        dlg_btn_eng = dlg_settings.findViewById(R.id.dlg_btn_eng);
    }

    //切換語言的方法
    private void setLanguage() {

        SharedPreferences preferences = getSharedPreferences("Language", Context.MODE_PRIVATE);
        int language = preferences.getInt("Language", 0);

        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        switch (language){
            case 0:
                configuration.setLocale(Locale.CHINA);
                break;
            case 1:
                configuration.setLocale(Locale.ENGLISH);
                break;
            case 2:
                configuration.setLocale(Locale.CHINESE);
            default:
                break;
        }
        resources.updateConfiguration(configuration,displayMetrics);
    }

    private void initdb(){
        //資料庫建立、資料表建立
        db = openOrCreateDatabase(DB_FILE, MODE_PRIVATE, null);
        try {
            db.execSQL(" CREATE TABLE " + DB_TABLE +
                    "(_id INTEGER PRIMARY KEY," + "FormulaName TEXT NOT NULL," + "Ingredient TEXT," + "Content TEXT);");
            db.execSQL(" CREATE TABLE " + DB_TABLE2 + "(_id INTEGER PRIMARY KEY," + "date TEXT NOT NULL);");
            for ( int k =0; k < 8; k++) {

                db.execSQL("INSERT INTO Formula (FormulaName ,Ingredient, Content) values ('請輸入配方名稱', '請輸入材料', '請輸入做法')");
            }
            db.execSQL("INSERT INTO Date (date) values ('1999/12/31')");
        }catch (Exception e){

        }

        db.close();
    }


}