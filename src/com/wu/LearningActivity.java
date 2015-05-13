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


public class LearningActivity extends Activity  {
	private static int mode = 0;// 1 是 db4小波 2 是haar小波 3是 FFT
	Button walkingButton;
	Button runningButton;
	Button idleButton;
	Button upstairsButton;
	Button downstairsButton;
	Button buckButton;
	Button fftButton;
	Button haarButton;
	Button db4Button;
	TextView learningStateTV;
	
	learnBackReceiver receiver;
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.learn);
        //设置我的按钮
        walkingButton = (Button)findViewById(R.id.wallinglearning);
        runningButton = (Button)findViewById(R.id.runninglearning);
        idleButton = (Button)findViewById(R.id.idle);
        upstairsButton = (Button)findViewById(R.id.upstairslearning);
        downstairsButton = (Button)findViewById(R.id.downstairslearning);
        buckButton = (Button)findViewById(R.id.learnback);
        fftButton = (Button)findViewById(R.id.fftLearn);
        db4Button = (Button)findViewById(R.id.dbLearn);
        haarButton = (Button)findViewById(R.id.haarLearn);
        learningStateTV = (TextView)findViewById(R.id.learnstate);
        
        //过滤器
        filter = new IntentFilter("learned");
        receiver = new learnBackReceiver();
        registerReceiver(receiver, filter);
        
        //回到探测模式
        buckButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				finish();
			}
        	
        });
        
        //为db4模式学习
        db4Button.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mode = 1;
				Toast.makeText(getApplicationContext(), "DB 4 Learning Mode", Toast.LENGTH_SHORT).show();
			}
        	
        });
        
        //为haar模式学习
        haarButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mode = 2 ;
				Toast.makeText(getApplicationContext(), "Haar Learning Mode", Toast.LENGTH_SHORT).show();

			}
        	
        });
        
        //FFT学习模式
        fftButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				mode = 3;
				Toast.makeText(getApplicationContext(), "FFT Learning Mode", Toast.LENGTH_SHORT).show();

			}
        	
        });
        
        //开始walking学习
        walkingButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startLearning("walking");//需要传入一个变量 来分辨你在学习什么
			}
        });
        
        //开始running学习
		runningButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startLearning("running");
			}
		});
		
		//开始sitting学习
		idleButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startLearning("idle");
			}
		});
		
		//开始upstairs学习
		upstairsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startLearning("up stairs");
			}
		});
		
		//开始downstairs学习
		downstairsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startLearning("down stairs");
			}
		});

	}
	
	private void startLearning(String type){
		String modeStr = null;
		if(mode == 0)
		{
			Toast.makeText(getApplicationContext(), "You need choice a mode firstly", Toast.LENGTH_SHORT).show();
		}
		else
		{
			if(mode == 1){
				 modeStr = "DB4";
			}
			else if(mode == 2){
				 modeStr = "HAAR";
			}
			else{
				 modeStr = "FFT";
			}
			final Handler handler = new Handler();
			final long delay = 2 *1000;
			Toast.makeText(getApplicationContext(), "After this message it will start", Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(this,LearningService.class);
			intent.putExtra("type", type);
			intent.putExtra("mode", mode);
			if(mode != 0){
				handler.postDelayed(new Runnable(){//在这里工作
					public void run() {
						startService(intent);
					}
				}, delay);
				learningStateTV.setText(modeStr+" "+type+" Learning!!! Please waitting");
			}
			
		}
	}
	
	private class learnBackReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String result = bundle.getString("state");
			if(result != null)
			{
				learningStateTV.setText("Learning Finished");
			}
			else
			{
				learningStateTV.setText("Something is wrong in learning processing");
				Log.e("res", result);
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
