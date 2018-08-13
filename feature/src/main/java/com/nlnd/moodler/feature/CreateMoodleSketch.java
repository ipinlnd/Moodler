package com.nlnd.moodler.feature;

import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.TouchEvent;

public class CreateMoodleSketch extends PApplet
{
    private int frame = 0;
    private PGraphics graphics;

    private boolean paused;
    private boolean touchLock = false;

    public static boolean saving = false;
    private boolean loaded = false;

    public enum Method
    {
        snow,
        sticks,
        rings,
        firework
    }

    private Map<Integer, List<MoodleObject>> objects;

    public static Method method;
    private MoodleButton pauseButton, playButton, stopButton;
    private MoodleSlider slider;

    private boolean menu = false;

    @Override
    public void settings()
    {
        fullScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CreateMoodle.Companion.stopMusic();
    }

    @Override
    public void setup()
    {
        background(0);
        orientation(LANDSCAPE);
        objects = new HashMap<>();
        graphics = g;
        pauseButton = new MoodleButton(30, height - 100, 95, 95, loadImage("pause_button.png"));
        playButton = new MoodleButton(30, height - 100, 95, 95, loadImage("play_button.png"));
        stopButton = new MoodleButton(30+ 95 - 10, height - 100, 95, 95, loadImage("stop_button.png"));
        slider = new MoodleSlider(220, height - 50, width - 240, 50,0, CreateMoodle.Companion.getMplayer().getDuration());

        if (CreateMoodle.Companion.getContinueMoodle())
            loadMoodle();
        else
            loaded = true;

        paused = true;
        touchLock = false;
        method = Method.sticks;
    }

    @Override
    public void draw()
    {
        background(0);

        if (!loaded)
            return;

        drawUI();

        if(!paused)
        {
            frame = CreateMoodle.Companion.getCurrentPosition();
            slider.setPos(CreateMoodle.Companion.getCurrentPosition());
            slider.update(this, objects);
        }

        if(frame == CreateMoodle.Companion.getDuration())
        {
            paused = true;
            CreateMoodle.Companion.pauseMusic();
        }

        if(mousePressed && !touchLock)
        {
            if(playButton.isClicked(mouseX, mouseY) || pauseButton.isClicked(mouseX, mouseY) )
            {
                paused = !paused;
                touchLock = true;
                if(paused)
                    CreateMoodle.Companion.pauseMusic();
                else
                    CreateMoodle.Companion.startMusic();
            }
            else if(stopButton.isClicked(mouseX, mouseY))
            {
                paused = true;
                frame = 0;
                CreateMoodle.Companion.musicSeekTo(0);
                CreateMoodle.Companion.pauseMusic();

            }
            else if(touches.length > 0 && !paused)
            {
                for (TouchEvent.Pointer touche : touches)
                {
                    if (method == Method.sticks)
                    {
                        Sticks s = new Sticks((int) touche.x, (int) touche.y, 5, 10, frame);
                        if (!objects.containsKey(frame))
                            objects.put(frame, new ArrayList<MoodleObject>());
                        objects.get(frame).add(s);
                    } else if (method == Method.snow)
                    {
                        Snow s = new Snow((int) touche.x, (int) touche.y, 5, 10, frame);
                        if (!objects.containsKey(frame))
                            objects.put(frame, new ArrayList<MoodleObject>());
                        objects.get(frame).add(s);
                    } else if (method == Method.rings)
                    {
                        println(touche.pressure);
                        Ring s = new Ring((int) touche.x, (int) touche.y, 5, 20, frame, touche.pressure);
                        if (!objects.containsKey(frame))
                            objects.put(frame, new ArrayList<MoodleObject>());
                        objects.get(frame).add(s);
                    } else if (method == Method.firework)
                    {
                        FireWorks s = new FireWorks((int) touche.x, (int) touche.y, frame);
                        if (!objects.containsKey(frame))
                            objects.put(frame, new ArrayList<MoodleObject>());
                        objects.get(frame).add(s);
                    }
                }
            }
        }
        for (int i = frame - 500; i <= frame; i++)
        {
            if (!objects.containsKey(i))
                continue;
            for (int j = 0; j <objects.get(i).size(); j++)
            {
                if(!paused)
                    objects.get(i).get(j).update();
                objects.get(i).get(j).display(graphics);
            }
        }
    }

    private void loadMoodle()
    {
        float multX, multY;
        float theirX, theirY;
        String[] config = CreateMoodle.Companion.getConfig();

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
                            parseInt(config[i].split(" ")[3]),parseFloat(config[i].split(" ")[4]) ));
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

    private void drawUI()
    {
        if(paused)
        {
            playButton.show(this);
            frame = slider.getPos();
            CreateMoodle.Companion.getMplayer().seekTo((int) slider.getPos());
            slider.update(this, objects);
            slider.display(this);
            if (saving)
            {
                try
                {
                    saving = false;
                    saveMoodle();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (!menu)
            {
                setView();
                menu = true;
            }
        }
        else
        {
            pauseButton.show(this);
            if (menu)
                unsetView();
            menu = false;
        }
        stopButton.show(this);
    }

    private void unsetView()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                CreateMoodle.Companion.unSetView();
            }
        });
    }

    private void setView()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                CreateMoodle.Companion.setView();
            }
        });
    }

    private void saveMoodle() throws IOException
    {
        List<String> str = new ArrayList<>();

        str.add(width + " " + height);

        for (Map.Entry<Integer, List<MoodleObject>> entry : objects.entrySet())
        {
            for (int j = 0; j < entry.getValue().size(); j ++)
            {
                MoodleObject o = entry.getValue().get(j);
                String line = o.name + " " + o.posX + " " + o.posY + " " + o.time;
                if (o.name.equals("ring"))
                    line += " " + o.pressure;
                str.add(line);
            }
        }
        File combined = new File(getActivity().getFilesDir()+"/"+ FullscreenActivity.Companion.getFileName() + ".v3v");

        DataOutputStream osw = new DataOutputStream(new FileOutputStream(combined));
        DataInputStream dis = new DataInputStream
                (getContext().getContentResolver().openInputStream(CreateMoodle.Companion.getMusicFile()));

        osw.writeBytes("begin\n");
        for (int i=0;i<str.size(); i++)
            osw.writeBytes(str.get(i) + "\n");
        osw.writeBytes("end\n");

        byte[] b = new byte[dis.available()];
        dis.read(b);
        osw.write(b);
        osw.close();
        dis.close();

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getActivity().getApplicationContext(), "Done saving", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void mouseReleased() {
        touchLock = false;
    }
}
