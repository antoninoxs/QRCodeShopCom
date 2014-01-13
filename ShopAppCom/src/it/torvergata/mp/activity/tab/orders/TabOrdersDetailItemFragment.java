package it.torvergata.mp.activity.tab.orders;


 
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeListFragment.OnAddQrCodeListener;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 

public class TabOrdersDetailItemFragment extends Fragment {
    
	private Product prod;
	private LinearLayout mLinearLayout;
	private TextView tvTitle,tvDescription,tvPrice,tvQuantitative,tvSimplePrice;
	private ImageView ivImage;
	private ListProduct productlist;
	private int position;
	
	
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
       
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_orders_product_detail,
				container, false);
        
        tvTitle 			= (TextView) mLinearLayout.findViewById(R.id.tvTitleDetail);
        
        //Inizializzazione
      		ivImage = (ImageView)  mLinearLayout.findViewById(R.id.ivDetailImage);
      		tvTitle = (TextView)  mLinearLayout.findViewById(R.id.tvTitleDetail);
      		tvDescription = (TextView)  mLinearLayout.findViewById(R.id.tvDescriptionDetail);
      		tvPrice = (TextView)  mLinearLayout.findViewById(R.id.tvTotalPrice);
      		tvQuantitative = (TextView)  mLinearLayout.findViewById(R.id.tvQuantitativeDetail);
      		tvSimplePrice = (TextView)  mLinearLayout.findViewById(R.id.tvSimplePrice);
      	
		ivImage.setImageDrawable(prod.getImmagine());
		tvTitle.setText(prod.getNome());
		tvDescription.setText(prod.getDescrizione());
		tvPrice.setText(getString(R.string.tvTotal)+" "+GenericFunctions.currencyStamp(prod.getPrezzoTotale())+"  "+getString(R.string.Euro));
		tvQuantitative.setText(getString(R.string.tQuantitative)+" "+prod.getQuantita());
		tvSimplePrice.setText(getString(R.string.tPrice)+" "+GenericFunctions.currencyStamp(prod.getPrezzoUnitario())+" "+getString(R.string.Euro));
	
		
		
		
	
		
        return mLinearLayout;   
    }
 
    
	public void updateProduct(ListProduct list,int pos) {
		// TODO Auto-generated method stub
		productlist = new ListProduct();
		productlist	=	list;
		prod		=	list.get(pos);
		position	= 	pos;
		
	}

}