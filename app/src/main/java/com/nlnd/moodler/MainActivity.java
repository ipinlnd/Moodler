package com.nlnd.moodler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;

public class MainActivity extends AppCompatActivity
{
    List<Moodle> moodles;
    static Context context;
    public static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setView();
    }

    private void setView()
    {
        ListView lv = findViewById(R.id.main_page_moodles_list_view);
        String path = this.getFilesDir().toString() + "/";
        File directory = new File(path);
        File[] files = directory.listFiles();
        moodles = new ArrayList<>();

        for (File file : files)
        {
            if (file.getName().charAt(file.getName().length() - 1) == 'v' &&
                    file.getName().charAt(file.getName().length() - 2) == '3' &&
                    file.getName().charAt(file.getName().length() - 3) == 'v')
            {
                moodles.add(new Moodle(file.getName()));
                moodles.get(moodles.size() - 1).setFile(file);
            }
        }

        MainPageMoodlesListView mpmlv = new MainPageMoodlesListView(this, R.layout.main_page_list_view, moodles);
        lv.setAdapter(mpmlv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l)
            {
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        menuItemClick(menuItem, i);

                        return true;
                    }
                });
                inflater.inflate(R.menu.moodle_popup, popup.getMenu());
                popup.show();
            }
        });
    }

    private void menuItemClick(MenuItem menuItem, int i)
    {
        switch (menuItem.getItemId())
        {
            case R.id.popup_menu_watch :
                watchMoodle(i);
                break;
            case R.id.popup_menu_delete:
                deleteMoodle(i);
                break;
            case R.id.popup_menu_render:
                renderMoodle(i);
                break;
            case R.id.popup_menu_share:
                shareMoodle(i);
                break;
            case R.id.popup_menu_continue:
                continueMoodle(i);
                break;
        }
    }

    private void continueMoodle(int i)
    {
        Intent intent = new Intent(this, CreateMoodle.class);
        fileName = getFileName(Uri.fromFile(moodles.get(i).getFile()));
        intent.putExtra("file", Uri.fromFile(moodles.get(i).getFile()));
        intent.putExtra("continue", true);
        startActivity(intent);
    }

    private void shareMoodle(int i)
    {
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                "com.nlnd.moodler", moodles.get(i).getFile());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent, "Share File"));
    }

    private void renderMoodle(int i)
    {
        Intent intent = new Intent(this, WatchMoodle.class);
        intent.putExtra("file", Uri.fromFile(moodles.get(i).getFile()));
        intent.putExtra("render", true);
        startActivity(intent);
    }

    private void deleteMoodle(int i)
    {
        boolean result = moodles.get(i).getFile().delete();
        if (result)
        {
            moodles.clear();
            setView();
        }
    }

    private void watchMoodle(int i)
    {
        Intent intent = new Intent(this, WatchMoodle.class);
        intent.putExtra("file", Uri.fromFile(moodles.get(i).getFile()));
        intent.putExtra("render", false);
        startActivity(intent);
    }

    public void addNewMoodleClicked(View v)
    {
        Intent getMusic = new Intent(ACTION_OPEN_DOCUMENT);
        getMusic.addCategory(Intent.CATEGORY_OPENABLE);
        getMusic.setType("audio/*");
        startActivityForResult(getMusic, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == Activity.RESULT_OK)
        {
            assert data != null;
            Uri mFile = data.getData();
            assert mFile != null;
            fileName = getFileName(mFile);
            Intent intent = new Intent(this, CreateMoodle.class);
            intent.putExtra("file", mFile);
            intent.putExtra("continue", false);
            startActivity(intent);
        }
    }

    public static String getFileName(Uri uri)
    {
        String result = null;

        if (uri.getScheme().equals("content"))
        {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst())
            {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        }
        else
        {
            result = uri.getPath().split("/")[uri.getPath().split("/").length - 1];
            result = result.substring(0, result.length() - 4);
        }

        return result;
    }
}
