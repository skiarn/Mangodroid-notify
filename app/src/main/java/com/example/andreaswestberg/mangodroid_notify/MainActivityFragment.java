package com.example.andreaswestberg.mangodroid_notify;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public final String TAG = "MainActivityFragment";
    public MainActivityFragment() {
    }

    public static MainActivityFragment newInstance(String notification) {
        MainActivityFragment f = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putString("notification", notification);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        String notification = args.getString("notification");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
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
