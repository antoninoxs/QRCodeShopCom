package it.torvergata.mp.helper;

/**
 * This file is part of AdvancedListViewDemo.
 * You should have downloaded this file from www.intransitione.com, if not, 
 * please inform me by writing an e-mail at the address below:
 *
 * Copyright [2011] [Marco Dinacci <marco.dinacci@gmail.com>]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * The license text is available online and in the LICENSE file accompanying the distribution
 * of this program.
 */

import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product> {

	private LayoutInflater mInflater;
	private Context context;
	
	
	private ListProduct productList =new ListProduct();
	private int mViewResourceId;
	
	public ProductAdapter(Context ctx, int viewResourceId,ListProduct pList) {
		super(ctx, viewResourceId, pList);
		context=ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		productList=pList;
		
		mViewResourceId = viewResourceId;
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Product getItem(int position) {
		return productList.get(position);
	}


	@Override
	public long getItemId(int position) {
		return Long.parseLong(productList.get(position).getId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(mViewResourceId, null);
		
		TextView tvTitle = (TextView)convertView.findViewById(R.id.title);
		TextView tvDescription = (TextView)convertView.findViewById(R.id.description);
		ImageView iv = (ImageView)convertView.findViewById(R.id.list_image);
		TextView tvQuantitative = (TextView)convertView.findViewById(R.id.tvQuantitative);
		
		TextView tvPrice = (TextView)convertView.findViewById(R.id.price);
		
		tvTitle.setText(productList.get(position).getNome());
		tvDescription.setText(productList.get(position).getDescrizione());
		tvQuantitative.setText("Quantità:"+" "+productList.get(position).getQuantita());
		
		
		
		String price = GenericFunctions.currencyStamp(productList.get(position).getPrezzoTotale());
		
		
		tvPrice.setText(price+" "+"\u20ac"+" ");
				
		DrawableManager.fetchDrawableOnThread(productList.get(position), iv,context);
		
		//iv.setImageDrawable(drawab.fetchDrawable(Const.IMAGE_URL));
		
		
		return convertView;
	}
}
