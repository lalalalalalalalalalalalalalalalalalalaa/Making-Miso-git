package com.example.miso;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Formula extends Fragment {

    private SQLiteDatabase db;
    View view;

    Button button01, button02, button03, button04, button05, button06, button07, button08;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_formula, container, false);

        findviewby();

        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 1;
                btnclick(ID);
            }
        });

        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 2;
                btnclick(ID);
            }
        });

        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 3;
                btnclick(ID);
            }
        });

        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 4;
                btnclick(ID);

            }
        });

        button05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 5;
                btnclick(ID);
            }
        });

        button06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 6;
                btnclick(ID);
            }
        });

        button07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 7;
                btnclick(ID);
            }
        });

        button08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = 8;
                btnclick(ID);
            }
        });
        return  view;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainpart,fragment);
        fragmentTransaction.commit();

    }

    private void btnclick(int ID){

        db = getActivity().openOrCreateDatabase("Miso.db", Context.MODE_PRIVATE, null);
        String Ingredient = "";
        String Content = "";
        String FormulaName = "";

        Cursor cursor = db.rawQuery("SELECT _id, FormulaName, Ingredient, Content FROM Formula", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int searchid = cursor.getInt(0);
                if (ID == searchid) {
                    FormulaName = cursor.getString(1);
                    Ingredient = cursor.getString(2);
                    Content = cursor.getString(3);
                    break;
                }
            }
        }

        Bundle list = new Bundle();
        list.putInt("ID", ID);
        list.putString("FormulaName",FormulaName);
        list.putString("Ingredient", Ingredient );
        list.putString("Content", Content);
        getParentFragmentManager().setFragmentResult("Formula",list);
        replaceFragment(new Formula_list());

        cursor.close();
        db.close();
    }

    private void findviewby(){
        button01 = view.findViewById(R.id.btn_formula_01);
        button02 = view.findViewById(R.id.btn_formula_02);
        button03 = view.findViewById(R.id.btn_formula_03);
        button04 = view.findViewById(R.id.btn_formula_04);
        button05 = view.findViewById(R.id.btn_formula_05);
        button06 = view.findViewById(R.id.btn_formula_06);
        button07 = view.findViewById(R.id.btn_formula_07);
        button08 = view.findViewById(R.id.btn_formula_08);

    }


}

