package it.torvergata.mp.entity;


import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.crypto.CryptoSha256;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.TextView;

public class ListProduct extends ArrayList<Product> {
	private CryptoSha256 crypto;
	double totalPrice=0.00;
	int count=0;
	int associateOrderId=0;
	String associateOrderDate="";
	int associateOrderState=-1;
	
	public void setAssociateOrderState(int associateOrderState) {
		this.associateOrderState = associateOrderState;
	}

	public String getAssociateOrderDate() {
		return associateOrderDate;
	}

	public void setAssociateOrderDate(String associateOrderDate) {
		this.associateOrderDate = associateOrderDate;
	}

	public String getAssociateOrderTime() {
		return associateOrderTime;
	}

	public void setAssociateOrderTime(String associateOrderTime) {
		this.associateOrderTime = associateOrderTime;
	}

	String associateOrderTime="";
	
	public int getAssociateOrderId() {
		return associateOrderId;
	}

	public void setAssociateOrderId(int associateOrderId) {
		this.associateOrderId = associateOrderId;
	}

	public ListProduct(){
		super();
		crypto=new CryptoSha256();
		
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public int getCount() {
		return  this.count;
	}
	public int getProductsCount() {
	
		int result=0;
		for(int i=0;i<this.size();i++)
		{
			result+=this.get(i).getQuantita();
		}
		return  result;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean add(Product prod){
		super.add(prod);
		
		totalPrice+=prod.getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		for (int i=0;i<prod.getQuantita();i++){
			count++;
		}
		return true;
		
	}
	
	public Product searchByIdAndUpdateLast(String id){
		
		for(int i=0;i<super.size();i++){
			if((super.get(i).getId().equals(id))){
				Product t=super.get(i);
				updateLast(i);
				return t;	
			}
		}
		
		return null;
	}
	
	public Product searchAndCheck(String id){
		
		for(int i=0;i<super.size();i++){
			if((super.get(i).getId().equals(id))){
				Product t=super.get(i);
				t.setChecked(true);
				return t;	
			}
		}
		
		return null;
	}
	
	public Product remove(int position){
		int q=super.get(position).getQuantita();
		totalPrice-=super.get(position).getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		for (int i=0;i<q;i++){
			count--;
		}
		super.remove(position);
		return null;
	}
	private double round(double d, int numbersAfterDecimalPoint) {
	    double n = Math.pow(10, numbersAfterDecimalPoint);
	    double d2 = d * n;
	    long l = (long) d2;
	    return ((double) l) / n;
	}

	public void setIncrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count++;
		totalPrice+=prezzoUnitarioProdotto;
	}

	public void updateLast(int pos) {
		Product t=this.get(pos);
		this.remove(pos);
		this.add(t);
		
	}

	public JSONObject getListIdForOrder(String user) {
		JSONArray jsonArray= new JSONArray();
		JSONObject jsonObjH = new JSONObject();
		String toHash="";
		JSONObject jsonObjU = new JSONObject();
		JSONObject json = new JSONObject();
		
		long msTime = System.currentTimeMillis();  

		Date anotherCurDate = new Date(msTime);  
		SimpleDateFormat formatter = new SimpleDateFormat("dMMMMyyyHHmm");  
		String formattedDateString = formatter.format(anotherCurDate);
		Log.i("Data nell'hash:",formattedDateString);
		
		
		/*
		 * Si Crea L'hash dalla stringa contenente :
		 * user
		 * Date
		 * Time
		 * idProduct#1
		 * QuantitativeProduct#1
		 * idProduct#2
		 * QuantitativeProduct#2
		 * .
		 * .
		 * .
		 * idProduct#n
		 * QuantitativeProduct#n
		 * 
		*/
		toHash+=user;
		toHash+=formattedDateString;
		
		try {
			
			jsonObjU.put("user", user);
		
		
		for(int i=0;i<this.size();i++){
			JSONObject temp = new JSONObject();
			int qnt=this.get(i).getQuantita();
			String id=this.get(i).getId();
			toHash+=id;
			toHash+=qnt;
			temp.put("Q",qnt );
			temp.put("id",id );
			jsonArray.put(temp);
		}
		Log.i("HashToSend",toHash);
		String hashToSendCrypto=crypto.encrypt(toHash);
		Log.i("HashToSend Crittogr",crypto.encrypt(toHash));
		
		jsonObjH.put("hashOrder", hashToSendCrypto);
		json.put("HashID"	, jsonObjH);
		json.put("User"		, jsonObjU);
		json.put("Products"	, jsonArray);
		
		
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
		// TODO Auto-generated method stub
		
	}

	public void setDecrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count--;
		totalPrice-=prezzoUnitarioProdotto;
	}

	public String getAssociateOrderState() {
		String result;
		result= GenericFunctions.convertOrderState(associateOrderState);
		return result;
	}

	
}
