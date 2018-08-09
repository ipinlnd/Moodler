package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class WatchMoodleSketch extends PApplet
{
    private int frame = 0;
    private Map<Integer, List<MoodleObject>> objects;

    float multX, multY;
    float theirX, theirY;

    private boolean paused;
    private boolean touchLock = false;

    private MoodleButton pauseButton, playButton, stopButton;
    private MoodleSlider slider;

    private boolean render = false;
    private PGraphics pGraphics;
    private boolean loaded = false;

    @Override
    public void settings()
    {
        fullScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WatchMoodle.Companion.stopMusic();
    }

    @Override
    public void setup()
    {
        background(0);
        orientation(LANDSCAPE);

        objects = new HashMap<>();

        pauseButton = new MoodleButton(30, height - 100, 95, 95, loadImage("pause_button.png"));
        playButton = new MoodleButton(30, height - 100, 95, 95, loadImage("play_button.png"));
        stopButton = new MoodleButton(30+ 95 - 10, height - 100, 95, 95, loadImage("stop_button.png"));
        slider = new MoodleSlider(220, height - 50, width - 240, 50,0, WatchMoodle.Companion.getMplayer().getDuration());
        loaded = false;

        pGraphics = g;

        render = WatchMoodle.Companion.getRender();

        loadMoodle();

        if (render)
        {
            WatchMoodle.Companion.mute();
            paused = false;
            WatchMoodle.Companion.startMusic();
        }
        else
            paused = true;

        touchLock = false;
    }

    @Override
    public void draw()
    {
        background(0);
        if (!loaded)
            return;

        if (!render)
            drawUI();

        if(!paused)
        {
            frame = WatchMoodle.Companion.getCurrentPosition();
            slider.setPos(WatchMoodle.Companion.getCurrentPosition());
            slider.update();
        }

        if(frame == WatchMoodle.Companion.getDuration())
        {
            paused = true;
            WatchMoodle.Companion.pauseMusic();
            List<File> images = Arrays.asList(new File(getActivity().getFilesDir() + "/render").listFiles());
        }

        if(mousePressed && !touchLock)
        {
            if(playButton.isClicked(mouseX, mouseY) || pauseButton.isClicked(mouseX, mouseY) )
            {
                paused = !paused;
                touchLock = true;
                if(paused)
                    WatchMoodle.Companion.pauseMusic();
                else
                    WatchMoodle.Companion.startMusic();
            }
            else if(stopButton.isClicked(mouseX, mouseY))
            {
                paused = true;
                frame = 0;
                WatchMoodle.Companion.musicSeekTo(0);
                WatchMoodle.Companion.pauseMusic();
            }
        }
        for (int i = frame - 500; i <= frame; i++)
        {
            if (!objects.containsKey(i))
                continue;
            for (int j = 0; j <objects.get(i).size(); j++)
            {
                objects.get(i).get(j).update();
                objects.get(i).get(j).display(pGraphics);
            }
        }
        textSize(100);
        stroke(255);
        fill(255);
        text(frameRate, 10, 100);

        if (render)
            saveFrame(getActivity().getFilesDir()+"/render"+"/"+"######.png");
    }

    public void drawUI()
    {
        if(paused)
        {
            playButton.show();
            frame = (int) slider.getPos();
            WatchMoodle.Companion.getMplayer().seekTo((int) slider.getPos());
            slider.update();
            slider.display();
        }
        else
        {
            pauseButton.show();
        }
        stopButton.show();
    }

    public void loadMoodle()
    {
        String[] config = WatchMoodle.Companion.getConfig();
        theirX = Float.parseFloat(config[1].split(" ")[0]);
        theirY = Float.parseFloat(config[1].split(" ")[1]);

        multX = width / theirX;
        multY = height / theirY;
        for(int i=1;i<config.length;i++)
        {
            if(config[i].split(" ")[0].equals("stick"))
            {
                Sticks s = new Sticks((int)(parseInt(config[i].split(" ")[1]) * multX),
                            (int)(parseInt(config[i].split(" ")[2]) * multY), 5, 10,
                            parseInt(config[i].split(" ")[3]));
                int f = parseInt(config[i].split(" ")[3]);
                if (!objects.containsKey(f))
                    objects.put(f, new ArrayList<MoodleObject>());
                objects.get(f).add(s);
            }
            else if(config[i].split(" ")[0].equals("snow"))
            {
                Snow s = (new Snow((int)(parseInt(config[i].split(" ")[1]) * multX),
                            (int)(parseInt(config[i].split(" ")[2]) * multY), 5, 10,
                            parseInt(config[i].split(" ")[3])));
                int f = parseInt(config[i].split(" ")[3]);
                if (!objects.containsKey(f))
                    objects.put(f, new ArrayList<MoodleObject>());
                objects.get(f).add(s);
            }
            else if(config[i].split(" ")[0].equals("ring"))
            {
                Ring s = (new Ring((int)(parseInt(config[i].split(" ")[1]) * multX),
                            (int)(parseInt(config[i].split(" ")[2]) * multY), 5, 5,
                            parseInt(config[i].split(" ")[3])));
                int f = parseInt(config[i].split(" ")[3]);
                if (!objects.containsKey(f))
                    objects.put(f, new ArrayList<MoodleObject>());
                objects.get(f).add(s);
            }
            else if(config[i].split(" ")[0].equals("fireworks"))
            {
                FireWorks s = (new FireWorks((int)(parseInt(config[i].split(" ")[1]) * multX),
                                (int)(parseInt(config[i].split(" ")[2]) * multY),
                                parseInt(config[i].split(" ")[3])));
                int f = parseInt(config[i].split(" ")[3]);
                if (!objects.containsKey(f))
                    objects.put(f, new ArrayList<MoodleObject>());
                objects.get(f).add(s);
            }
            else
                continue;
        }
        loaded = true;
    }

    @Override
    public void mouseReleased() {
        touchLock = false;
    }

    class MoodleButton {
        int x, y;
        int width, height;
        PImage buttonImage;
        MoodleButton(int x, int y, int w, int h, PImage image)
        {
            this.x = x;
            this.y = y;
            this.buttonImage = image;
            this.width = w;
            this.height = h;
        }

        boolean isClicked(int x, int y)
        {
            if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + height)
                return true;
            else
                return false;
        }

        void show()
        {
            image(this.buttonImage, x, y, this.width, this.height);
        }
    }

    class MoodleSlider {
        int x, y;
        int width;
        int height;
        int min, max;
        int current;
        int col = 50;
        boolean lock = false;
        MoodleSlider(int x, int y, int w, int h, int min, int max)
        {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.min = min;
            this.max = max;
            current = min;
        }

        void setPos(int pos)
        {
            int temp = (this.width * pos) / max;
            if(temp < this.width -10)
                current = temp;
        }

        void update()
        {
            if(mousePressed)
            {
                if(mouseX > x && mouseX < x + this.width && mouseY > y && mouseY < y+ this.height)
                    lock = true;
            }
            else
            {
                col = 50;
                lock = false;
            }

            if(lock)
            {
                col = 100;
                if((mouseX) >= x && (mouseX) <= x + width)
                    current = (mouseX - x);
            }
        }
        void display()
        {
            fill(255);
            rectMode(CORNER);
            rect(x, y, this.width, this.height);
            fill(col);
            rectMode(CENTER);
            rect(x+current, y+this.height/2, 20, this.height);

        }

        public int getPos()
        {
            return (current * max) / this.width;
        }
    }
}
