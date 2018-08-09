package com.nlnd.moodler.feature;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Sticks extends PApplet implements Comparable<Sticks>,MoodleObject
{
    int posX, posY;
    int red, green, blue;
    int alpha;
    int fadeSpeed;
    float size, originalSize;
    int time;
    String name;

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