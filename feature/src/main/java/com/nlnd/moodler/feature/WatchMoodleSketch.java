package com.nlnd.moodler.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;

public class WatchMoodleSketch extends PApplet
{
    private int frame = 0;
    private Map<Integer, List<MoodleObject>> objects;

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
    public void onStop()
    {
        super.onStop();
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
            slider.update(this, objects);
        }

        if(frame == WatchMoodle.Companion.getDuration())
        {
            paused = true;
            WatchMoodle.Companion.pauseMusic();
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
                if (i == frame)
                    objects.get(i).get(j).reset();
                if (!paused)
                    objects.get(i).get(j).update();
                objects.get(i).get(j).display(pGraphics);
            }
        }
    }

    private void drawUI()
    {
        if(paused)
        {
            playButton.show(this);
            frame = slider.getPos();
            WatchMoodle.Companion.getMplayer().seekTo((int) slider.getPos());
            slider.update(this, objects);
            slider.display(this);
        }
        else
        {
            pauseButton.show(this);
        }
        stopButton.show(this);
    }

    private void loadMoodle()
    {
        float multX, multY;
        float theirX, theirY;
        String[] config = WatchMoodle.Companion.getConfig();
        theirX = Float.parseFloat(config[1].split(" ")[0]);
        theirY = Float.parseFloat(config[1].split(" ")[1]);

        multX = width / theirX;
        multY = height / theirY;
        for(int i=1;i<config.length;i++)
        {
            switch (config[i].split(" ")[0])
            {
                case "stick":
                {
                    Sticks s = new Sticks((int) (parseInt(config[i].split(" ")[1]) * multX),
                            (int) (parseInt(config[i].split(" ")[2]) * multY), 5, 10,
                            parseInt(config[i].split(" ")[3]));
                    int f = parseInt(config[i].split(" ")[3]);
                    if (!objects.containsKey(f))
                        objects.put(f, new ArrayList<MoodleObject>());
                    objects.get(f).add(s);
                    break;
                }
                case "snow":
                {
                    Snow s = (new Snow((int) (parseInt(config[i].split(" ")[1]) * multX),
                            (int) (parseInt(config[i].split(" ")[2]) * multY), 5, 10,
                            parseInt(config[i].split(" ")[3])));
                    int f = parseInt(config[i].split(" ")[3]);
                    if (!objects.containsKey(f))
                        objects.put(f, new ArrayList<MoodleObject>());
                    objects.get(f).add(s);
                    break;
                }
                case "ring":
                {
                    Ring s = (new Ring((int) (parseInt(config[i].split(" ")[1]) * multX),
                            (int) (parseInt(config[i].split(" ")[2]) * multY), 5, 5,
                            parseInt(config[i].split(" ")[3]),parseInt(config[i].split(" ")[4])));
                    int f = parseInt(config[i].split(" ")[3]);
                    if (!objects.containsKey(f))
                        objects.put(f, new ArrayList<MoodleObject>());
                    objects.get(f).add(s);
                    break;
                }
                case "fireworks":
                {
                    FireWorks s = (new FireWorks((int) (parseInt(config[i].split(" ")[1]) * multX),
                            (int) (parseInt(config[i].split(" ")[2]) * multY),
                            parseInt(config[i].split(" ")[3])));
                    int f = parseInt(config[i].split(" ")[3]);
                    if (!objects.containsKey(f))
                        objects.put(f, new ArrayList<MoodleObject>());
                    objects.get(f).add(s);
                    break;
                }
            }
        }
        loaded = true;
    }

    @Override
    public void mouseReleased() {
        touchLock = false;
    }

}
