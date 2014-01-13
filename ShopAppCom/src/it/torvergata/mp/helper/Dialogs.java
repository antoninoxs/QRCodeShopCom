package it.torvergata.mp.helper;

import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.widget.TextView;

public class Dialogs {

	public Dialogs(){
		
	}
	public AlertDialog ConnectionTimeout(Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tTimeout)
				.setIcon(R.drawable.timeout)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
	public AlertDialog DeleteDialog(final int position,ListProduct productList,Context ctx) {
		Product prod = productList.get(position);
		AlertDialog alertDialog = new AlertDialog.Builder(ctx)
				.setTitle(prod.getNome())
				.setMessage(R.string.tMessageDelete)
				.setIcon(Const.resize(prod.getImmagine()))
				.setPositiveButton(R.string.tDeleteProduct,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {							}
						})
				.setNegativeButton(R.string.tCancelDelete,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								
							}
						}).create();
		return alertDialog;
	}

	
	public AlertDialog ConnectionNotFound(Context ctx) {
		AlertDialog alertDialog = new AlertDialog.Builder(ctx)
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tActivateConnection)
				.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	public AlertDialog chrashSendOrder(Context ctx) {
		AlertDialog alertDialog = new AlertDialog.Builder(ctx)
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tOrderSendWrong)
				.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
	
	public AlertDialog successSendOrder(Context ctx) {
		AlertDialog alertDialog = new AlertDialog.Builder(ctx)
				.setTitle(R.string.tShopApp)
				.setMessage(R.string.tOrderSendSuccess)
				.setIcon(android.R.drawable.ic_dialog_info)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
}
