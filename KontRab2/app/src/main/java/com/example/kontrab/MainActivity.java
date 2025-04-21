package com.example.kontrab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private int countTry = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText nameText = (EditText) findViewById(R.id.nameText);
        EditText surnameText = (EditText) findViewById(R.id.surnameText);
        EditText timeText = (EditText) findViewById(R.id.timeText);
        Button button = (Button) findViewById(R.id.startBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countTry++;
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("nameStudent", nameText.getText().toString());
                intent.putExtra("surnameStudent", surnameText.getText().toString());
                intent.putExtra("timeStart", timeText.getText().toString());
                intent.putExtra("countTry", countTry);
                startActivity(intent);
            }
        });
    }



}