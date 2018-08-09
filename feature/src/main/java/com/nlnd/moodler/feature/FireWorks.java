package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;

import processing.core.PApplet;
import processing.core.PGraphics;

class FireWorks extends PApplet implements Comparable<FireWorks>, MoodleObject
{
    int posX = 0, posY = 0;
    int time = 0;
    int red, green, blue;
    int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6;
    int x11,y11,x21,y21,x31,y31,x41,y41,x51,y51,x61,y61;
    int alpha;
    int originalSize, size;
    String name;

    FireWorks(int x, int y, int t)
    {
        this.posX = x;
        this.posY = y;
        this.time = t;
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
        name = "fireworks";
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

    public void display(PGraphics g)
    {
        g.fill(red, green, blue, this.alpha);
        g.stroke(red, green, blue, this.alpha);
        g.ellipse(x1,y1,size,size);
        g.ellipse(x2,y2,size,size);
        g.ellipse(x3,y3,size,size);
        g.ellipse(x4,y4,size,size);
        g.ellipse(x5,y5,size,size);
        g.ellipse(x6,y6,size,size);
        g.ellipse(x11,y11,size,size);
        g.ellipse(x21,y21,size,size);
        g.ellipse(x31,y31,size,size);
        g.ellipse(x41,y41,size,size);
        g.ellipse(x51,y51,size,size);
        g.ellipse(x61,y61,size,size);
    }

    @Override
    public int compareTo(@NonNull FireWorks fireWorks)
    {
        return (this.time - fireWorks.time) ;
    }

    public void reset()
    {
        alpha = 255;
    }
}