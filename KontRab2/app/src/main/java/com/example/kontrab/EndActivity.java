package com.example.kontrab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText(loadResults());
        Button button = (Button) findViewById(R.id.endBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    // Метод для чтения данных из файла
    private String loadResults() {
        try (FileOutputStream fos = openFileOutput("results.txt", MODE_APPEND)) {
            // Оставляем пустым, просто для создания файла, если его нет
        } catch (IOException e) {
            Log.e("EndActivity", "Ошибка при создании файла", e);
        }

        String lastLine = null;
        try (FileInputStream fis = openFileInput("results.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) { // Игнорируем пустые строки
                    lastLine = line; // Обновляем значение на текущую непустую строку
                }
            }
        } catch (IOException e) {
            Log.e("EndActivity", "Ошибка при чтении из файла", e);
            return "Нет данных для отображения........";
        }

        return lastLine;
    }
}
