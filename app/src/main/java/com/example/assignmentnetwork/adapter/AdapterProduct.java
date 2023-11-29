package com.example.assignmentnetwork.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentnetwork.R;
import com.example.assignmentnetwork.UpdateItemClickListener;
import com.example.assignmentnetwork.asynctask.CreateNewProductTask;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.fragment.HomeFragment;
import com.example.assignmentnetwork.model.Product;

import java.util.ArrayList;

public class AdapterProduct extends BaseAdapter {

    private AlertDialog dialog;
    String pid, strName, strPrice, strDes;
    CreateNewProductTask newProductTask;

    Context context;
    ArrayList<Product> listProducts;

//    public void setSearchProduct(ArrayList<Product> listProducts){
//        this.listProducts = listProducts;
//        notifyDataSetChanged();
//    }

    public AdapterProduct(Context context, ArrayList<Product> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
    }


    @Override
    public int getCount() {
        return listProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return listProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public static class ViewHolder {
        ImageView imgProduct;
        TextView tvID, tvName, tvDescription, tvPrice;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;

        Product productItem = listProducts.get(position);
//        String exp = productItem.getName();
//        Log.i("TAG", "getView: " + exp);


        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item, null);
            viewHolder.imgProduct = view.findViewById(R.id.viewimgproducts);
            viewHolder.tvID = view.findViewById(R.id.pid);
            viewHolder.tvName = view.findViewById(R.id.name);
            viewHolder.tvDescription = view.findViewById(R.id.description);

            viewHolder.tvPrice = view.findViewById(R.id.price);
            viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Product product = listProducts.get(position);
//        viewHolder.imgProduct.setImageBitmap(product.);
        viewHolder.tvID.setText(product.getId() + "");
        viewHolder.tvName.setText(product.getName());
        viewHolder.tvDescription.setText(product.getDescription());
        viewHolder.tvPrice.setText(product.getPrice());


        //ImageButton
        ImageButton img_btn = view.findViewById(R.id.imgbtn_flowmenu);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, productItem);
            }
        });

        notifyDataSetChanged();
        return view;
    }

    private void showPopupMenu(View view, Product productItems) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.edit_del);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit_product) {

                    Log.i("TAG", "onMenuItemClick: Edit");
                    editItemClickListener.onEditItemClicked(productItems);
                    return true;
                } else if (item.getItemId() == R.id.delete_product) {

                    Log.i("TAG", "onMenuItemClick: Remove");
                    RemoveItemClickListener.onRemoveItemClicked(productItems);
                    return true;
                } else {
                    return false;
                }
            }
        });
        // Show the Popup Menu
        popupMenu.show();
    }

    private OnEditItemClickListener editItemClickListener;

    public interface OnEditItemClickListener {
        void onEditItemClicked(Product product);
    }

    public void setOnEditItemClickListener(OnEditItemClickListener listener) {
        this.editItemClickListener = listener;
    }
    private OnRemoveItemClickListener RemoveItemClickListener;

    public interface OnRemoveItemClickListener {
        void onRemoveItemClicked(Product product);
    }

    public void setOnRemoveItemClickListener(OnRemoveItemClickListener listener) {
        this.RemoveItemClickListener = listener;
    }
}
