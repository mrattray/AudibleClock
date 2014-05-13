package com.mrattray.audibleclock;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	private final int MINUTES_MULTIPLIER = 5;
	private final int MILLISECOND_TO_MINUTE_MULTIPLIER = 60000;
	private final int MINIMUM_MINUTES = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final SeekBar intervalSeek = (SeekBar)findViewById(R.id.intervalSlider);
	    final TextView intervalText = (TextView)findViewById(R.id.intervalAmount);
	    final Button startButton = (Button)findViewById(R.id.startButton);
	    intervalText.setText(convertProgressToMinutes(intervalSeek.getProgress()));
	    intervalSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				intervalText.setText(convertProgressToMinutes(progress));
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	    startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Task(intervalSeek.getProgress() * MINUTES_MULTIPLIER)).start();
			}
		});
	}
    private String convertProgressToMinutes(int progress) {
    	if (progress < MINIMUM_MINUTES) {
    		return getString(R.string.audible) + " " + Integer.toString(MINIMUM_MINUTES) + " " + getString(R.string.minutes);
    	}
    	else {
    		return getString(R.string.audible) + " " + Integer.toString(progress * MINUTES_MULTIPLIER) + " " + getString(R.string.minutes);
    	}
    }
    
    class Task implements Runnable {
    	private int _min;
    	public Task(int minutes)
    	{
        	if (_min < MINIMUM_MINUTES) {
        		_min = MINIMUM_MINUTES;
        	}
        	else {
        		_min = minutes;
        	}
    	}
      	@Override
        public void run() {
      		int count = 0;
      		while(true)
      		{
	            try {
	                Thread.sleep(_min * MILLISECOND_TO_MINUTE_MULTIPLIER);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	            count++;
            	r.play();
      		}
        }
    }
}
