package com.example.andreaswestberg.mangodroid_notify.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andreaswestberg.mangodroid_notify.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventDetailFragment extends Fragment {

    public final String TAG = "EventDetailFragment";

    private final static String ARG_SOURCE = "source";
    public EventDetailFragment() {
    }

    public static EventDetailFragment newInstance(String source) {
        EventDetailFragment f = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        String notification = args.getString(ARG_SOURCE);

        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        Intent intent = getActivity().getIntent();

        TextView notificationTV = (TextView) rootView.findViewById(R.id.notification);
        notificationTV.setText(notification);
        /*Uri data = getActivity().getIntent().getData();
        if (!data.equals(null)){
            String scheme = data.getScheme();
            System.out.println("Path:" + data.getPath());
            notificationTV.setText(data.toString());
        }*/
        return rootView;
    }

}
