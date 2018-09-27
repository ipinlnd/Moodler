package com.nlnd.moodler;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import processing.android.CompatUtils;
import processing.android.PFragment;
import processing.core.PApplet;

public class WatchMoodle extends AppCompatActivity
{
    Context context;
    Uri file;
    private static Boolean render;
    Boolean loaded;
    private static int duration;
    private Uri musicFile;
    private static String[] config;
    private static MediaPlayer mplayer;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("file", file);
        outState.putBoolean("render", render);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Uri ownFile;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout frame = new FrameLayout(this);
        context = this;

        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (savedInstanceState == null)
        {
            file = Objects.requireNonNull(getIntent().getExtras()).getParcelable("file");
            render = getIntent().getExtras().getBoolean("render");
        }
        else
        {
            file = savedInstanceState.getParcelable("file");
            render = savedInstanceState.getBoolean("render");
        }

        ownFile = file;
        try
        {
            loadFile(new File(ownFile.getPath()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mplayer = new MediaPlayer();
        mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try
        {
            mplayer.setDataSource(this, musicFile);
            mplayer.prepare();
            duration = mplayer.getDuration();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PApplet sketch = new WatchMoodleSketch();
        PFragment fragment = new PFragment(sketch);
        fragment.setView(frame, this);
    }

    private void loadFile(File file) throws Exception
    {
        List<String> ar = new ArrayList<>();
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        DataOutputStream dos;
        String newSong = (getFilesDir().toString() + "/tempSong.mp3");
        String line;

        while (true)
        {
            line = dis.readLine();
            ar.add(line);
            if (line.equals("end"))
                break;
        }
        config = new String[ar.size()];
        config = ar.toArray(config);

        byte[] b = new byte[dis.available()];
        dos = new DataOutputStream(new FileOutputStream(newSong));
        dis.read(b);
        dos.write(b);

        musicFile = Uri.fromFile(new File(newSong));
        loaded = true;
    }

    public static void stopMusic()
    {
        mplayer.stop();
    }

    public static int getCurrentPosition()
    {
        return mplayer.getCurrentPosition();
    }

    public static void pauseMusic()
    {
        mplayer.pause();
    }

    public static void startMusic()
    {
        mplayer.start();
    }

    public static void musicSeekTo(int a)
    {
        mplayer.seekTo(a);
    }

    public static void mute()
    {
        mplayer.setVolume(0f, 0f);
    }

    public static Boolean getRender()
    {
        return render;
    }

    public static String[] getConfig()
    {
        return config;
    }

    public static MediaPlayer getMplayer()
    {
        return mplayer;
    }

    public static int getDuration()
    {
        return duration;
    }
}
