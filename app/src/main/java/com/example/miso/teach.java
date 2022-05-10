package com.example.miso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class teach extends Fragment {

    View view;

    Button button01, button02, button03, button04, button05, button06, button07, button08, btn_teach_introduce;
    String step1, step2, step3, step4, step5, step6, step7, step8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_teach, container,  false);

        findviewby();
        getstr();

        //設定味增介紹的切換按鍵
        btn_teach_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaceFragment(new misointroduce());

            }
        });
        //設定每個步驟要帶過去的資料，並傳到步驟的Fragment
        button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step1);
                Bundle edttext = new Bundle();
                edttext.putString("content", step1);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());

            }
        });

        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step2);
                Bundle edttext = new Bundle();
                edttext.putString("content", step2);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step3);
                Bundle edttext = new Bundle();
                edttext.putString("content", step3);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step4);
                Bundle edttext = new Bundle();
                edttext.putString("content", step4);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step5);
                Bundle edttext = new Bundle();
                edttext.putString("content", step5);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step6);
                Bundle edttext = new Bundle();
                edttext.putString("content", step6);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step7);
                Bundle edttext = new Bundle();
                edttext.putString("content", step7);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });

        button08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = getString(R.string.step8);
                Bundle edttext = new Bundle();
                edttext.putString("content", step8);
                edttext.putString("step", step);
                getParentFragmentManager().setFragmentResult("teachstep",edttext);
                replaceFragment(new teach_step());
            }
        });
        return view;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainpart,fragment);
        fragmentTransaction.commit();

    }

    private void findviewby(){
        button01 = view.findViewById(R.id.step1);
        button02 = view.findViewById(R.id.step2);
        button03 = view.findViewById(R.id.step3);
        button04 = view.findViewById(R.id.step4);
        button05 = view.findViewById(R.id.step5);
        button06 = view.findViewById(R.id.step6);
        button07 = view.findViewById(R.id.step7);
        button08 = view.findViewById(R.id.step8);
        btn_teach_introduce = view.findViewById(R.id.btn_teach_introduce);
    }

    private void getstr() {
        step1 = getString(R.string.step1_content);
        step2 = getString(R.string.step2_content);
        step3 = getString(R.string.step3_content);
        step4 = getString(R.string.step4_content);
        step5 = getString(R.string.step5_content);
        step6 = getString(R.string.step6_content);
        step7 = getString(R.string.step7_content);
        step8 = getString(R.string.step8_content);
    }

}