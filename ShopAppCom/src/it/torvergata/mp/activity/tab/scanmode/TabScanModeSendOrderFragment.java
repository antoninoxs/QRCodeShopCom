package it.torvergata.mp.activity.tab.scanmode;


 
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.MainActivity;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeScanningFragment.LoadDataProduct;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.crypto.CryptoSha256;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.HttpConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
/**
 * @author mwho
 *
 */
public class TabScanModeSendOrderFragment extends Fragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	
	private LinearLayout mLinearLayout;
	private ListProduct productList;
	private ImageView ivSendOrder;
	private TextView   tvNumberProducts,tvTotalCost;
	private Dialogs dialogs;
	
	private Handler handler;
	
	OnFinishOrderListener mCallback;
	// Container Activity must implement this interface
    public interface OnFinishOrderListener {
        public void FinishOrder(ListProduct list,int res);
    }

	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	//Handler per il messaggio di risposta del Server, proveniente dal Thread.
    			handler = new Handler() {
    	            @Override
    	            public void handleMessage(Message mess) {
    	            	
    	            	int res = mess.arg1;
    	               	
    	            	if(res==Const.KO){
    	                	AlertDialog dialogBox = dialogs.chrashSendOrder(getActivity());
    						dialogBox.show();
    	                }
    	            	
    	                else if(res==Const.TIMEOUT){
    	                	AlertDialog dialogBox = dialogs.ConnectionTimeout(getActivity());
    	    				dialogBox.show();
    	                }
    	                else {
    	                	Log.i("Ordine", "Ordine inviato con successo, Id Assegnato :"+res);
    	                	AlertDialog dialogBox = dialogs.successSendOrder(getActivity());
    	    				dialogBox.show();
    	                	mCallback.FinishOrder(productList,res);
    	                }
    	                }
    	                
    	            
    			};
    			
    	
    	if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_order_layout,
				container, false);
        
        dialogs=new Dialogs();
        ivSendOrder= (ImageView) mLinearLayout.findViewById(R.id.ivSendOrder);
        tvNumberProducts =(TextView) mLinearLayout.findViewById(R.id.tvNumberProductsTabOrder);
        tvTotalCost =(TextView) mLinearLayout.findViewById(R.id.tvTotalCostTabOrder);
        
        setTotalProductAndTotalPrice(tvNumberProducts,tvTotalCost);
        
      //Gestione della Sessione
		SharedPreferences settings = getActivity().getSharedPreferences(Const.PREFS_NAME, 0);
	
		String user = settings.getString("User","*");
		Log.i("USER RECUPERATO DA PREFERENCES", user);
		
        final JSONObject listIdForOrder =productList.getListIdForOrder(user);
        		
        
        ivSendOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Determiniamo se c'è una connessione ad internet
				boolean isConnected = Const.verifyConnection(getActivity());
				
				
				if(isConnected){
					//Lancio dell'AsyncTask Thread che effettua il download delle informazioni dal Server
					SendOrder task = new SendOrder();
					task.execute(listIdForOrder);
				}else{
					AlertDialog dialogBox = dialogs.ConnectionNotFound(getActivity());
					dialogBox.show();
				}
				
				
			}
		});
		
        return mLinearLayout;}

	public void updateProduct(ListProduct productl) {
		// TODO Auto-generated method stub
		productList=productl;
		
	}
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFinishOrderListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFinishOrderListener");
        }
    }
    
	public class SendOrder extends AsyncTask<JSONObject, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			//Creazione di un Dialog di attesa per il login
	        progressDialog= ProgressDialog.show(getActivity(), "ShopApp","Invio Ordine...", true);
		};

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
	        progressDialog.dismiss();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject json = params[0];
			boolean timeout=true;
				try {
				HttpConnection connection = new HttpConnection();
				
				String jsonStr = json.toString();

				Log.i("Json Inviato: ", json.toString(4));
				for(int i=0;i<Const.ATTEMPTS_RETRANSMISSION;i++){			
					Log.i("CICLO DI TIMEOUT", "INIZIO ITERAZIONE NUMERO: "+i);
					JSONObject object;
					
					object = connection.connect("ordernew", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
					
					
					
					String result = object.getString("result");
					int idOrder=Integer.parseInt(result);
				
	//				if (idOrder==Const.KO){
	//					Message message = handler.obtainMessage(1, Const.KO, 0);
	//					handler.sendMessage(message);
	//				}
					if(idOrder==Const.TIMEOUT){
						Log.i("CICLO DI TIMEOUT", "CONTINUE ITERAZIONE NUMERO: "+i);
						
						continue;
					}
					else{
						Log.i("CICLO DI TIMEOUT", "BREAK ITERAZIONE NUMERO: "+i);
						timeout=false;
						Message message = handler.obtainMessage(1, idOrder, 0);
						handler.sendMessage(message);
						break;
					}
				
				}
			if(timeout){
			Message message = handler.obtainMessage(1, Const.TIMEOUT, 0);
			handler.sendMessage(message);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Generic Error:" + e.toString());
			}

			return null;
		}

	
	}
	
	public void setTotalProductAndTotalPrice(TextView totalProducts,TextView totalPrice){
		String price = GenericFunctions.currencyStamp(productList.getTotalPrice());
		totalPrice.setText(price+" "+getString(R.string.Euro));
		totalProducts.setText(""+productList.getCount()); 
	}
}