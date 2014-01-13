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

public class ListCategories extends ArrayList<Category> {
	int count;

	public ListCategories(){
		super();
		count=0;
		
	}
	
	public int getCount() {
		return  this.count;
	}


	public boolean add(Category m){
		super.add(m);
		count++;
	
		return true;
		
	}
	
	public Category get(int position){
		return super.get(position);
		
	}
	
	public Category remove(int position){
		super.remove(position);
		return null;
	}
	
	
}
