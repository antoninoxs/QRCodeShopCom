package it.torvergata.mp;

import static it.torvergata.mp.Const.SERVER_URL;
import it.torvergata.mp.helper.HttpConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
  
public final class ServerUtilities {
	
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    static Handler handler;
    public static void register(final Context context, final String regId) {
//        String serverUrl = SERVER_URL;
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("regId", regId);
//        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
//        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
//            try {
//                post(serverUrl, params);
//                GCMRegistrar.setRegisteredOnServer(context, true);
//                return;
//            } catch (IOException e) {
//                if (i == MAX_ATTEMPTS) {
//                    break;
//                }
//                try {
//                    Thread.sleep(backoff);
//                } catch (InterruptedException e1) {
//                    Thread.currentThread().interrupt();
//                    return;
//                }
//                backoff *= 2;
//            }
//        }
    	
//		//Handler per il messaggio di risposta del Server, proveniente dal Thread.
//		handler = new Handler() {
//            @Override
//            public void handleMessage(Message mess) {
//            	
//            	int res = mess.arg1;
//            }               
//            
//		};
		HttpConnection connection = new HttpConnection();
		JSONObject json = new JSONObject();
	    //Gestione della Sessione
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		String user = settings.getString("User","*");
		Log.i("USER RECUPERATO DA PREFERENCES", user);
		try {
			json.put("regId", regId);
			json.put("user", user);
			
			JSONObject object = connection.connect("notificationRegister", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						
		
		
		
    }
    static void unregister(final Context context, final String regId) {
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
        } catch (IOException e) {
        }
    }
    private static void post(String endpoint, Map<String, String> params) throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Errore " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}