package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PGraphics;

class FireWorks extends MoodleObject implements Comparable<FireWorks>
{
    int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6;
    int x11,y11,x21,y21,x31,y31,x41,y41,x51,y51,x61,y61;

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
        y1 += (int)new Random().nextInt(10);
        y2 += (int)new Random().nextInt(10); x2 += (int)new Random().nextInt(10);
        y3 += (int)new Random().nextInt(10); x3 -= (int)new Random().nextInt(10);
        y4 -= (int)new Random().nextInt(10);
        y5 -= (int)new Random().nextInt(10); x5 -= (int)new Random().nextInt(10);
        y6 -= (int)new Random().nextInt(10); x6 += (int)new Random().nextInt(10);
        y11 += (int)new Random().nextInt(10); x11 += (int)new Random().nextInt(10);
        y21 += (int)new Random().nextInt(10); x21 += (int)new Random().nextInt(10);
        y31 += (int)new Random().nextInt(10); x31 -= (int)new Random().nextInt(10);
        y41 -= (int)new Random().nextInt(10); x41 -= (int)new Random().nextInt(10);
        y51 -= (int)new Random().nextInt(10); x51 -= (int)new Random().nextInt(10);
        y61 -= (int)new Random().nextInt(10); x61 += (int)new Random().nextInt(10);
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