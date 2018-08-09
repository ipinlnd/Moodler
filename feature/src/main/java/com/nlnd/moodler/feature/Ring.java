package com.nlnd.moodler.feature;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Ring extends MoodleObject implements Comparable<Ring>
{
    Ring(int posX, int posY, int fadeSpeed, float originalSize, int time)
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
        this.name = "ring";
    }

    public void reset()
    {
        alpha = 255;
        size = originalSize;
    }

    public void update()
    {
        size += 10;
        alpha -= 1;
    }

    public void display(PGraphics p)
    {
        p.stroke(red, blue, green, alpha);
        p.strokeWeight(2);
        p.fill(255,1);
        p.ellipse(posX, posY, size, size);
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