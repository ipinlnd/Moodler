package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;

import java.util.Random;

import processing.core.PGraphics;

class FireWorks extends MoodleObject implements Comparable<FireWorks>
{
    private int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6;
    private int sx1,sy1,sx2,sy2,sx3,sy3,sx4,sy4,sx5,sy5,sx6,sy6;
    private int x11,y11,x21,y21,x31,y31,x41,y41,x51,y51,x61,y61;
    private int sx11,sy11,sx21,sy21,sx31,sy31,sx41,sy41,sx51,sy51,sx61,sy61;
    FireWorks(int x, int y, int t)
    {
        this.posX = x;
        this.posY = y;
        this.time = t;
        x1 = x2 = x3 = x4 = x5 = x6 = x;
        x11 = x21 = x31 = x41 = x51 = x61 = x;
        y1 = y2 = y3 = y4 = y5 = y6 = y;
        y11 = y21 = y31 = y41 = y51 = y61 = y;
        Random r = new Random();
        sy1 = r.nextInt(5); sx1 = r.nextInt(5);
        sy2 = r.nextInt(5); sx2 = r.nextInt(5);
        sy3 = r.nextInt(5); sx3 = r.nextInt(5);
        sy4 = r.nextInt(5); sx4 = r.nextInt(5);
        sy5 = r.nextInt(5); sx5 = r.nextInt(5);
        sy6 = r.nextInt(5); sx6 = r.nextInt(5);
        sy11 = r.nextInt(5); sx11 = r.nextInt(5);
        sy21 = r.nextInt(5); sx21 = r.nextInt(5);
        sy31 = r.nextInt(5); sx31 = r.nextInt(5);
        sy41 = r.nextInt(5); sx41 = r.nextInt(5);
        sy51 = r.nextInt(5); sx51 = r.nextInt(5);
        sy61 = r.nextInt(5); sx61 = r.nextInt(5);

        alpha = 255;
        this.red = x % 255;
        this.blue = y % 255;
        this.green = (x * y) % 255;
        this.size = 5;
        name = "fireworks";
    }

    @Override
    public void update()
    {
        this.alpha -=2;
        size -= 1;
        y1 += sy1; x1 += sx1;
        y2 += sy2; x2 += sx2;
        y3 += sy3; x3 -= sx3;
        y4 -= sy4; x4 -= sx4;
        y5 -= sy5; x5 -= sx5;
        y6 -= sy6; x6 += sx6;
        y11 += sy11; x11 += sx11;
        y21 += sy21; x21 += sx21;
        y31 += sy31; x31 -= sx31;
        y41 -= sy41; x41 -= sx41;
        y51 -= sy51; x51 -= sx51;
        y61 -= sy61; x61 += sx61;
    }

    @Override
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

    @Override
    public void reset()
    {
        alpha = 255;
        size = 5;

        x1 = x2 = x3 = x4 = x5 = x6 = this.posX;
        x11 = x21 = x31 = x41 = x51 = x61 = this.posX;
        y1 = y2 = y3 = y4 = y5 = y6 = this.posY;
        y11 = y21 = y31 = y41 = y51 = y61 = this.posY;
    }
}