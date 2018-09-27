package com.nlnd.moodler;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MainPageMoodlesListView extends ArrayAdapter<Moodle>
{
    Activity context;
    List<Moodle> moodles;

    public MainPageMoodlesListView(@NonNull Context context, int resource, @NonNull List<Moodle> objects)
    {
        super(context, resource, objects);
        this.context = (Activity) context;
        moodles = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.main_page_list_view, null, true);

        TextView caption = rowView.findViewById(R.id.mainPageMoodleCaption);
        TextView artist = rowView.findViewById(R.id.mainPageArtistName);

        caption.setText(moodles.get(position).getName());
        artist.setText(moodles.get(position).getArtist());

        return rowView;
    }
}