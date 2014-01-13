package it.torvergata.mp.activity;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;

import it.torvergata.mp.crypto.CryptoSha256;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.HttpConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registrazione extends Activity {

	private EditText edNome, edCognome, edEmail, edUsername, edPassword,
			edPassword1;
	private Button bRegistrati;
	private InputStream is = null, ins = null;
	private Handler handler;
	private CryptoSha256 crypto;
	private Dialogs dialogs;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrazione);
		
		/*Inizializzazione dell'oggetto Dialog e dell'oggetto crypto*/
		dialogs= new Dialogs();
		context=this;
		crypto= new CryptoSha256();
		edNome = (EditText) findViewById(R.id.ETregistrazioneNome);
		edCognome = (EditText) findViewById(R.id.ETregistrazioneCognome);
		edEmail = (EditText) findViewById(R.id.ETregistrazioneEmail);
		edUsername = (EditText) findViewById(R.id.ETregistrazioneUsername);
		edPassword = (EditText) findViewById(R.id.ETregistrazionePassword);
		edPassword1 = (EditText) findViewById(R.id.ETregistrazionePassword1);
		bRegistrati = (Button) findViewById(R.id.BregistrazioneRegistrati);

		edNome.requestFocus();
		
		bRegistrati.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//Se ci sono campi vuoti si avvisa l'utente
				if (edNome.length() == 0 || edCognome.length() == 0
						|| edEmail.length() == 0 || edUsername.length() == 0
						|| edPassword.length() == 0) {
					Toast toast = Toast.makeText(Registrazione.this,
							R.string.tBlankField, Toast.LENGTH_LONG);
					toast.show();
				//Se le password inserite non coincidono si avvisa l'utente
				} else if (!((edPassword.getText().toString())
						.equals(edPassword1.getText().toString()))) {
					Toast toast = Toast.makeText(Registrazione.this,
							R.string.tPasswordMismatch, Toast.LENGTH_LONG);
					toast.show();
				} else {
					String nome = edNome.getText().toString();
					String cognome = edCognome.getText().toString();
					String email = edEmail.getText().toString();
					String username = edUsername.getText().toString();
					String password = edPassword.getText().toString();
					String passwordCrypto="";
					try {
						passwordCrypto = crypto.encrypt(password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Si provvede ad inserire nel DB il nuovo utente
					insertToDB(nome, cognome, email, username, passwordCrypto);
				}
			}
		});
	}

	public void insertToDB(String nome, String cognome, String email,
			String username, String password) {

		Registration task = new Registration();
		task.execute(nome, cognome, email, username, password);

		//Handler per il messaggio di risposta del Server, proveniente dal Thread che effettua il login.
		handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	Bundle b = message.getData();
            	String mess = b.getString("Message");
            	String res = b.getString("Result");
            	String errQ = b.getString("ErrorQuery");
            	if(res.equals(Const.TIMEOUTS)){
            		AlertDialog dialogBox = dialogs.ConnectionTimeout(context);
    				dialogBox.show();
            	}
            	else{		            	
	            	//KOU==Ko User, è l'identificativo del messaggio che segnala un Username già presente
	            	if(res.equals("KOU")){
	            		Toast toast = Toast.makeText(Registrazione.this,
	            				mess, Toast.LENGTH_LONG);
	            		toast.show();
	            	}
	            	//KO== Gestioamo l'errore Generico, viene stampato l'errore in un Toast
	            	else if(res.equals("KO")){
	                Toast toast = Toast.makeText(Registrazione.this,
	        				errQ, Toast.LENGTH_LONG);
	        		toast.show();
	            	}
	            	//Ok Operazione di registrazione eseguita con successo, torniamo alla schermata di Login
	            	else if(res.equals("OK")){
	            		Toast toast = Toast.makeText(Registrazione.this,
	            				mess, Toast.LENGTH_LONG);
	            		toast.show();
	            		finish();
	            	}
            	}
            
            }
		};
		
	}


//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.registrazione, menu);
//		return true;
//	}

	/***
	 * Classe AsyncTask di gestione per l'inserimento del nuovo utente nel DB.
	 */
	public class Registration extends AsyncTask<String, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// Creazione di un Dialog di attesa
			progressDialog = ProgressDialog.show(Registrazione.this, "ShopApp",
					"Registrazione in corso...", true);
		};

		@Override
	    protected Void doInBackground(String... params)
	    {   
		
	    	try {
	    	
	    		JSONObject json = new JSONObject();
	    		json.put("Name", params[0]);
				json.put("Surname", params[1]);
				json.put("Email", params[2]);
				json.put("User", params[3]);
				json.put("Password", params[4]);
				
				HttpConnection connection = new HttpConnection();
				JSONObject object = connection.connect("registrazioneAppCommesso", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);

				//Lettura dell'oggetto Json
				String mess = object.getString("Message");
				String result = object.getString("Result");
				String errorQuery = object.getString("QueryErr");
				
				
				Log.i("Message",mess);
				Log.i("Result", result);
				Log.i("errorQuery", errorQuery);
				
				
				//Comunicazione al Thread principale dell'esito dell'operazione di Registrazione
				Message message = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("Message",mess);
				b.putString("Result", result);
				b.putString("errorQuery",mess);
				message.setData(b);
				handler.sendMessage(message);
				
			
				
				
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}
			return null;	    	
	        
	    }		@Override
		protected void onPostExecute(Void result) {
			// Chiusura del Dialog di attesa
			super.onPostExecute(result);
			progressDialog.dismiss();
		};
	}
	
}
