package com.wu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import com.wu.fft.Complex;
import com.wu.fft.FFT;
import com.wu.wavelet.DaubechiesTranform;
import com.wu.wavelet.HaarTransform;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LearningService extends Service {
	//重要变量
	private int LEARNING_MODE = 0; // 1 是 db4小波 2 是haar小波 3是 FFT
	private final int SAMPLE_NUMBER = 256; 
	private String ACT_TYPE = "unknown";
	private final int CS = 4;//一次学习采集几个set
	//计数器
	private static double[] learnSamples = new double[256];
	private static int learnCount = 0;
	private static int quitCount = 0;
	private static int otherCount = 0;
	//传感器
	private SensorManager sensorManager;
	private SensorEventListener sensorListener;
	private DbAdapter dba;
	//写入文件的变量和常量
	private PrintWriter pw;
	private File LEARN_ANALYSIS_DATA_FILE=new File("/sdcard/Learning_Analysis_Data.txt");
	private File LEARN_SAMPLES_REC_FILE=new File("/sdcard/Learning_Raw_Data.txt");
	//4个属性
	private double learnMean;
	private double learnMax;
	private double[] learnED;
	//判断是否停止
	private static boolean RUN = false ;
	//计算频率的定时器
	private static long start;
	private static long end;
	private static long passTime;
	
	//学习主过程，包括采样
	private void processSample(double[] sensorValues) {
		double ampl = Math.sqrt( (double)sensorValues[0]*sensorValues[0] +
				 (double)sensorValues[1]*sensorValues[1] +
				 (double)sensorValues[2]*sensorValues[2] );
		//计算出我要的单值
		ampl = ampl-SensorManager.STANDARD_GRAVITY;
		
		//坐满了次数退出
		if(quitCount == CS)//在这里控制收集几组数据
		{
			end = System.currentTimeMillis();
			passTime = end - start;
			Log.i("time", Long.toString(passTime));
			Log.i("time", "结束只应该进行一次");//调试
			setResult("ok");
			Log.i("time", "结果被发送了");
			this.onDestroy();//结束服务自己
		}
		
		//如果没有填满256个就继续填
		else if(RUN && learnCount<SAMPLE_NUMBER)
		{
			if(otherCount==0)
			{
				start = System.currentTimeMillis();

			}
			
			learnSamples[learnCount]=ampl;
			learnCount++;
			Log.i("count", Integer.toString(learnCount));//调试
			otherCount++;
			
		}
		
		//如果填满了就处理，并把计数器清零
		else if(quitCount < CS && learnCount==SAMPLE_NUMBER)
		{
			learnCount=0;
			//计算数值 并放入数据库
			learnFrom(LEARNING_MODE,ACT_TYPE);
			quitCount++;
		}
		else
		{
			this.onDestroy();
			Log.i("pro", "something wrong!!!!");//调试
		}
	}
	
	//计算三个值并把他们放入数据库
	private void learnFrom(int mode, String type)
	{
		learnMax = CalculateMax(learnSamples);
		learnMean = CalculateMean(learnSamples);
		learnED = CalculateEnergyDistribution(learnSamples);
		
		
		Log.i("pro", "进入learnFrom方法计算三个值完成了!");//调试
		dba = new DbAdapter(getBaseContext());
		dba.open();
		dba.addLearnSet(mode, type, learnMax, learnMean, learnED);
		Log.i("pro", "传给learning service 的"+LEARNING_MODE+"值" +ACT_TYPE);
		dba.close();
	}
	
	private double[] CalculateEnergyDistribution(double[] samples) {
		double[] ex = new double[256];
		for(int i=0;i<samples.length;i++){
			ex[i]= samples[i];
		}
		if(LEARNING_MODE == 1)//DB4
		{
			DaubechiesTranform db4WT = new DaubechiesTranform();
			db4WT.forwardTrans(ex);
			double[] spectrumSquare = new double[8];
			spectrumSquare = spectrumSquareCalculate(ex);
			return spectrumSquare;
			
		}
		else if(LEARNING_MODE == 2)//Haar
		{
			HaarTransform hr = new HaarTransform();
			hr.forwardTrans(ex);
			double[] spectrumSquare = new double[8];
			spectrumSquare = spectrumSquareCalculate(ex);
			return spectrumSquare;
		}
		else if(LEARNING_MODE == 3)//FFT
		{
			FFT fftTrans= new FFT(); 
	        Complex[] x = new Complex[256];
	        Complex[] y;

	        // 我的一个窗口大小就是256
	        for (int i = 0; i < 256; i++) 
	        {
	            x[i] = new Complex(ex[i], 0);
	        }
	        y = fftTrans.fft(x);
	        double[] fftSpectrum = fftTrans.spectrum(y);
	        return fftSpectrum;
		}
		else
		{
			return null;
		}
	}
	
	//为小波计频谱
	private double[] spectrumSquareCalculate(double[] ex) {  									
		double[] spectrumSquare = new double[8];
		int passCount = 1;
		for(int i=0;i<8;i++){
			 for(int j=passCount;j<Math.pow(2,i+1);j++){
				 spectrumSquare[i] = spectrumSquare[i]+ex[j]*ex[j];
				 passCount++; 
				 
			 }
		}
		return spectrumSquare;
	}

	//计算最大值
	private double CalculateMax(double[] samples) {
		double max;
		max=samples[0];
		for(int i=1; i<256;i++){
			if(samples[i]>learnMax){
				learnMax = samples[i];
			}
		}
		return max;
	}
	
	//计算平均值
	private double CalculateMean(double[] samples) {
		double mean;
		double sum = 0;
		for(int i=0; i<256;i++){
			sum = sum + samples[i];
		}
		mean = sum/256;
		return mean;
	}
	
	//系统方法
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onCreate() {
		super.onCreate();
		Toast.makeText(getApplicationContext(), "LearningService was created", Toast.LENGTH_SHORT).show();
		
	}

	public void onDestroy() {
		RUN = false;
		quitCount = 10;//防止再次进入结束
		printLine();
		sensorManager.unregisterListener(sensorListener);
		sensorListener = null;
		dba.close();
		Toast.makeText(getApplicationContext(), "LearningService was destroyed", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Toast.makeText(getApplicationContext(), "LearningService is started", Toast.LENGTH_SHORT).show();
		RUN=true;
		learnCount = 0;
		quitCount =0;
		otherCount = 0;
		start = 0;
		end = 0;
		passTime = 0;
		learnSamples = new double[256];
		//从intent 中得到类型和模式
		ACT_TYPE = intent.getStringExtra("type");
		LEARNING_MODE = intent.getIntExtra("mode", 0);

		//打开sensor manager
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		sensorListener = new SensorEventListener() {
			
			public void onSensorChanged(SensorEvent event) {
				if(RUN=true){
					double[] values = new double[3];
					values[0] = event.values[0];
					values[1] = event.values[1];
					values[2] = event.values[2];
					processSample(values);
				}
				
			}
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		};
		sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	private void setResult(String result){
		Intent intent = new Intent("learned");
		Bundle bundle = new Bundle();
		bundle.putString("state", result);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	//打印分割线
	private void printLine(){
		Date date = new Date();
		StringBuffer buf = new StringBuffer();
		buf.append(date.toGMTString());
		for(int i=0;i<125;i++){
			buf.append("-");
		}
		try {
			pw = new PrintWriter(new FileOutputStream(LEARN_ANALYSIS_DATA_FILE, true));
			pw.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			pw.flush();
			pw.close();
		}
	}
}
