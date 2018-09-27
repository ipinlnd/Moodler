package com.nlnd.moodler;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

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

public class CreateMoodle extends AppCompatActivity
{
    private static View layout;
    private static PopupWindow pw;
    private List<MoodleMethod> buttons;
    Context context;
    private Uri file;
    private static boolean continueMoodle;
    private volatile boolean loaded;
    private static Uri musicFile;
    private static MediaPlayer mplayer;
    private static int duration;
    private PApplet sketch;
    private static String[] config;

    private void makeList()
    {
        buttons = new ArrayList<>();
        buttons.add(new MoodleMethod("Snow", R.drawable.snow_method));
        buttons.add(new MoodleMethod("Stick", R.drawable.sticks_method));
        buttons.add(new MoodleMethod("Ring", R.drawable.rings_method));
        buttons.add(new MoodleMethod("Firework", R.drawable.fireworks_method));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("file", file);
        outState.putBoolean("continue", continueMoodle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Uri ownFile;
        FrameLayout frame = new FrameLayout(this);
        context = this;
        makeList();
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (savedInstanceState == null)
        {
            file = Objects.requireNonNull(getIntent().getExtras()).getParcelable("file");
            continueMoodle = getIntent().getExtras().getBoolean("continue");
        }
        else
        {
            file = savedInstanceState.getParcelable("file");
            continueMoodle = savedInstanceState.getBoolean("continue");
        }

        if (continueMoodle)
        {
            ownFile = file;
            try
            {
                loadFile(new File(ownFile.getPath()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            while(!loaded);
        }
        else
            musicFile = file;
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        layout = inflater.inflate(R.layout.toolbox_menu, null);
        DisplayMetrics density = context.getResources().getDisplayMetrics();
        pw = new PopupWindow(layout, (int) (density.widthPixels * .8), (int) (density.heightPixels * 0.7), true);
        layout.findViewById(R.id.toolbox_close_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pw.dismiss();
            }
        });
        pw.setOutsideTouchable(false);

        RecyclerView rv = layout.findViewById(R.id.toolbox_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        ToolsRecyclerAdaptor adapter = new ToolsRecyclerAdaptor(buttons);
        rv.setAdapter(adapter);

        Button saveButton = layout.findViewById(R.id.toolbox_save_button);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CreateMoodleSketch.saving = true;
            }
        });

        sketch = new CreateMoodleSketch();
        PFragment fragment = new PFragment(sketch);
        fragment.setView(frame, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (sketch != null)
        {
            sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (sketch != null)
        {
            sketch.onNewIntent(intent);
        }
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

    public static void setView()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        }, 100);
    }

    public static void unSetView()
    {
        pw.dismiss();
    }

    public static Boolean getContinueMoodle()
    {
        return continueMoodle;
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

    public static Uri getMusicFile()
    {
        return musicFile;
    }
}
