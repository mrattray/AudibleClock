package com.mrattray.audibleclock;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	private final int MINUTES_MULTIPLIER = 5;
	private final int MILLISECOND_TO_SECOND_MULTIPLIER = 1000;
	private final int SECOND_TO_MINUTE_MULTIPLIER = 60;
	private final int MINIMUM_MINUTES = 1;
	private boolean _isRunning = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		final SeekBar intervalSeek = (SeekBar)findViewById(R.id.intervalSlider);
	    final TextView intervalText = (TextView)findViewById(R.id.intervalAmount);
	    final Button startButton = (Button)findViewById(R.id.startButton);
	    final TextView intervalCounter = (TextView)findViewById(R.id.intervalCounter);
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
				if (!_isRunning) {
					new Thread(new Task(intervalSeek.getProgress() * MINUTES_MULTIPLIER, startButton, intervalCounter)).start();
				}
				else {
					_isRunning = false;
					startButton.setText(R.string.start);
					intervalCounter.setText("");
				}
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
    	private int _staticMin;
    	private Button _statusButton;
    	private TextView _counterText;
    	private int _counter = 0;
    	private int _toneCount = 0;
    	public Task(int minutes, Button statusButton, TextView counterText)
    	{
    		_counterText = counterText;
    		_statusButton = statusButton;
        	if (minutes < MINIMUM_MINUTES) {
        		_min = MINIMUM_MINUTES;
        	}
        	else {
        		_min = minutes;
        	}
    		_staticMin = _min;
    	}
      	@Override
        public void run() {
      		_isRunning = true;
      		while(_isRunning)
      		{
      			int sleepInterval = MILLISECOND_TO_SECOND_MULTIPLIER;
      			_min = _staticMin * MILLISECOND_TO_SECOND_MULTIPLIER * SECOND_TO_MINUTE_MULTIPLIER;
      			_counter = 0;
      			while (_min > 0) {
      				if (!_isRunning) {
      					break;
      				}
		            try {
		                Thread.sleep(sleepInterval);
		                _min = _min - sleepInterval;
		                _counter = _counter + sleepInterval;
		                runOnUiThread(new Runnable()
		                {
							@Override
							public void run() {
								if (_isRunning) {
									_statusButton.setText(Integer.toString(_counter / MILLISECOND_TO_SECOND_MULTIPLIER));		
								}
							}
						});
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
      			}
      			if (_isRunning) {
		        	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		            _toneCount++;
	            	r.play();
	            	runOnUiThread(new Runnable()
	                {
						@Override
						public void run() {
							_counterText.setText(Integer.toString(_toneCount) + " " + getString(R.string.tone_count));		
						}
					});
      			}
      		}
        }
    }
}
