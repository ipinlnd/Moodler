package com.nlnd.moodler;
import android.graphics.Bitmap;

import java.io.File;

class Moodle
{
    private Bitmap image;
    private String name;
    private String artist;
    private File file;

    Moodle(String name)
    {
        this.name = name;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
