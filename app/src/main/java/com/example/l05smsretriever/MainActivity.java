package com.example.l05smsretriever;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Todo: MainActivity is done by Sf
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FragmentFirst f1 = new FragmentFirst();
        ft.replace(R.id.frame1, f1);

        FragmentSecond f2 = new FragmentSecond();
        ft.replace(R.id.frame2, f2);

        ft.commit();
    }
}