package com.example.andreaswestberg.mangodroid_notify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.andreaswestberg.mangodroid_notify.R;
import com.example.andreaswestberg.mangodroid_notify.model.Event;

import java.util.List;

/**
 * Created by andreaswestberg on 29/10/15.
 */
public class EventsListAdapter extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public EventsListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView firstline;
        TextView secondline;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Event item = (Event)getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if(useList){
                viewToUse = mInflater.inflate(R.layout.events_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.events_list_item, null);
            }

            holder = new ViewHolder();
            holder.firstline = (TextView)viewToUse.findViewById(R.id.firstLine);
            holder.secondline = (TextView)viewToUse.findViewById(R.id.secondLine);
            viewToUse.setTag(holder);


        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.firstline.setText(item.getSource());
        holder.secondline.setText(item.getSource());

        return viewToUse;
    }
}
