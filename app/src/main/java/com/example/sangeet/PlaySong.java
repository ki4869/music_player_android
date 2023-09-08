package com.example.sangeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaplayer.stop();
        mediaplayer.release();
        updateSeek.interrupt();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down); // slide animation
    }


    TextView textView, startTime, endTime;
    ImageView previous, play, next, loop;

    ArrayList<File> songs;
    MediaPlayer mediaplayer;
    String textcontent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    boolean repeatPressedTwice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        loop = findViewById(R.id.loop);





        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("SongList");
        textcontent = intent.getStringExtra("currentSong");
        position = intent.getIntExtra("position", 0);


        textView.setText(textcontent);
        textView.setSelected(true);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaplayer = MediaPlayer.create(this, uri);
        mediaplayer.start();


        mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {


            }
        });


        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
                seekBar.setProgress(0);


            }
        });


        seekBar.setMax(mediaplayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaplayer.seekTo(seekBar.getProgress());


            }
        });

        String endtime = CreateTime(mediaplayer.getDuration());
        endTime.setText(endtime);

        final Handler handler = new Handler();
        final int delay =1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    String currentTime = CreateTime(mediaplayer.getCurrentPosition());
                    startTime.setText(currentTime);

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                handler.postDelayed(this,delay);
            }
        },delay);







        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentposition = 0;
                try {
                    while (currentposition < mediaplayer.getDuration()) {
                        currentposition = mediaplayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        sleep(800);

                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        };
        updateSeek.start();





        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaplayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaplayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaplayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaplayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaplayer.getDuration());
                textcontent = songs.get(position).getName().toString();
                textView.setText(textcontent);
                textView.setSelected(true);


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaplayer.start();
                play.setImageResource(R.drawable.pause);
                String endtime = CreateTime(mediaplayer.getDuration());
                endTime.setText(endtime);
                seekBar.setMax(mediaplayer.getDuration());
                textcontent = songs.get(position).getName().toString();
                textView.setText(textcontent);
                seekBar.setProgress(0);



            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repeatPressedTwice) {

                    mediaplayer.setLooping(true);
                    Toast.makeText(PlaySong.this, "Looping Enabled", Toast.LENGTH_SHORT).show();
                    repeatPressedTwice = true;
                } else {
                    mediaplayer.setLooping(false);
                    Toast.makeText(PlaySong.this, "Looping Disabled", Toast.LENGTH_SHORT).show();
                }


            }

        });







    }

    public String CreateTime(int duration){

        String time ="";
        int min = duration / 1000/60;
        int sec = duration /1000%60;

        time = time +min+":";
        if(sec<10){

            time+="0";

        }
        time+=sec;
        return time;

    }


    }































