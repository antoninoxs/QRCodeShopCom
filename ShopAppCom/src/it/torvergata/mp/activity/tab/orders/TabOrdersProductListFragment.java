package it.torvergata.mp.activity.tab.orders;


 
import java.text.DecimalFormat;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeListFragment.OnAddQrCodeListener;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
 

public class TabOrdersProductListFragment extends Fragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	private ListProduct productList;
	private TextView totalPrice,tvOrderState;
	private Button btnAdd,BtnContinue;
	private LinearLayout mLinearLayout;
	private ProductAdapter adapter;
	private Dialogs dialogs;
	

	OnProductsList mCallback;
	
	public interface OnProductsList{
		public void viewProductDetail(ListProduct list, int pos);
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
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
       
        
        dialogs=new Dialogs();
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_orders_products_list_layout,
				container, false);
        
    	totalPrice 			= (TextView) mLinearLayout.findViewById(R.id.tvTotalPrice);
    	tvOrderState 			= (TextView) mLinearLayout.findViewById(R.id.tvOrderDetailState);
		
    	Button btnAdd 		= (Button) mLinearLayout.findViewById(R.id.btnAdd);
		Button btnContinue 	= (Button) mLinearLayout.findViewById(R.id.btnContinue);
		final ListView list = (ListView) mLinearLayout.findViewById(id.list);
		list.setCacheColorHint(00000000);
		
		adapter =new ProductAdapter(getActivity(),
				R.layout.new_list_item, productList);
		list.setAdapter(adapter);
		
		tvOrderState.setText(getString(R.string.tvOrderState)+productList.getAssociateOrderState());
		
		setTotalPrice();
		
		
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mCallback.viewProductDetail(productList, arg2);
			}
		});
		
		
	
		
		
		
        return mLinearLayout;
        
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnProductsList) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductsList");
        }
    }

	public void updateProductList(ListProduct list) {
		// TODO Auto-generated method stub
		productList=list;
	}
	
	public void setTotalPrice(){
		String price = GenericFunctions.currencyStamp(productList.getTotalPrice());
		totalPrice.setText(getString(R.string.tvTotal)+" "+price+" "+getString(R.string.Euro));
		
	}
	

}