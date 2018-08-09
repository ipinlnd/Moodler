package com.nlnd.moodler.feature;

import processing.core.PGraphics;

interface MoodleObject
{
    int posX = 0, posY = 0;
    int red = 0, green = 0, blue = 0;
    int alpha = 0;
    int fadeSpeed = 0;
    float size = 0, originalSize = 0;
    int time = 0;
    String name = null;

    void reset();
    void update();
    void display(PGraphics p);
}