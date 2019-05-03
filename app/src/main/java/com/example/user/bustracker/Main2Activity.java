package com.example.user.bustracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity {
    public String global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    public void driver (View view){
        global = "driver";
        Intent intent = new Intent(this, Main6Activity.class);
        startActivity(intent);
    }
    public void pssngr (View view){
        global = "passanger";
        Intent intent = new Intent(this, Main3Activity.class);
        startActivity(intent);
    }
    public void info(View view){
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }
    public void exit(View view){
        finish();
        moveTaskToBack(true);
    }
}
