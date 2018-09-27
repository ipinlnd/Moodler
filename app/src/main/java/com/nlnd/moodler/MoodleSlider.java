package com.nlnd.moodler;

import java.util.List;
import java.util.Map;

import processing.core.PApplet;

class MoodleSlider
{
    private int x, y;
    private int width;
    private int height;
    private int min, max;
    private int current;
    private int col = 50;
    private boolean lock = false;
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

    void update(PApplet p, Map<Integer, List<MoodleObject>> objects)
    {
        if(p.mousePressed)
        {
            if(p.mouseX > x && p.mouseX < x + this.width && p.mouseY > y && p.mouseY < y+ this.height)
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
            if((p.mouseX) >= x && (p.mouseX) <= x + width)
                current = (p.mouseX - x);
            for (Map.Entry<Integer, List<MoodleObject>> entry : objects.entrySet())
            {
                for (MoodleObject o : entry.getValue())
                    o.reset();
            }
        }
    }
    void display(PApplet p)
    {
        p.fill(255);
        p.rectMode(p.CORNER);
        p.rect(x, y, this.width, this.height);
        p.fill(col);
        p.rectMode(p.CENTER);
        p.rect(x+current, y+this.height/2, 20, this.height);

    }

    int getPos()
    {
        return (current * max) / this.width;
    }
}