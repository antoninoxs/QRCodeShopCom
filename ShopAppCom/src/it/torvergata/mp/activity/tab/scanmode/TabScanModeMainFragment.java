package it.torvergata.mp.activity.tab.scanmode;

import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.database.DatabaseManager;
import it.torvergata.mp.activity.tab.scanmode.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Classe del Fragment iniziale del Tab Scanning,
 * in questo fragment viene mostrato il pulsante che avvia la scansione dei qrCode
 * 
 */
public class TabScanModeMainFragment extends Fragment {
	
	//Interfaccia per la transizione al Fragment di acquisizione
	OnStartAcquisitionListener mCallback;
	// Container Activity must implement this interface
    public interface OnStartAcquisitionListener {
        public void ViewScanningFragment();
    }

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		if (container == null) {
			return null;
		}
		//Associazione del layout
		LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag1_layout,
				container, false);

		Button mButton = (Button) mLinearLayout.findViewById(R.id.btnQrCode);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Si lancia la transizione verso il tab di acquisizione
				mCallback.ViewScanningFragment();

			}
		});
		return mLinearLayout;

	}

	public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
         * Con questa chiamata ci assicuriamo che l'activity container abbia
         * correttamente implementato l'interfaccia di callback,
         * in caso contrario, viene sollevata un eccezione
         */
        try {
            mCallback = (OnStartAcquisitionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStartAcquisitionListener");
        }
    }
}