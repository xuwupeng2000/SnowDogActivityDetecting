package com.wu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;

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

import com.wu.fft.Complex;
import com.wu.fft.FFT;
import com.wu.wavelet.DaubechiesTranform;
import com.wu.wavelet.HaarTransform;

public class DetectingService extends Service {
	//关键变量
	int SWITCH_MODE = 0; // 1 是 db4小波 2 是haar小波 3是 FFT
	private static final int SAMPLE_STEP =50;
	private final int SAMPLE_NUMBER = 256; 
	private static double[] samples = new double[256];
	//计数器
	private static int samplesCount = 0;
	private static boolean isFirst = true;
	private static int moveCount = 0;
	//阀值
	private final double T_MEAN_REST =0.35;
	private final double T_MAX_PHONE_DETACHED = 0.0;
	private final double T_MEAN_PHONE_DETACHED = -0.25;
	//Sensor常量
	private SensorManager sensorManager;
	private SensorEventListener sensorListener;
	//写入文件的变量和常量
	private PrintWriter pw;
	private File DETCET_ANA_DATA_FILE=new File("/sdcard/Detecting_Analysis_Data.txt");
	private File DETECT_RAW_DATA_FILE=new File("/sdcard/Detecting_Raw_Data.txt");
	//三个属性 他们其实可以成为局部属性 并不一定需要在这里声明
	private double[] energyDistributon;
	private double mean;
	private double max;
	//判断是否停止
	private boolean RUN = false ;
	
	//处理信号的主方法
	private void processSample(double[] sensorValues) {
		double ampl = Math.sqrt( (double)sensorValues[0]*sensorValues[0] +
				 (double)sensorValues[1]*sensorValues[1] +
				 (double)sensorValues[2]*sensorValues[2] );
		ampl = ampl-SensorManager.STANDARD_GRAVITY;

		if(samplesCount<SAMPLE_NUMBER){
				
				samples[samplesCount]=ampl;
				samplesCount++;
			
		}
		if(samplesCount == SAMPLE_NUMBER && isFirst){
			isFirst = false;
			detectingActivity(samples);      
			
		}
		if(samplesCount == SAMPLE_NUMBER && !isFirst && moveCount != SAMPLE_STEP){
			for(int j=0; j<SAMPLE_NUMBER-1; j++){
				double exchange;
				int k;
				k = j+1;
				exchange = samples[k];
				samples[j]= exchange;
			}
			samples[255]=ampl;
			moveCount++;
		}
		
		while(samplesCount == SAMPLE_NUMBER && !isFirst && moveCount == SAMPLE_STEP){
			detectingActivity(samples);        
			moveCount=0;
		}
	}

	//开始探测活动
	private void detectingActivity(double[] samples) {
		 mean = CalculateMean(samples);
		 max = CalculateMax(samples);
		 energyDistributon = new double[8];
		

		if(max<T_MAX_PHONE_DETACHED && mean<T_MEAN_PHONE_DETACHED){
				String result = "phone detached";
				printOut(result);
				setResult(result);
		}
		else
		{
			energyDistributon = CalculateEnergyDistribution(samples);
			DataSet set = new DataSet(SWITCH_MODE,"unkonw",max,mean,energyDistributon);
			ONNClassifier classifier = new ONNClassifier(set, SWITCH_MODE, getBaseContext());
			String result = classifier.setResult();
			Log.e("res", result);
			if(result != null){
				printOut(result);
				setResult(result);
			}
			else{
				setResult("分类结果为空");
			}
		}
	}

	private double[] CalculateEnergyDistribution(double[] samples) {
		printRawData(samples, "samples");
		double[] ex = new double[256];
		for(int i=0;i<samples.length;i++){
			ex[i]= samples[i];
		}
		if(SWITCH_MODE == 0){
			Log.e("pass", "探测服务类型为空");
			return null;
		}
		else if(SWITCH_MODE == 1)//DB4
		{
			DaubechiesTranform db4WT = new DaubechiesTranform();
			db4WT.forwardTrans(ex);
			double[] spectrumSquare = new double[8];
			spectrumSquare = spectrumSquareCalculate(ex);
			return spectrumSquare;
			
		}
		else if(SWITCH_MODE == 2)//Haar
		{
			HaarTransform hr = new HaarTransform();
			hr.forwardTrans(ex);
			double[] spectrumSquare = new double[8];
			spectrumSquare = spectrumSquareCalculate(ex);
			return spectrumSquare;
		}
		else if(SWITCH_MODE == 3)//FFT
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
			if(samples[i]>max){
				max = samples[i];
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
	
	//没什么用
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	//没什么用
	public void onCreate() {
		super.onCreate();
	}

	//关闭这个服务
	public void onDestroy() {
		RUN=false;
		printLine();
		sensorManager.unregisterListener(sensorListener);
		sensorListener=null;
		isFirst = true;
		samplesCount = 0;
		moveCount = 0;
		samples = new double[256];
		Toast.makeText(getApplicationContext(), "Detecting Stoped", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SWITCH_MODE = intent.getIntExtra("mode", 0);
		RUN=true;
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorListener = new SensorEventListener() {
			
				public void onSensorChanged(SensorEvent event) {
					if(RUN){
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
	
	//返回探测服务的值
	private void setResult(String result){
		Intent intent = new Intent("result");
		Bundle bundle = new Bundle();
		bundle.putString("activity", result);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	
	//打印数据的工具方法 
	//打印纯属样本
	private void printRawData(double[] samples, String name){
		StringBuffer buf =  new StringBuffer();
		buf.append("("+name+"),   ");
		for(int i=0; i<samples.length; i++){
			buf.append(samples[i]+",  ");
		}
		try {
			pw=new PrintWriter(new FileOutputStream(DETECT_RAW_DATA_FILE, true));
			pw.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			pw.flush();
			pw.close();
		}
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
			pw = new PrintWriter(new FileOutputStream(DETCET_ANA_DATA_FILE, true));
			pw.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			pw.flush();
			pw.close();
		}
	}
	
	
	//打印分析结果到文件
	private void printOut(String result){
		StringBuffer buf =  new StringBuffer();
		if(result=="phone detached"){
			buf.append("phone detached"+"   ");
			buf.append("max="+max+"   ");
			buf.append("mean="+mean);
			buf.append("\n");
			
		}
		else if(result=="siting"){
			buf.append("siting"+"   ");
			buf.append("max="+max+"   ");
			buf.append("mean="+mean);
			buf.append("\n");
		}
		else{
			String res = result+",";
			buf.append(res);
			DecimalFormat df = new DecimalFormat("0.000");
			for(int i=0; i<8; i++){
				buf.append(df.format(energyDistributon[i]));
				buf.append(",   ");
			}
			
			buf.append(max+",   ");
			buf.append(mean);
			}
		
		try {
			pw = new PrintWriter(new FileOutputStream(DETCET_ANA_DATA_FILE, true));
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
