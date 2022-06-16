package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MapMenu extends View {
    private static final String tag = "MyActivity";
    private Bitmap bmGrass1, bmGrass2, bmSnake1, bmApple, bmStop;
    private ArrayList<Background> arrBackgrounds = new ArrayList<>();
    private int w = 12, h = 21;
    public static int sizeElementMap = 75*Constants.SCREEN_WIDTH/1080;
    private MySnake mySnake;
    private Object apple;
    private Object stop;
    private Handler handler;
    private Runnable r;
    private boolean move = false;
    private float mx, my;
    public static boolean isPlaying = false;
    public static int score = 0, bestScore = 0, oldBestScore = 0;
    private Context context;
    private int soundEat, soundDie;
    private float volume;
    private boolean loadedsound;
    private SoundPool soundPool;

    public MapMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
        if(sp!=null){
            bestScore = sp.getInt("bestscore",0);
            oldBestScore = sp.getInt("bestscore",0);
        }
        bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.texture_grass_1);
        bmGrass1 = Bitmap.createScaledBitmap(bmGrass1, sizeElementMap, sizeElementMap, true);
        bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.texture_grass_2);
        bmGrass2 = Bitmap.createScaledBitmap(bmGrass2, sizeElementMap, sizeElementMap, true);
        bmSnake1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.snake1);
        bmSnake1 = Bitmap.createScaledBitmap(bmSnake1, 14*sizeElementMap, sizeElementMap, true);
        bmApple = BitmapFactory.decodeResource(this.getResources(), R.drawable.ze_pom);
        bmApple = Bitmap.createScaledBitmap(bmApple, sizeElementMap, sizeElementMap, true);
        bmStop = BitmapFactory.decodeResource(this.getResources(), R.drawable.stop);
        bmStop = Bitmap.createScaledBitmap(bmStop, sizeElementMap, sizeElementMap, true);

        for(int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                if((j+i)%2==0){
                    arrBackgrounds.add(new Background(bmGrass1, j*bmGrass1.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }else{
                    arrBackgrounds.add(new Background(bmGrass2, j*bmGrass2.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                }
            }
        }
        mySnake = new MySnake(bmSnake1, arrBackgrounds.get(126).getX(), arrBackgrounds.get(126).getY(), 4);
        apple = new Object(bmApple, arrBackgrounds.get(objectPlacementRandom()[0]).getX(), arrBackgrounds.get(objectPlacementRandom()[1]).getY());
        stop = new Object(bmStop, arrBackgrounds.get(objectPlacementRandom()[0]).getX(), arrBackgrounds.get(objectPlacementRandom()[1]).getY());
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        if(Build.VERSION.SDK_INT>=21){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedsound = true;
            }
        });
        soundEat = this.soundPool.load(context, R.raw.eating_voice, 1);
        soundDie = this.soundPool.load(context, R.raw.dying_voice, 1);
        Timer timer = new Timer();
        timer.schedule(new stopload(), 0, 5000);
    }

    class stopload extends TimerTask {
        public void run() {
            stop.reset(arrBackgrounds.get(objectPlacementRandom()[0]).getX(), arrBackgrounds.get(objectPlacementRandom()[1]).getY());
        }
    }
    private int[] objectPlacementRandom(){
        int []xy = new int[2];
        Random r = new Random();
        xy[0] = r.nextInt(arrBackgrounds.size()-1);
        xy[1] = r.nextInt(arrBackgrounds.size()-1);
        Rect rect = new Rect(arrBackgrounds.get(xy[0]).getX(), arrBackgrounds.get(xy[1]).getY(), arrBackgrounds.get(xy[0]).getX()+sizeElementMap, arrBackgrounds.get(xy[1]).getY()+sizeElementMap);
        boolean check = true;
        while (check){
            check = false;
            for (int i = 0; i < mySnake.getArrPartSnake().size(); i++){
                if(rect.intersect(mySnake.getArrPartSnake().get(i).getrBody())){
                    check = true;
                    xy[0] = r.nextInt(arrBackgrounds.size()-1);
                    xy[1] = r.nextInt(arrBackgrounds.size()-1);
                    rect = new Rect(arrBackgrounds.get(xy[0]).getX(), arrBackgrounds.get(xy[1]).getY(), arrBackgrounds.get(xy[0]).getX()+sizeElementMap, arrBackgrounds.get(xy[1]).getY()+sizeElementMap);
                }
            }
        }
        return xy;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getActionMasked();
        switch (a){
            case  MotionEvent.ACTION_MOVE:{
                if(!move){
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                }else{
                    if(mx - event.getX() > 100 && !mySnake.isMove_right()){
                        mx = event.getX();
                        my = event.getY();
                        this.mySnake.setMove_left(true);
                        isPlaying = true;
                        MainGame.img_swipe.setVisibility((INVISIBLE));
                    }else if(event.getX() - mx > 100 &&!mySnake.isMove_left()){
                        mx = event.getX();
                        my = event.getY();
                        this.mySnake.setMove_right(true);
                        isPlaying = true;
                        MainGame.img_swipe.setVisibility((INVISIBLE));
                    }else if(event.getY() - my > 100 && !mySnake.isMove_up()){
                        mx = event.getX();
                        my = event.getY();
                        this.mySnake.setMove_down(true);
                        isPlaying = true;
                        MainGame.img_swipe.setVisibility((INVISIBLE));
                    }else if(my - event.getY() > 100 && !mySnake.isMove_down()){
                        mx = event.getX();
                        my = event.getY();
                        this.mySnake.setMove_up(true);
                        isPlaying = true;
                        MainGame.img_swipe.setVisibility((INVISIBLE));
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                mx = 0;
                my = 0;
                move = false;
                break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(0xFF065700);
        for(int i = 0; i < arrBackgrounds.size(); i++){
            canvas.drawBitmap(arrBackgrounds.get(i).getBm(), arrBackgrounds.get(i).getX(), arrBackgrounds.get(i).getY(), null);
        }
        if(isPlaying){
            mySnake.update();
            if(mySnake.getArrPartSnake().get(0).getX() < this.arrBackgrounds.get(0).getX()
                    || mySnake.getArrPartSnake().get(0).getY() < this.arrBackgrounds.get(0).getY()
                    || mySnake.getArrPartSnake().get(0).getY()+sizeElementMap>this.arrBackgrounds.get(this.arrBackgrounds.size()-1).getY() + sizeElementMap
                    || mySnake.getArrPartSnake().get(0).getX()+sizeElementMap>this.arrBackgrounds.get(this.arrBackgrounds.size()-1).getX() + sizeElementMap){
                gameOver();
            }
            for (int i = 1; i < mySnake.getArrPartSnake().size(); i++){
                if (mySnake.getArrPartSnake().get(0).getrBody().intersect(mySnake.getArrPartSnake().get(i).getrBody())){
                    gameOver();
                }
            }
        }
        mySnake.drawSnake(canvas);
        apple.draw(canvas);
        stop.draw(canvas);
        if(mySnake.getArrPartSnake().get(0).getrBody().intersect(apple.getR())){
            if(loadedsound){
                int streamId = this.soundPool.play(this.soundEat, (float)0.5, (float)0.5, 1, 0, 1f);
            }
            apple.reset(arrBackgrounds.get(objectPlacementRandom()[0]).getX(), arrBackgrounds.get(objectPlacementRandom()[1]).getY());
            mySnake.addPart();
            score++;
            MainGame.txt_score.setText(score+"");
            MainActivity.score.setText(score+"");
            if(score > bestScore){
                bestScore = score;
                SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("bestscore", bestScore);
                editor.apply();
                MainActivity.bestscore.setText(bestScore+"");
                MainGame.txt_best_score.setText(bestScore+"");
            }
        }
        if(mySnake.getArrPartSnake().get(0).getrBody().intersect(stop.getR())){
            if(loadedsound){
                int streamId = this.soundPool.play(this.soundDie, (float)0.5, (float)0.5, 1, 0, 1f);
            }
            gameOver();
        }
        handler.postDelayed(r, 100);
    }

    public void sauvegarderDonnees() throws IOException {
        File file = new File(this.context.getFilesDir(), "stats.txt");
        if(file.exists()) file.delete();

        FileOutputStream stream = new FileOutputStream(file);
        try {
            int length = MainGame.score.size();
            for(int i=0; i < length-1; i++){
                stream.write((MainGame.score.get(i)+"\n").getBytes());
            }
            stream.write((MainGame.score.get(length-1)).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }
    private void gameOver() {
        isPlaying = false;
        if(bestScore > oldBestScore){
            MainGame.score.add(String.valueOf(bestScore));
        }
        try {
            sauvegarderDonnees();
        } catch (IOException e){
            e.printStackTrace();
        }
        MainGame.dialogDeath.show();
        if(loadedsound){
            int streamId = this.soundPool.play(this.soundDie, (float)0.5, (float)0.5, 1, 0, 1f);
        }
    }
    public void reset(){
        for(int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                if((j+i)%2==0){
                    arrBackgrounds.add(new Background(bmGrass1, j*bmGrass1.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }else{
                    arrBackgrounds.add(new Background(bmGrass2, j*bmGrass2.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                }
            }
        }
        mySnake = new MySnake(bmSnake1, arrBackgrounds.get(126).getX(), arrBackgrounds.get(126).getY(), 4);
        apple = new Object(bmApple, arrBackgrounds.get(objectPlacementRandom()[0]).getX(), arrBackgrounds.get(objectPlacementRandom()[1]).getY());
        score = 0;
    }


}