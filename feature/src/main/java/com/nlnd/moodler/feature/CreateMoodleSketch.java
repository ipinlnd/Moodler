package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;
import android.widget.Toast;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;

public class CreateMoodleSketch extends PApplet
{
    private int frame = 0;
    private static List<Sticks>    sticks;
    private static List<Snow>      snowes;
    private static List<Ring>      rings;
    private static List<FireWorks> fireworks;

    private boolean paused;
    private boolean touchLock = false;

    public static boolean saving = false;

    public static enum Method
    {
        snow,
        sticks,
        rings,
        firework
    }

    public static Method method;
    private MoodleButton pauseButton, playButton, stopButton;
    private MoodleSlider slider;

    private int lastStick = 0;
    private int lastSnow = 0;
    private int lastRing = 0;
    private int lastFire = 0;

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
        sticks  = new ArrayList<>();
        snowes  = new ArrayList<>();
        rings   = new ArrayList<>();
        fireworks = new ArrayList<>();

        pauseButton = new MoodleButton(30, height - 100, 95, 95, loadImage("pause_button.png"));
        playButton = new MoodleButton(30, height - 100, 95, 95, loadImage("play_button.png"));
        stopButton = new MoodleButton(30+ 95 - 10, height - 100, 95, 95, loadImage("stop_button.png"));
        slider = new MoodleSlider(220, height - 50, width - 240, 50,0, CreateMoodle.Companion.getMplayer().getDuration());

