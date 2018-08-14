package com.nlnd.moodler;

import processing.core.PGraphics;

public class Sticks extends MoodleObject implements Comparable<Sticks>
{
    Sticks(int posX, int posY, int fadeSpeed, int originalSize, int time)
    {
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
        name = "stick";
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

    public void display(PGraphics p)
    {
        p.stroke(this.red, this.green, this.blue, this.alpha);
        p.strokeWeight(this.size);
        p.line(posX, posY, posX, p.height - posY);
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