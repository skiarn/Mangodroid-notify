package com.example.andreaswestberg.mangodroid_notify.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.example.andreaswestberg.mangodroid_notify.R;
import com.example.andreaswestberg.mangodroid_notify.adapter.EventsListAdapter;
import com.example.andreaswestberg.mangodroid_notify.model.Event;

import java.util.ArrayList;

/**
 * Created by andreaswestberg on 29/10/15.
 */
public class EventsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_CONTENT = "content";
    private AbsListView mListView;

    private ListAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    public static EventsFragment newInstance(String content) {
        EventsFragment f = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        f.setArguments(args);
        return f;
    }

    private ArrayList<Event> getRows() {
        Bundle args = getArguments();
        String content = args.getString(ARG_CONTENT);
        String[] rows = content.split("\n");
        final ArrayList<Event> list = new ArrayList<Event>();
        for (int i = 0; i < rows.length; ++i) {
            list.add(new Event(rows[i]));
        }
        return list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new EventsListAdapter(getActivity(), getRows());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            Event item = this.getRows().get(position);
            mListener.onEventSelected(item.getSource());
        }
    }

    /**
     * This interface must be implemented by activity.
     */
    public interface OnFragmentInteractionListener {
        void onEventSelected(String source);
    }

}
