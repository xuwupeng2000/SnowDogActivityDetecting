package com.wu;


public class DataSet{
	private int mode;
	private String type;
	private double max;
	private double mean;
	private double[] ed;
	
	public DataSet(){
	
		
	}
	
	public DataSet(int mode, String type, double max, double mean,double[] ed) {
		this.mode = mode;
		this.type = type;
		this.max = max;
		this.mean =mean;
		this.ed =ed;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double[] getED() {
		return ed;
	}

	public void setED(double[] eD) {
		ed = eD;
	}

}
