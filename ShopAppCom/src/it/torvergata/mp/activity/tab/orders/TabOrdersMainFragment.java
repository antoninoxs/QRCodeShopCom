package it.torvergata.mp.activity.tab.orders;


 
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.database.DatabaseManager;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeListFragment.OnAddQrCodeListener;
import it.torvergata.mp.entity.ListOrders;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.OrdersAdapter;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
 
/**
 * @author mwho
 *
 */
public class TabOrdersMainFragment extends Fragment {

	    /** (non-Javadoc)
	     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	     */
		private ListProduct productList;
		private TextView totalPrice;
		private Button btnAdd,BtnContinue;
		private LinearLayout mLinearLayout;
		private OrdersAdapter adapter;
		private Dialogs dialogs;
		
		OnOrderDetailListener mCallback;

		// Container Activity must implement this interface
	    public interface OnOrderDetailListener {
	         public void ViewOrderDetailFragment(ListOrders list,int pos);

			
			
	 }
	  
		
		
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	
	    	final DatabaseManager db = new DatabaseManager(getActivity());
	    	db.open();
	    	final ListOrders listOrders=db.returnListOrder();
	    	//productList=db.returnProductListOrder(236);
	    	db.close();
	    	
	    	
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
	       
	        
	      
	        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_orders_list_layout,
					container, false);
	   		final ListView list = (ListView) mLinearLayout.findViewById(id.list);
	   		list.setCacheColorHint(00000000);
	   		
			adapter =new OrdersAdapter(getActivity(),
					R.layout.order_list_item, listOrders);
			list.setAdapter(adapter);
		
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					mCallback.ViewOrderDetailFragment(listOrders,arg2);
				}
			});
			
	        return mLinearLayout;
	        
	    }
	  
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        
	        // This makes sure that the container activity has implemented
	        // the callback interface. If not, it throws an exception
	        try {
	            mCallback = (OnOrderDetailListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnOrderDetailListener");
	        }
	    }
		
	
		

	}