package com.xuhaoran.mtbf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MTBFActivity extends Activity {
	
	private LinearLayout mLayout;
	private Handler mHandler;
	private static final int BG_CLR_CHANGE_TIME = 1000;
	private static final int SPK_RCV_OPEN_TIME = 30000;
	private static final int VBT_ON_OFF_TIME = 10;//3000;
	private static final int EVENT_CHANGE_BG_CLR = 1;
	private static final int EVENT_SPK_RCV_OPEN = 2;
	private int mBGColorFlag;
	private static final int FLAG_MAX_VALUE = 40;
	private Ringtone mRingTone;
	private Vibrator mVibrator;
	
	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 
		setContentView(R.layout.activity_main);
		
		mLayout = (LinearLayout) findViewById(R.id.layout);
		
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == EVENT_CHANGE_BG_CLR) {
					if (mBGColorFlag >= FLAG_MAX_VALUE) {
						mBGColorFlag = 0;
					}
					changeBGColor(mBGColorFlag);
					mBGColorFlag++;					
				} else if (msg.what == EVENT_SPK_RCV_OPEN) {
					if (mRingTone.isPlaying()) {
						if (mRingTone.getStreamType() == AudioManager.STREAM_SYSTEM) {
							playFromReceiver(mRingTone);
						} else if (mRingTone.getStreamType() == AudioManager.STREAM_VOICE_CALL) {
							playFromSpeaker(mRingTone);
						}
						mHandler.sendEmptyMessageDelayed(EVENT_SPK_RCV_OPEN, SPK_RCV_OPEN_TIME);
					}
				}
			}

		};

		mRingTone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);
		
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}
	
	private void playFromSpeaker(Ringtone r) {
		if (r == null) return; 
		r.setStreamType(AudioManager.STREAM_SYSTEM);
		if(!r.isPlaying()){
			r.play();
		}
	}

	private void playFromReceiver(Ringtone r) {
		if (r == null) return; 
		r.setStreamType(AudioManager.STREAM_VOICE_CALL);
		if(!r.isPlaying()){
			r.play();
		}
	}
	
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);
		playFromSpeaker(mRingTone);
		mVibrator.vibrate(new long[] { VBT_ON_OFF_TIME, VBT_ON_OFF_TIME}, 0);
		mHandler.sendEmptyMessageDelayed(EVENT_SPK_RCV_OPEN, SPK_RCV_OPEN_TIME);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();	
		mHandler.removeCallbacksAndMessages(mHandler);
		mRingTone.stop();
		mVibrator.cancel();
	}

	private void changeBGColor(int num) {
        switch(num % 4) {   
        case 0:      
            mLayout.setBackgroundColor(Color.RED);
            mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);   
            break;   
        case 1:   
            mLayout.setBackgroundColor(Color.GREEN);   
            mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);     
            break;   
        case 2:   
            mLayout.setBackgroundColor(Color.BLUE);  
            mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);    
            break;   
        case 3:   
            mLayout.setBackgroundColor(Color.WHITE);  
            mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);   
            break;   
        default:
            mLayout.setBackgroundColor(Color.BLACK);  
            mHandler.sendEmptyMessageDelayed(EVENT_CHANGE_BG_CLR, BG_CLR_CHANGE_TIME);        	
            break; 
        }   
    }

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		mVibrator.vibrate(VBT_ON_OFF_TIME);
		return super.onMenuOpened(featureId, menu);
	}
}
