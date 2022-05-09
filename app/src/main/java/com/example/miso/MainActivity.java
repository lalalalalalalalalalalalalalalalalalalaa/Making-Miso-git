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

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //讀取主畫面的fragment
        replaceFragment(new main());
        //更新為上次設定的語言
        setLanguage();

        navigation = findViewById(R.id.navigation);
        navigation.getMenu().clear();
        navigation.inflateMenu(R.menu.menu_resource);
        navigation.setOnItemSelectedListener(navigationListener);

        setting = findViewById(R.id.setting);
        setting.setText(R.string.setting);
        //設定"設定鍵"的傾聽
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //設定Dialog，讀取自訂的設定畫面、設定畫面的按鍵
                dlg_settings = new Dialog(MainActivity.this);
                dlg_settings.setCancelable(false);
                dlg_settings.setContentView(R.layout.setting);
                dlg_settings.setTitle(getString(R.string.setting));
                dlg_settings.show();

                Button dlg_btn_back = dlg_settings.findViewById(R.id.dlg_btn_back);
                Button dlg_btn_twch = dlg_settings.findViewById(R.id.dlg_btn_twch);
                Button dlg_btn_chch = dlg_settings.findViewById(R.id.dlg_btn_chch);
                Button dlg_btn_eng = dlg_settings.findViewById(R.id.dlg_btn_eng);

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

    /*//取得相機使用權限
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            openCamera();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
            }
        }
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();

            // 核心就是这一行代码
            Uri fileUri = FileProvider.getUriForFile(this, "com.siyee.android7.fileprovider", file);

            List<ResolveInfo> resInfoList = getPackageManager()
                    .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
        //startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            try {
                Uri uri = data.getData(MediaStore.EXTRA_OUTPUT);
                Bitmap image = (Bitmap)data.getExtras().get("data");
                Bundle sentimage = new Bundle();
                sentimage.putParcelable("sentimage", image);
                capture capture = new capture();
                capture.setArguments(sentimage);
                replaceFragment(capture);
            }catch (Exception e){
            }
        }
    }*/

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


}