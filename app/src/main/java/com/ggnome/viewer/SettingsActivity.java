package com.ggnome.viewer;

import android.os.Bundle;
import android.view.MenuItem;

import com.ggnome.viewer.databinding.ActivitySettingsBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
