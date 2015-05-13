package com.wu;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ONNClassifier {
	private DataSet set;
	private DbAdapter dba;
	private int mode;
	boolean isFirst = true;
	
	public ONNClassifier(DataSet set, int mode, Context ctx) {
		this.dba = new DbAdapter(ctx);
		this.mode = mode;
		this.set = set;
	}
	
	private ArrayList<DataSet> getAllSet(){
		ArrayList<DataSet> list = new ArrayList<DataSet>();
	    ObjectMapper mapper = new ObjectMapper(); // JACKSON
	    dba.open();
		Cursor cur = dba.fetchAllSet(mode);
		for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
			int column = cur.getColumnIndex("object");
			String objStr = cur.getString(column);//问题
			Log.e("class","读出这个对象的String "+objStr);//读出了这个String
			try {
				DataSet set = new DataSet();
				JsonNode rootNode = mapper.readValue(objStr, JsonNode.class);
				set = mapper.readValue(rootNode, DataSet.class);
				list.add(set);
				Log.e("class", "成功加入对象set到list");//没有到达这里

			} 
			catch (Exception e) {
				e.printStackTrace();
				Log.e("class","JACKSON读出失败了");
			} 
			finally{
				dba.close();
			}
		}
		return list;
	}
	
	//计算最短距离
	private String calDistance(ArrayList<DataSet> list){
		String res = "计算距离失败";
		double shortestDis = 999999999.0;
		double dis;
		Log.e("class", "list里面一共有多少个set "+Integer.toString(list.size()));
		for(DataSet learnSet : list){
			dis = Math.abs(set.getMax()-learnSet.getMax())+Math.abs(set.getMean()-learnSet.getMean());
			for(int j=0; j<set.getED().length;j++){//我剪掉了1是为了 不使用level8的dwt的能量分布 那些可以被认为仅仅是噪音
				dis = dis + Math.abs(set.getED()[j]-learnSet.getED()[j]);
			}
			if(isFirst){
				shortestDis = dis;
				res = learnSet.getType();
				isFirst = !isFirst;
			}
			else if(!isFirst && dis<shortestDis){
				shortestDis = dis;
				Log.e("class", "最短距离改变了 shortestDis= "+Double.toString(dis));
				res = learnSet.getType();
			}
		}
		return res;
	}

	public String setResult(){
			String res = null;
			ArrayList<DataSet> list = new ArrayList<DataSet>();
			list = getAllSet();
			res = calDistance(list);
			return res;
	}
}
