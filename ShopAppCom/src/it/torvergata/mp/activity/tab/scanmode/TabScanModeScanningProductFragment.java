package it.torvergata.mp.activity.tab.scanmode;

import java.io.IOException;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;

import it.torvergata.mp.activity.MainActivity;


import it.torvergata.mp.activity.MainActivity.LoadData;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.CameraPreview;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.DrawableManager;
import it.torvergata.mp.helper.HttpConnection;
import it.torvergata.mp.helper.ProductAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class TabScanModeScanningProductFragment extends Fragment{

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private LinearLayout mLinearLayout;
	private RelativeLayout mRelativeLayoutLastProduct;
	private	TextView scanText,tvTitle,tvDescription,tvQuantitative,tvPrice;
	private ImageView iv;
	private Button FinishScanButton, ContinueScanButton, encodeButton;
	
	private Dialogs dialogs;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	private ListProduct productList = new ListProduct();
	private Handler handler;
	
	
	OnTermAcquisitionListener mCallback;
	// Container Activity must implement this interface
    public interface OnTermAcquisitionListener {
        public void ViewListFragment(ListProduct list);
    }

    public void updateProductList(ListProduct list) {
		// TODO Auto-generated method stub
		
    	productList=list;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mLinearLayout = (LinearLayout) inflater.inflate(R.layout.activity_zbar,
				container, false);
		
		autoFocusHandler = new Handler();

		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		//productList = new ListProduct();
		
	
		dialogs= new Dialogs();
		
		
		scanText = (TextView) mLinearLayout.findViewById(R.id.scanText);
		
		mRelativeLayoutLastProduct= (RelativeLayout) mLinearLayout.findViewById(R.id.rlProductDetails);
		
		FinishScanButton = (Button) mLinearLayout.findViewById(R.id.FinishScanButton);
		
		
		if(productList.size()==0)mRelativeLayoutLastProduct.setVisibility(View.INVISIBLE);
		
		
		FinishScanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				if (barcodeScanned) {
//					barcodeScanned = false;
//					scanText.setText("Scanning...");
//					mCamera.setPreviewCallback(previewCb);
//					mCamera.startPreview();
//					previewing = true;
//					mCamera.autoFocus(autoFocusCB);
//				}
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				FrameLayout preview = (FrameLayout) mLinearLayout
						.findViewById(R.id.cameraPreview);

				if (preview.getChildCount() > 0) {
					preview.removeAllViews();
				}
				
				mCallback.ViewListFragment(productList);
//				
//				FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Fragment fragment = new TabScanModeListFragment();
//                
//                
//                fragmentTransaction.replace(R.id.realtabcontent, fragment);
//                fragmentTransaction.commit();
			}
		});

		//Handler per il messaggio di risposta del Server, proveniente dal Thread.
		handler = new Handler() {
            @Override
            public void handleMessage(Message mess) {
            	
            	int res = mess.arg1;
               	
            	if(res==Const.KO){
            		//Creiamo l'alert Dialog Product Not Fount On the Fly attraverso il Builder
                	AlertDialog.Builder dialogBox = new AlertDialog.Builder(getActivity());
					dialogBox.setIcon(R.drawable.productnotfound);
                	dialogBox.setTitle(R.string.tWarning);
                	dialogBox.setMessage(R.string.tProductNotFound);
					
                	dialogBox.setPositiveButton(R.string.tContinueScan, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss(); 
							scanText.setText(R.string.tScanning);
			                    mCamera.setPreviewCallback(previewCb);
			                    mCamera.startPreview();
			                    previewing = true;
			                    mCamera.autoFocus(autoFocusCB);
						}});
                	dialogBox.show();
                }
            	
                else if(res==Const.TIMEOUT){
                	AlertDialog dialogBox = dialogs.ConnectionTimeout(getActivity());
    				dialogBox.show();
                }

            } 
		};
		
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.

			return null;
		}

		return mLinearLayout;

	}
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTermAcquisitionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTermAcquisitionListener");
        }
    }
    
	public void onResume() {
		super.onResume();

		mCamera = getCameraInstance();

		mPreview = new CameraPreview(getActivity(), mCamera, previewCb,
				autoFocusCB);
		FrameLayout preview = (FrameLayout) mLinearLayout
				.findViewById(R.id.cameraPreview);

		if (preview.getChildCount() > 0) {
			preview.removeAllViews();
		}

		preview.addView(mPreview);

	}

	public void onPause() {
		super.onPause();
		releaseCamera();
	}


	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	
	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
//				previewing = false;
//				mCamera.setPreviewCallback(null);
//				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					String contents = sym.getData();
					String idProdotto="";
					
					playSound(getActivity());
					previewing = false;
					mCamera.setPreviewCallback(null);
					mCamera.stopPreview();
					
					
					try{
						idProdotto=contents;
					}
					catch(NumberFormatException e){
						Log.d("ERR","Errore");
						
					}
					scanText.setText("Id Ordine Scansionato= "+idProdotto);
					barcodeScanned = true;
					
					productList.searchAndCheck(idProdotto);
	
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	
	
	
	public void playSound(Context context)  {

		MediaPlayer mPlayer = new MediaPlayer();
	    mPlayer = MediaPlayer.create(context, R.raw.beep);
	    mPlayer.start();

}
}
