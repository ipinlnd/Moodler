package com.nlnd.moodler;

import processing.core.PGraphics;

public abstract class MoodleObject
{
    public int posX, posY;
    int red, green, blue;
    public int alpha;
    int fadeSpeed;
    int size, originalSize;
    public int time;
    public float pressure;
    public String name;

    public abstract void reset();
    public abstract void update();
    public abstract void display(PGraphics p);
}
