package com.nlnd.moodler.feature;

import android.support.annotation.NonNull;

import processing.core.PGraphics;

public class Ring extends MoodleObject implements Comparable<Ring>
{
    private int fill = 0;
    Ring(int posX, int posY, int fadeSpeed, int originalSize, int time, float pressure)
    {
        this.posX = posX;
        this.posY = posY;
        this.fadeSpeed = fadeSpeed;
        this.time = time;
        this.red = posX % 255;
        this.originalSize = (int)(originalSize * pressure);
        this.blue = posY % 255;
        this.green = (posX * posY) % 255;
        this.size = originalSize;
        this.alpha = 255;
        this.name = "ring";
        this.pressure = pressure;

        if (this.pressure <= 0.1)
            this.fill = 0;
        else if (this.pressure <= .2)
            this.fill = 10;
        else if (this.pressure <= .3)
            this.fill = 50;
        else if (this.pressure <= .4)
            this.fill = 120;
        else
            this.fill = 200;
        System.out.println(this.fill);
    }

    public void reset()
    {
        this.alpha = 255;
        this.size = 10;
    }

    public void update()
    {
        this.size += 10;
        this.alpha -= this.fadeSpeed;
    }

    public void display(PGraphics p)
    {
        p.stroke(this.red, this.blue, this.green, this.alpha);
        p.strokeWeight(10 * this.pressure);
        p.fill(this.red, this.blue, this.green, this.fill);
        p.ellipse(this.posX, this.posY, this.size, this.size);
    }

    @Override
    public int hashCode() {
        return this.time;
    }

    @Override
    public int compareTo(@NonNull Ring o) {
        return (this.time - o.time) ;
    }
}