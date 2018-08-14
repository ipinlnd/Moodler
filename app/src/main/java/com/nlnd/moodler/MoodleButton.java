package com.nlnd.moodler;

import processing.core.PApplet;
import processing.core.PImage;

class MoodleButton
{
    private int x, y;
    private int width, height;
    private PImage buttonImage;

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
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + height;
    }

    void show(PApplet p)
    {
        p.image(this.buttonImage, x, y, this.width, this.height);
    }
}