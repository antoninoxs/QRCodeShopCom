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
import it.torvergata.mp.helper.ProductChoiceAdapter;
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
public class TabCatalogProductsFragment extends Fragment {

    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	private ListMacrocategories listMacrocategories;
	private Macrocategory Mcategory;
	private ListCategories listCategories;
	private Category Category;
	private ListProduct productList;
	
	private LinearLayout mLinearLayout;
	private Dialogs dialogs;
	private ProductChoiceAdapter adapter;
	private Handler handler;
	private int positionOnCategoryList;
	OnProductChoiceDetailListener mCallback;

	// Container Activity must implement this interface
    public interface OnProductChoiceDetailListener {
         public void ViewProductChoiceDetailFragment(ListProduct productList,int pos,Category c);	
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
                	
            		adapter =new ProductChoiceAdapter(getActivity(),
            				R.layout.product_choice_list_item, productList);
            		list.setAdapter(adapter);
            	
            		list.setOnItemClickListener(new OnItemClickListener() {

            			@Override
            			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
            					long arg3) {
            				// TODO Auto-generated method stub
            				mCallback.ViewProductChoiceDetailFragment(productList,arg2,Category);
            			}
            		});
                }
            }
                
            
		};
    	
    	
    	if(isConnected){
			//Lancio dell'AsyncTask Thread che effettua il download delle informazioni dal Server
			
			requestProduct task = new requestProduct();
			task.execute(""+Category.getId());
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
       
        
      
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_catalog_products_list_layout,	container, false);
   		
		

        return mLinearLayout;
        
    }
  
    public void updateCategory(ListCategories list, int pos){
    	positionOnCategoryList=pos;
    	listCategories= new ListCategories();
    	listCategories=list;
    	Category =list.get(pos);
    }
    public void updateCategory(Category cat){
    	Category =cat;
    }
    public class requestProduct extends AsyncTask<String, Void, Void> {
		
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
				String sCategory = params[0];

			
				try {
				HttpConnection connection = new HttpConnection();
					
				JSONObject json=new JSONObject();
				json.put("richiesta", "3");
				json.put("categoria", sCategory);
				
				JSONArray arrayObject = connection.connectForCataalog("gestioneCatalogoApp", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
				Log.i("Lungh array: ", ""+arrayObject.length());
				productList=new ListProduct();
				for (int i=0;i<arrayObject.length();i++){ 
					// Lettura dell'oggetto Json
					JSONObject object= (JSONObject)arrayObject.get(i);
					// Lettura dell'oggetto Json
					
					String idProdotto = object.getString("idProdotto");
					
					Product tempProd= new Product(idProdotto);
					
					String nome = object.getString("nome");
					double prezzo = object.getDouble("prezzo");
					String scadenza = object.getString("scadenza");
					String disponibilita = object.getString("disponibilita");
					String descrizione = object.getString("descrizione");
					String fileImmagine = object.getString("file_immagine");
	
					Log.i("idProdotto: ", idProdotto);
					Log.i("nome: ", nome);
					Log.i("prezzo: ", Double.toString(prezzo));
					Log.i("scadenza: ", scadenza);
					Log.i("descrizione: ", descrizione);
					Log.i("disponibilita: ", disponibilita);
					Log.i("file_immagine: ", fileImmagine);
	
					// Creazione del nuovo Prodotto
					tempProd.setId(idProdotto);
					tempProd.setNome(nome);
					tempProd.setPrezzoUnitario(prezzo);
					tempProd.setScadenza(scadenza);
					tempProd.setDescrizione(descrizione);
					tempProd.setDisponibilita(Integer.parseInt(disponibilita));
					tempProd.setFileImmagine(fileImmagine);
					tempProd.setQuantita(0);
	
					// Aggiunta del nuovo prodotto alla lista dei prodotti
					productList.add(tempProd);
				
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
            mCallback = (OnProductChoiceDetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductChoiceDetailListener");
        }
    }
	

	

}