package it.torvergata.mp.activity.tab.catalog;


 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.database.DatabaseManager;
import it.torvergata.mp.activity.tab.orders.TabOrdersMainFragment.OnOrderDetailListener;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeSendOrderFragment.SendOrder;
import it.torvergata.mp.entity.Category;
import it.torvergata.mp.entity.ListCategories;
import it.torvergata.mp.entity.ListMacrocategories;
import it.torvergata.mp.entity.ListOrders;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Macrocategory;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.CategoriesAdapter;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.HttpConnection;
import it.torvergata.mp.helper.MacrocategoriesAdapter;
import it.torvergata.mp.helper.OrdersAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
 
/**
 * @author mwho
 *
 */
public class TabCatalogCategoryFragment extends Fragment {

    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	private ListMacrocategories listMacrocategories;
	private Macrocategory Mcategory;
	private ListCategories listCategories;
	
	private LinearLayout mLinearLayout;
	private Dialogs dialogs;
	private CategoriesAdapter adapter;
	private Handler handler;
	
	OnCategoryDetailListener mCallback;

	// Container Activity must implement this interface
    public interface OnCategoryDetailListener {
         public void ViewCategoryDetailFragment(ListCategories listCategories,int pos);	
 }
  
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	final DatabaseManager db = new DatabaseManager(getActivity());
		dialogs= new Dialogs();
    	boolean isConnected = Const.verifyConnection(getActivity());
  	
    	listMacrocategories= new ListMacrocategories();
		
    	//Handler per il messaggio di risposta del Server, proveniente dal Thread.
		handler = new Handler() {
            @Override
            public void handleMessage(Message mess) {
            	
            	int res = mess.arg1;
               	
            	if(res==Const.TIMEOUT){
                	AlertDialog dialogBox = dialogs.ConnectionTimeout(getActivity());
    				dialogBox.show();
                }
                else {
                	
                	final ListView list = (ListView) mLinearLayout.findViewById(id.list);
                	list.setCacheColorHint(00000000);
                	
            		adapter =new CategoriesAdapter(getActivity(),
            				R.layout.macrocategory_list_item, listCategories);
            		list.setAdapter(adapter);
            	
            		list.setOnItemClickListener(new OnItemClickListener() {

            			@Override
            			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
            					long arg3) {
            				// TODO Auto-generated method stub
            				mCallback.ViewCategoryDetailFragment(listCategories,arg2);
            			}
            		});
                }
            }
                
            
		};
    	
    	
    	if(isConnected){
			//Lancio dell'AsyncTask Thread che effettua il download delle informazioni dal Server
			
			requestCategories task = new requestCategories();
			task.execute(""+Mcategory.getId());
		}else{
			AlertDialog dialogBox = dialogs.ConnectionNotFound(getActivity());
			dialogBox.show();
		}
		
    	
    		
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
       
        
      
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_catalog_category_list_layout,	container, false);
   		
		

        return mLinearLayout;
        
    }
  
    public void updateMacrocategory(ListMacrocategories list, int pos){
    	listMacrocategories= new ListMacrocategories();
      	listMacrocategories=list;
    	Mcategory =list.get(pos);
    }
    public class requestCategories extends AsyncTask<String, Void, Void> {
		
    	ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			//Creazione di un Dialog di attesa per il login
	        
		};

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
	       
		}

		@Override
		protected Void doInBackground(String...params) {
				String sMacrocategory = params[0];

			
				try {
				HttpConnection connection = new HttpConnection();
					
				JSONObject json=new JSONObject();
				json.put("richiesta", "2");
				json.put("macrocategoria", sMacrocategory);
				
				JSONArray arrayObject = connection.connectForCataalog("gestioneCatalogoApp", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
				Log.i("Lungh array: ", ""+arrayObject.length());
				listCategories=new ListCategories();
				for (int i=0;i<arrayObject.length();i++){ 
					// Lettura dell'oggetto Json
					JSONObject obj= (JSONObject)arrayObject.get(i);
					String idCategory = obj.getString("idCategoria");
					String nome = obj.getString("nome");
					Category tempCategory;
					
					Log.i("idCategoria: ", idCategory);
					Log.i("nome: ", nome);
					
					tempCategory= new Category(Integer.parseInt(idCategory), nome);
					
					listCategories.add(tempCategory);
				}
				
				Message message = handler.obtainMessage(1, Const.OK, 0);
			
				handler.sendMessage(message);
			
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}

			return null;
		}

	
		
	
	}
    
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCategoryDetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategoryDetailListener");
        }
    }
	

	

}