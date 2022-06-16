package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by <VOTRE-NOM> on <DATE-DU-JOUR>.
 * Project Name
 */
public class MainGame extends AppCompatActivity {
    public static ImageView img_swipe;
    public static Dialog dialogDeath;
    private MapMenu gv;
    public static TextView txt_score, txt_best_score;
    public static ArrayList<String> score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        score = new ArrayList();
        try {
            chargerDonnees();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        setContentView(R.layout.activity_game);
        img_swipe = findViewById(R.id.img_swipe);
        gv = findViewById(R.id.gv);
        txt_score = findViewById(R.id.txt_score);
        txt_best_score = findViewById(R.id.txt_best_score);
        dialogDeath();
    }

    public void chargerDonnees() throws IOException {
        File file = new File(getFilesDir(), "stats.txt");

        if(!file.exists()) return;

        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = new FileInputStream(file);
        try {
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        String contents = new String(bytes);
        String[] data = contents.split("\n");
        for (int i=0; i < data.length; i++){
            score.add(data[i]);
        }
    }

    private void dialogDeath() {
        dialogDeath = new Dialog(this);
        dialogDeath.setContentView(R.layout.dialog_start);
        dialogDeath.setCanceledOnTouchOutside(false);
        RelativeLayout rl_start = dialogDeath.findViewById(R.id.rl_start);
        rl_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_swipe.setVisibility(View.VISIBLE);
                gv.reset();
                dialogDeath.dismiss();
            }
        });
        RelativeLayout rl_menu = dialogDeath.findViewById(R.id.rl_menu);
        rl_menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        dialogDeath.show();
    }
}
