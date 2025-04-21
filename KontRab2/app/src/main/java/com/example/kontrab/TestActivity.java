package com.example.kontrab;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class Question{
    int countVar; //кол-во вопросов
    String nameQuestion; //имя вопроса
    ArrayList<String> questions = new ArrayList<>();
    String correctAnswer;
    Question(int countVar, String nameQuestion, ArrayList<String> questions, String correctAnswer){
        this.countVar = countVar;
        this.nameQuestion = nameQuestion;
        this.questions = questions;
        this.correctAnswer = correctAnswer;
    }
    Question(ArrayList<String> all){
        this.countVar = all.size()-1;
        this.nameQuestion = all.get(0);
        all.remove(0);
        Collections.shuffle(all);
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).startsWith("*")) {
                correctAnswer = all.get(i).substring(1); // убираем символ '*'
                all.set(i, correctAnswer);
            }
        }
        this.questions = all;
    }

}
public class TestActivity extends AppCompatActivity {
    private int currentQuestionIndex = 0;
    private ArrayList<ArrayList<String>> data = new ArrayList<>();
    private int countCorrect = 0;
    private RadioButton[] radioButtons;
    ArrayList<Question> questions = new ArrayList<>();
    private int timeLeft = 0;
    ProgressBar progressBar;

    private void
    saveResults(int attemptNumber, String surname, String name, int totalTimeSpent, int correctAnswers, int score) {
    //запись результатов в файл
        // Форматирование даты с часовым поясом Москвы
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        // Конвертация времени из секунд в формат "мин:сек"
        String timeFormatted = String.format(Locale.getDefault(), "%d мин %d сек", totalTimeSpent / 60, totalTimeSpent % 60);

        String data = String.format(Locale.getDefault(), "Попытка №%d, Дата: %s, Фамилия: %s, Имя: %s, Время: %s, Правильные ответы: %d, Оценка: %d\n",
                attemptNumber, date, surname, name, timeFormatted, correctAnswers, score);

        try (FileOutputStream fos = openFileOutput("results.txt", Context.MODE_APPEND);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            writer.write(data);
        } catch (IOException e) {
            Log.e("EndActivity", "Ошибка при записи в файл", e);
        }
        Intent intent = new Intent(TestActivity.this, EndActivity.class);
        startActivity(intent);
        TestActivity.this.finish(); // Завершаем активность
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String name = getIntent().getStringExtra("nameStudent");
        String surname = getIntent().getStringExtra("surnameStudent");
        String timeStartStr = getIntent().getStringExtra("timeStart");
        int countTry = getIntent().getIntExtra("countTry", 1);
        int timeStart = 1; // значение по умолчанию
        if (timeStartStr != null && !timeStartStr.isEmpty()) {
            try {
                timeStart = Integer.parseInt(timeStartStr);
            } catch (NumberFormatException e) {
                Log.e("TestActivity", "Некорректное значение времени: " + timeStartStr, e);
            }
        }
        progressBar = findViewById(R.id.progressBar);
        TextView fullName = findViewById(R.id.nameStudent);
        TextView nowTime = findViewById(R.id.nowTimeText);
        fullName.setText("Тест проходит " + name + " " + surname);
        // Загружаем вопросы
        loadQuestions();

        // Инициализируем элементы интерфейса
        TextView questionNameText = findViewById(R.id.questionNameText);
        radioButtons = new RadioButton[]{
                findViewById(R.id.radio1Btn),
                findViewById(R.id.radio2Btn),
                findViewById(R.id.radio3Btn),
                findViewById(R.id.radio4Btn)
        };
        int finalTimeStart = timeStart;
        CountDownTimer сountDownTimer = new CountDownTimer(60000 * finalTimeStart, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) ((int) 60 * finalTimeStart-millisUntilFinished/ 1000);
                nowTime.setText("Времени осталось: " + millisUntilFinished / 60000 + " мин "
                        + (millisUntilFinished / 1000 % 60) + " сек");
            }
            public void onFinish() {
                nowTime.setText("done!");
                Toast.makeText(TestActivity.this, "Время вышло!", Toast.LENGTH_SHORT).show();
                saveResults(1, surname, name, finalTimeStart*60, countCorrect,
                        (countCorrect >= 15 ? 5 : countCorrect >= 10 ? 4 : countCorrect >= 5 ? 3 : 2));
            }
        }.start();
        // Устанавливаем обработчик для каждого RadioButton
        for (RadioButton radioButton : radioButtons) {
            radioButton.setOnClickListener(v -> {
                // Проверка ответа пользователя
                RadioButton selectedRadio = (RadioButton) v;
                String selectedAnswer = selectedRadio.getText().toString();

                // Проверка правильности ответа

                String correctAnswer = data.get(currentQuestionIndex).get(0);
                if (selectedAnswer.equals(questions.get(currentQuestionIndex).correctAnswer)) {
                    countCorrect++;
                    Log.d("ANSWER", "Correct answer selected");
                } else {
                    Log.d("ANSWER", "Incorrect answer selected");
                }

                // Переход к следующему вопросу
                currentQuestionIndex++;
                if (currentQuestionIndex < data.size()) {
                    showQuestion(currentQuestionIndex); // Показать следующий вопрос
                } else {
                    // Если все вопросы пройдены
                    сountDownTimer.cancel();
                    Toast.makeText(this, "Тест завершён. Правильных ответов: " + countCorrect, Toast.LENGTH_SHORT).show();
                    saveResults(countTry, surname, name, timeLeft, countCorrect,
                            (countCorrect >= 15 ? 5 : countCorrect >= 10 ? 4 : countCorrect >= 5 ? 3 : 2));
                }
            });
        }

        // Показываем первый вопрос
        showQuestion(currentQuestionIndex);
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        // Обновляем текст вопроса и ответы
        TextView questionNameText = findViewById(R.id.questionNameText);
        questionNameText.setText("№" + (index + 1) + ". " + questions.get(index).nameQuestion); // Первый элемент - вопрос
        progressBar.incrementProgressBy(1);
        for (int i = 0; i < radioButtons.length; i++) {

            if (i < questions.get(index).countVar) {
                radioButtons[i].setText(questions.get(index).questions.get(i));
                radioButtons[i].setVisibility(View.VISIBLE);
            } else {
                radioButtons[i].setVisibility(View.GONE); // Скрываем лишние RadioButton
            }
        }

        // Сброс выбора
        for (RadioButton rb : radioButtons) {
            rb.setChecked(false);
        }
    }

    private void loadQuestions() {
        try (InputStream inputStream = getResources().openRawResource(R.raw.questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            ArrayList<String> record = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!record.isEmpty()) {
                        data.add(new ArrayList<>(record));
                        record.clear();
                    }
                } else {
                    record.add(line);
                }
            }
            if (!record.isEmpty()) {
                data.add(record);
            }
            Collections.shuffle(data);
            for (int i = 0; i<data.size(); i++){
                questions.add(new Question(data.get(i)));
            }
            progressBar.setMax(data.size());
        } catch (IOException e) {
            Log.e("TestActivity", "Ошибка при чтении файла вопросов", e);
            Toast.makeText(this, "Ошибка при загрузке вопросов", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
