

package it.torvergata.mp.helper;

/*
 Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.entity.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class DrawableManager {
	//Statica perchè tutte le Activity o Fragment devono possedere la stessa mappa di caching delle immagini
    public static Map<String, Drawable> drawableMap = new HashMap<String, Drawable>();
    
    public DrawableManager() {
    	 drawableMap = new HashMap<String, Drawable>();
    }

    public static Drawable fetchDrawable(String urlString, Context ctx) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        Log.d(ctx.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = fetch(urlString,ctx);
            Drawable drawable = Drawable.createFromStream(is, "src");


            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(ctx.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
              Log.w(ctx.getClass().getSimpleName(), "could not get thumbnail");
            }

            return drawable;
        } catch (MalformedURLException e) {
            Log.e(ctx.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(ctx.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public static void fetchDrawableOnThread( final Product product, final ImageView imageView, final Context ctx) {
        final String urlString=Const.IMAGE_URL+product.getFileImmagine();
    	if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
            //product.setImmagine(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Drawable dr = (Drawable) message.obj;
            	imageView.setImageDrawable(dr);
                product.setImmagine(dr);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
            	Drawable d = ctx.getResources().getDrawable( R.drawable.blank );
            	Message messagea = handler.obtainMessage(1, d);
                handler.sendMessage(messagea);
                Drawable drawable = fetchDrawable(urlString,ctx);
                Message messageb = handler.obtainMessage(1, drawable);
                handler.sendMessage(messageb);
            }
        };
        thread.start();
    }
    
    public void fetchDrawableOnThread(final String urlString, final Product product, final Context ctx) {
        if (drawableMap.containsKey(urlString)) {
            product.setImmagine(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                product.setImmagine((Drawable) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString,ctx);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
    

    private static InputStream fetch(String urlString, Context ctx) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}