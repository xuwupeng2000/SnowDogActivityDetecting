package com.wu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.four.R;

public class MainActivity extends Activity {
	
	public static int mode = 0; // 1 是 db4小波 2 是haar小波 3是 FFT
	Button startButton;
	Button stopButton;
	Button exitButton;
	Button statisticsButton;
	Button fftButton;
	Button db4Button;
	Button haarButton;
	Button learnButton;
	TextView stateTextView;
	TextView activityTextView;
	ActivityReceiver receiver;
	IntentFilter filter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //setup my Buttons and TextViews 
        startButton	 		= (Button)findViewById(R.id.start);
        stopButton 			= (Button)findViewById(R.id.stop);
        exitButton 			= (Button)findViewById(R.id.exit);
        statisticsButton 	= (Button)findViewById(R.id.info);
        fftButton 			= (Button)findViewById(R.id.fft);
        learnButton 		= (Button)findViewById(R.id.learn);
        haarButton 			= (Button)findViewById(R.id.haarwt);
        db4Button 			= (Button)findViewById(R.id.dbwt);
        stateTextView		= (TextView)findViewById(R.id.statetv);
        activityTextView	= (TextView)findViewById(R.id.acttv);

        
        filter = new IntentFilter("result");
        receiver = new ActivityReceiver();
        registerReceiver(receiver, filter);
        
        //开始探测
        startButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startDetecting();
			}
		});
        
        //结束探测
        stopButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				stopDetecting();
			}
		});
        
        //退出程序
        exitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
        
        //查看统计数据
        statisticsButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent goTJ = new Intent(MainActivity.this,tjActivity.class);
				startActivity(goTJ);
			}
        	
        });
        
        //切换到FFT模式
        fftButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mode = 3;
			}
        	
        });
        
        //切换到haar模式
        haarButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mode =2;
			}
        	
        });
        
        //切换到db4模式
        db4Button.setOnClickListener(new OnClickListener(){
        	
        	public void onClick(View v) {
				mode =1;
			}
        });
        
        //进入学习模式
        learnButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,LearningActivity.class);
				startActivity(intent);
			}
        	
        });
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void startDetecting(){
		final Handler handler = new Handler();
		final long delay = 2 *1000;
		if(mode == 0){
			Toast.makeText(getApplicationContext(), "You need choice a detecting mode firstly", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(getApplicationContext(), "After this message it will start", Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(this,com.wu.DetectingService.class);
			intent.putExtra("mode", mode);
			handler.postDelayed(new Runnable(){//在这里工作
				public void run() {
					startService(intent);
				}
			}, delay);
			stateTextView.setText("Detecting");
			}
	}
	
	private void stopDetecting(){
			stopService(new Intent(this,DetectingService.class));//问题
			stateTextView.setText("Stop Detecting");
	}
	
	private class ActivityReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String result = bundle.getString("activity");
			Log.e("res", "这是mainactivity里面的结果 "+result);
			activityTextView.setText(result);
		}
		
	}
    
    
}