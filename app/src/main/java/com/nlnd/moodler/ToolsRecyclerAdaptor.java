package com.nlnd.moodler;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ToolsRecyclerAdaptor extends RecyclerView.Adapter<ToolsRecyclerAdaptor.CategoryViewHolder>
{
    private List<MoodleMethod> methods;
    private int selected = 0;
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_list, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position)
    {
        holder.text.setText(methods.get(position).getName());
        if (selected == position)
            holder.text.setTextColor(Color.RED);
        else
            holder.text.setTextColor(Color.BLACK);
        holder.image.setImageResource(methods.get(position).getId());
        holder.image.setAdjustViewBounds(true);
        holder.image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (methods.get(position).getName().equals("Firework"))
                    CreateMoodleSketch.method = CreateMoodleSketch.Method.firework;
                if (methods.get(position).getName().equals("Snow"))
                    CreateMoodleSketch.method = CreateMoodleSketch.Method.snow;
                if (methods.get(position).getName().equals("Stick"))
                    CreateMoodleSketch.method = CreateMoodleSketch.Method.sticks;
                if (methods.get(position).getName().equals("Ring"))
                    CreateMoodleSketch.method = CreateMoodleSketch.Method.rings;
                selected = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return methods.size();
    }

    public ToolsRecyclerAdaptor(List<MoodleMethod> services)
    {
        this.methods = services;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{

        protected ImageView image;
        protected TextView text;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_id);
            text = itemView.findViewById(R.id.text_id);
        }
    }
}