        paused = true;
        touchLock = false;
        method = Method.sticks;
    }

    @Override
    public void draw()
    {
        background(0);
        drawUI();

        if(!paused)
        {
            frame = CreateMoodle.Companion.getCurrentPosition();
            slider.setPos(CreateMoodle.Companion.getCurrentPosition());
            slider.update();
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
                lastStick = 0;
                lastSnow = 0;
                lastRing = 0;
                lastFire = 0;
                for(Sticks stick: sticks)
                    stick.reset();
                for(Snow snow: snowes)
                    snow.reset();
                for(Ring ring: rings)
                    ring.reset();
                for(FireWorks f: fireworks)
                    f.reset();
            }
            else if(touches.length > 0 && !paused)
            {
                for(int i=0;i<touches.length; i++)
                {
                    if(method == Method.sticks)
                    {
                        println(touches[i].area);
                        sticks.add(new Sticks((int) touches[i].x, (int) touches[i].y,  5, 10, frame));
                        Collections.sort(sticks);
                    }
                    else if(method == Method.snow)
                    {
                        snowes.add(new Snow((int) touches[i].x, (int) touches[i].y, 5, 10, frame));
                        Collections.sort(snowes);
                    }
                    else if(method == Method.rings)
                    {
                        rings.add(new Ring((int) touches[i].x, (int) touches[i].y, 5,  10, frame));
                        Collections.sort(rings);
                    }
                    else if(method == Method.firework)
                    {
                        fireworks.add(new FireWorks((int) touches[i].x, (int) touches[i].y, frame, 10));
                        Collections.sort(fireworks);
                    }
                }
            }
        }

        for(int i=lastStick; i<sticks.size(); i++)
        {
            if(sticks.get(i).time < frame - 500 || sticks.get(i).time > frame)
                continue;
            sticks.get(i).update();
            sticks.get(i).display();
            if(sticks.get(i).alpha <= 0)
                lastStick = i;
        }
        for(int i=lastSnow; i<snowes.size(); i++)
        {
            if(snowes.get(i).time < frame - 500 || snowes.get(i).time > frame)
                continue;
            snowes.get(i).update();
            snowes.get(i).display();
            if(snowes.get(i).alpha <= 0)
                lastSnow = i;
        }
        for(int i = lastRing; i < rings.size(); i++)
        {
            if(rings.get(i).time < frame - 500 || rings.get(i).time > frame)
                continue;
            rings.get(i).update();
            rings.get(i).display();
            if(rings.get(i).alpha <= 0)
                lastRing = i;
        }
        for (int i = lastFire; i < fireworks.size(); i++)
        {
            if(fireworks.get(i).t < frame - 500 || fireworks.get(i).t > frame)
                continue;
            fireworks.get(i).update();
            fireworks.get(i).display();
            if(fireworks.get(i).alpha <= 0)
                lastFire = i;
        }
    }

        public void drawUI()
        {
            if(paused)
            {
                playButton.show();
                frame = (int) slider.getPos();
                CreateMoodle.Companion.getMplayer().seekTo((int) slider.getPos());
                slider.update();
                slider.display();
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
                pauseButton.show();
                if (menu)
                    unsetView();
                menu = false;
            }
            stopButton.show();
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

        public void saveMoodle() throws IOException
        {
            List<String> str = new ArrayList<>();

            str.add(width + " " + height);

            for(int i=0;i<sticks.size();i++)
            {
                String line = "stick " + sticks.get(i).posX + " " + sticks.get(i).posY + " " + sticks.get(i).time;
                str.add(line);
            }

            for(int i=0;i<snowes.size();i++)
            {
                String line = "snow " + snowes.get(i).posX + " " + snowes.get(i).posY + " " + snowes.get(i).time;
                str.add(line);
            }

            for(int i=0;i<rings.size();i++)
            {
                String line = "ring " + rings.get(i).posX + " " + rings.get(i).posY + " " + rings.get(i).time;
                str.add(line);
            }

            for(int i=0;i<fireworks.size();i++)
            {
                String line = "fireworks " + fireworks.get(i).x + " " + fireworks.get(i).y + " " + fireworks.get(i).t;
                str.add(line);
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

        class MoodleButton
        {
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

        class MoodleSlider
        {
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

        class Sticks implements Comparable<Sticks>
        {
            int posX, posY;
            int red, green, blue;
            int alpha;
            int fadeSpeed;
            float size, originalSize;
            int time;

            Sticks(int posX, int posY, int fadeSpeed, float originalSize, int time) {
                this.posX = posX;
                this.posY = posY;
                this.fadeSpeed = fadeSpeed;
                this.originalSize = originalSize;
                this.time = time;
                this.red = posX % 255;
                this.blue = posY % 255;
                this.green = (posX * posY) % 255;
                this.size = originalSize;
                this.alpha = 255;
            }

            public void reset()
            {
                alpha = 255;
                size = originalSize;
            }

            public void update()
            {
                alpha -= fadeSpeed;
                if(size >= 1)
                    size -= .4;
            }

            public void display()
            {
                stroke(this.red, this.green, this.blue, this.alpha);
                strokeWeight(this.size);
                line(posX, posY, posX, height - posY);
            }

            @Override
            public int hashCode() {
                return this.time;

            }

            @Override
            public int compareTo(Sticks o) {
                return (this.time - o.time) ;
            }
        }

        class Snow implements Comparable<Snow>
        {
            int posX, posY;
            int red, green, blue;
            int alpha;
            int fadeSpeed;
            float size, originalSize;
            int time;

            Snow(int posX, int posY, int fadeSpeed, float originalSize, int time) {
                this.posX = posX;
                this.posY = posY;
                this.fadeSpeed = fadeSpeed;
                this.originalSize = originalSize;
                this.time = time;
                this.red = posX % 255;
                this.blue = posY % 255;
                this.green = (posX * posY) % 255;
                this.size = originalSize;
                this.alpha = 255;
            }

            public void reset()
            {
                alpha = 255;
                size = originalSize;
            }

            public void update()
            {
                alpha -= fadeSpeed;
                if(size >= 1)
                    size -= .4;
            }

            public void display()
            {
                stroke(this.red, this.green, this.blue, this.alpha);
                strokeWeight(this.size);
                line(posX, posY, width - posX, height - posY);
            }

            @Override
            public int hashCode() {
                return this.time;

            }

            @Override
            public int compareTo(Snow o) {
                return (this.time - o.time) ;
            }

        }

        class Ring implements Comparable<Ring>
        {
            int posX, posY;
            int red, green, blue;
            int alpha;
            int fadeSpeed;
            float size, originalSize;
            int time;

            Ring(int posX, int posY, int fadeSpeed, float originalSize, int time) {
                this.posX = posX;
                this.posY = posY;
                this.fadeSpeed = fadeSpeed;
                this.originalSize = originalSize;
                this.time = time;
                this.red = posX % 255;
                this.blue = posY % 255;
                this.green = (posX * posY) % 255;
                this.size = originalSize;
                this.alpha = 255;
            }

            public void reset()
            {
                alpha = 255;
            }

            public void update()
            {
                size += 10;
                alpha -= 1;
            }

            public void display()
            {
                stroke(red, blue, green, alpha);
                strokeWeight(1);
                fill(0,0);
                ellipse(posX, posY, size, size);
            }

            @Override
            public int hashCode() {
                return this.time;

            }

            @Override
            public int compareTo(Ring o) {
                return (this.time - o.time) ;
            }
        }

        class FireWorks implements Comparable<FireWorks>
        {
            int x,y,t;
            int red, green, blue;
            int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6;
            int x11,y11,x21,y21,x31,y31,x41,y41,x51,y51,x61,y61;
            int originalSize, size;

            int alpha;
            FireWorks(int x, int y, int t, int size)
            {
                this.x = x;
                this.y = y;
                this.t = t;
                x1 = x2 = x3 = x4 = x5 = x6 = x;
                x11 = x21 = x31 = x41 = x51 = x61 = x;
                y1 = y2 = y3 = y4 = y5 = y6 = y;
                y11 = y21 = y31 = y41 = y51 = y61 = y;
                alpha = 255;
                this.red = x % 255;
                this.blue = y % 255;
                this.green = (x * y) % 255;
                originalSize = size;
                this.size = originalSize;
            }

            public void update()
            {
                this.alpha -=2;
                size -= 1;
                y1 += (int)random(1f, 10f);
                y2 += (int)random(1f, 10f); x2 += (int)random(1f, 10f);
                y3 += (int)random(1f, 10f); x3 -= (int)random(1f, 10f);
                y4 -= (int)random(1f, 10f);
                y5 -= (int)random(1f, 10f); x5 -= (int)random(1f, 10f);
                y6 -= (int)random(1f, 10f); x6 += (int)random(1f, 10f);
                y11 += (int)random(1f, 10f); x11 += (int)random(1f, 10f);
                y21 += (int)random(1f, 10f); x21 += (int)random(1f, 10f);
                y31 += (int)random(1f, 10f); x31 -= (int)random(1f, 10f);
                y41 -= (int)random(1f, 10f); x41 -= (int)random(1f, 10f);
                y51 -= (int)random(1f, 10f); x51 -= (int)random(1f, 10f);
                y61 -= (int)random(1f, 10f); x61 += (int)random(1f, 10f);
            }

            public void display()
            {
                fill(red, green, blue, this.alpha);
                stroke(red, green, blue, this.alpha);
                ellipse(x1,y1,size,size);
                ellipse(x2,y2,size,size);
                ellipse(x3,y3,size,size);
                ellipse(x4,y4,size,size);
                ellipse(x5,y5,size,size);
                ellipse(x6,y6,size,size);
                ellipse(x11,y11,size,size);
                ellipse(x21,y21,size,size);
                ellipse(x31,y31,size,size);
                ellipse(x41,y41,size,size);
                ellipse(x51,y51,size,size);
                ellipse(x61,y61,size,size);
            }

            @Override
            public int compareTo(@NonNull FireWorks fireWorks)
            {
                return (this.t - fireWorks.t) ;
            }

            public void reset()
            {
                alpha = 255;
                size = originalSize;
            }
        }
}
