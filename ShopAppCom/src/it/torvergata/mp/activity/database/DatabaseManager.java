package it.torvergata.mp.activity.database;


import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.entity.ListOrders;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {
	DatabaseInterface dbInterface;
	SQLiteDatabase db;

	//Costruttore
	public DatabaseManager(Context context) {
	    dbInterface = new DatabaseInterface(context);
	}
	
		
	public void open() {
		db = dbInterface.getWritableDatabase();
	}
	
	public void close() {
		db.close();
	}
	
	//Metodo per aggiungere una Riga alla tabella
	public void addRow()
	{
		ContentValues values = new ContentValues();
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_ID, "1212121");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_NAME, "Gocciole");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_DESCRIPTION, "Gocciole Pavesi");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_QUANTITATIVE, "25");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_EXPIRE_DATE, "2013-06-12");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_PRICE, "23.00");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_IMAGE_FILE, "gocc.png");

		try{
			db.insert(dbInterface.TABLE_PRODOTTO, null, values);
		}
		catch (Exception e){
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
	
	//Metodo per cancellare una Riga della tabella
	public void deleteRow()
	{
	}

	//Metodo per aggiornare una riga della tabella
	public void updateRow()
	{
	}


	public void insertOrder(int res, ListProduct list) {
		// TODO Auto-generated method stub
		
		ContentValues OrderValues =new ContentValues();
		
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_ID, res);
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_DATE, GenericFunctions.getDate());
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_TIME, GenericFunctions.getTime());
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_QUANTITATIVE,list.getCount());
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_TOTAL_PRICE,list.getTotalPrice());

		
		try{
			String field = dbInterface.TABLE_ORDINE_COLUMN_ID+" = ?";
			String [] filter = {""+res};
			Cursor cursor= db.query(dbInterface.TABLE_ORDINE,
					null ,
					field,
					filter,null, null,null);
			cursor.moveToLast();
			if(cursor.getCount() == 0) {

				db.insert(dbInterface.TABLE_ORDINE, null, OrderValues);
			}
		}
		catch (Exception e){
			Log.e("DB Error VALORI TABELLA ORDINE", e.toString());
			e.printStackTrace();
		}
		
		for (int i=0;i<list.size();i++){
			ContentValues PoductValues = new ContentValues();
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_ID, 				list.get(i).getId());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_NAME, 			list.get(i).getNome());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_DESCRIPTION, 	list.get(i).getDescrizione());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_QUANTITATIVE,	list.get(i).getQuantita());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_EXPIRE_DATE, 	list.get(i).getScadenza());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_PRICE,			list.get(i).getPrezzoUnitario());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_IMAGE_FILE, 		list.get(i).getFileImmagine());
		
			try{
				String field = dbInterface.TABLE_PRODOTTO_COLUMN_ID+" = ?";
				String [] filter = {""+list.get(i).getId()};
				Cursor cursor= db.query(dbInterface.TABLE_PRODOTTO,
						null ,
						field,
						filter,null, null,null);
				cursor.moveToLast();
				if(cursor.getCount() == 1) {
					continue;
				}else{
					db.insert(dbInterface.TABLE_PRODOTTO, null, PoductValues);
				}
			}
			catch (SQLiteConstraintException e){
				Log.e("DB Exception Constrait", e.toString());
				continue;
			}
			catch (Exception e){
				Log.e("DB Error VALORI TABELLA PRODOTTO", e.toString());
				e.printStackTrace();
			}
			ContentValues ContainsValues = new ContentValues();
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_ID_ORDINE, res);
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_ID_PRODOTTO,list.get(i).getId());
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_QUANTITATIVE, list.get(i).getQuantita());
			try{
				db.insert(dbInterface.TABLE_CONTIENE,null, ContainsValues);
			}
			catch (Exception e){
				Log.e("DB Error VALORI TABELLA CONTIENE", e.toString());
				e.printStackTrace();
			}
		
		}
		
		

	}


	public ListProduct returnProductListOrder(int idOrder) {
		ListProduct result= new ListProduct();
		String field = dbInterface.TABLE_CONTIENE_COLUMN_ID_ORDINE+" = ?";
		String [] filter = {""+idOrder};
		Cursor cursor= db.query(dbInterface.TABLE_CONTIENE,
				null ,
				field,
				filter,null, null,null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String idProduct=cursor.getString(1);
			Log.i("CURSOR ID PRODUCT", idProduct);
			
			String fieldP = dbInterface.TABLE_PRODOTTO_COLUMN_ID+" = ?";
			String [] filterP = {""+idProduct};
			Cursor cursorP= db.query(dbInterface.TABLE_PRODOTTO,
					null ,
					fieldP,
					filterP,null, null,null);
			
			cursorP.moveToFirst();	
			String idTempProduct=cursorP.getString(0);
			Product temp= new Product(idTempProduct);
			temp.setNome(cursorP.getString(1));
			temp.setPrezzoUnitario(cursorP.getDouble(2));
			temp.setScadenza(cursorP.getString(3));
			temp.setQuantita(cursorP.getInt(4));
			temp.setDescrizione(cursorP.getString(5));
			temp.setFileImmagine(cursorP.getString(6));
			
//			Log.i("CURSORE PRODOTTO COLONNA 0",cursorP.getString(0));
//			
//			Log.i("CURSORE PRODOTTO COLONNA 1",cursorP.getString(1));
//			Log.i("CURSORE PRODOTTO COLONNA 2",cursorP.getString(2));
//			Log.i("CURSORE PRODOTTO COLONNA 3",cursorP.getString(3));
//			Log.i("CURSORE PRODOTTO COLONNA 4",cursorP.getString(4));
//			Log.i("CURSORE PRODOTTO COLONNA 5",cursorP.getString(5));
//			Log.i("CURSORE PRODOTTO COLONNA 6",cursorP.getString(6));
			result.add(temp);
			
			cursor.moveToNext();
		}
		cursor.close();
		return result;
		
	}


	public ListOrders returnListOrder() {
		ListOrders result= new ListOrders();
			Cursor cursor= db.query(dbInterface.TABLE_ORDINE,
				null ,
				null,
				null,null, null,null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int idOrder=cursor.getInt(0);
			String dateOrder=cursor.getString(3);
			String timeOrder=cursor.getString(4);
			int stateOrder=cursor.getInt(5);
			ListProduct tempListProduct = new ListProduct();
			tempListProduct=returnProductListOrder(idOrder);
			tempListProduct.setAssociateOrderId(idOrder);
			tempListProduct.setAssociateOrderDate(dateOrder);
			tempListProduct.setAssociateOrderTime(timeOrder);
			tempListProduct.setAssociateOrderState(stateOrder);
			
			result.add(tempListProduct);
			cursor.moveToNext();
		}
		cursor.close();
		return result;
		
	}


	public void updateOrder(String orderId, String state) {
		// TODO Auto-generated method stub
		
				
				try{
					String [] filter = {""+orderId};
					String strFilter = "idOrdine=" + orderId;
					ContentValues args = new ContentValues();
					args.put(dbInterface.TABLE_ORDINE_COLUMN_STATE, state);
					db.update(dbInterface.TABLE_ORDINE, args, strFilter, null);
				
				}
				catch (Exception e){
					Log.e("DB Error VALORI TABELLA ORDINE", e.toString());
					e.printStackTrace();
				}
				
				
	}
	
	
}