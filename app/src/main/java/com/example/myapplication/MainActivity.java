package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static Button start;
    private static Button leaderboard;
    public static TextView bestscore,score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        setContentView(R.layout.activity_main);
        bestscore = findViewById(R.id.bestscore_);
        MainActivity.bestscore.setText(MapMenu.bestScore+"");
        score = findViewById(R.id.score_);
        MainActivity.score.setText(MapMenu.score+"");



        start = (Button) findViewById(R.id.button_start);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openGame();
            }
        });

        leaderboard = (Button) findViewById(R.id.button_learderboard);
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScore();
            }
        });

    }
    public void openGame(){
        Intent intent = new Intent(this,MainGame.class);
        startActivity(intent);
    }
    private void openScore(){
        Intent intent = new Intent(this, ListeBestScore.class);
        startActivity(intent);
    }

}