package com.nlnd.moodler.feature;

import processing.core.PGraphics;

public abstract class MoodleObject
{
    public int posX, posY;
    public int red, green, blue;
    public int alpha;
    public int fadeSpeed;
    public int size, originalSize;
    public int time;
    public float pressure;
    public String name;

    public abstract void reset();
    public abstract void update();
    public abstract void display(PGraphics p);
}
