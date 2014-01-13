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

public class ListOrders extends ArrayList<ListProduct> {
	int count;

	public ListOrders(){
		super();
		count=0;
		
	}
	


	public int getCount() {
		return  this.count;
	}


	public boolean add(ListProduct prod){
		super.add(prod);
		count++;
	
		return true;
		
	}
	
	
	
	public ListProduct remove(int position){

		super.remove(position);
		return null;
	}
	
	
}
