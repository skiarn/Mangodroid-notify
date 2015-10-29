package com.example.andreaswestberg.mangodroid_notify.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.example.andreaswestberg.mangodroid_notify.R;
import com.example.andreaswestberg.mangodroid_notify.fragment.EventDetailFragment;

/**
 * Created by andreaswestberg on 29/10/15.
 */
public class EventDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_body, EventDetailFragment.newInstance(source)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
