package com.example.assignmentnetwork.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.assignmentnetwork.R;
import com.example.assignmentnetwork.UpdateItemClickListener;
import com.example.assignmentnetwork.adapter.AdapterProduct;
import com.example.assignmentnetwork.asynctask.CreateNewProductTask;
import com.example.assignmentnetwork.asynctask.DeleteProductTask;
import com.example.assignmentnetwork.asynctask.GetProductDetailsTask;
import com.example.assignmentnetwork.asynctask.LoadAllProductsTask;
import com.example.assignmentnetwork.asynctask.SaveProductDetailsTask;
import com.example.assignmentnetwork.database.Constants;
import com.example.assignmentnetwork.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterProduct.OnEditItemClickListener, AdapterProduct.OnRemoveItemClickListener {

    SearchView search;
    private FloatingActionButton add_product;
    String strName, strPrice, strDes;
    CreateNewProductTask newProductTask;
    private AlertDialog dialog;
    private ListView lvProducts;
    private ActivityResultLauncher<Intent> myLauncher;
    private ArrayList<Product> listSearch;

    AdapterProduct adapterProduct;

    LoadAllProductsTask task;
    SaveProductDetailsTask saveProductDetailsTask;
    DeleteProductTask deleteProductTask;
    String id_product;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        add_product = view.findViewById(R.id.floatAdd);
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view);
            }
        });

        loadData(view);

        return view;
    }

    //Load data
    private void loadData(View view) {
        lvProducts = view.findViewById(R.id.listProducts);
        AdapterProduct.OnEditItemClickListener myItemClickListener = this;
        AdapterProduct.OnRemoveItemClickListener onRemoveItemClickListener = this;
        task = new LoadAllProductsTask(getContext(), lvProducts, adapterProduct, myItemClickListener, onRemoveItemClickListener);
        task.execute();
    }
    private void showDialog(View view1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_product, null);
        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtDescription = view.findViewById(R.id.edtProductDes);

        newProductTask = new CreateNewProductTask(getContext());

        builder.setView(view);
        builder.setTitle("Thêm sản phẩm");
        builder.setPositiveButton("Add products", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strName = edtName.getText().toString();
                strDes = edtDescription.getText().toString();
                strPrice = edtPrice.getText().toString();

                newProductTask.execute(strName, strDes, strPrice);
                dialog.dismiss();
                loadData(view1);

            }
        });
    }


    //cập nhật sản phẩm
    @Override
    public void onEditItemClicked(Product product) {
        showDialogUpdate(product);
    }

    public void showDialogUpdate(Product product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_product, null);
        EditText ed_productName = view.findViewById(R.id.edProductName);
        EditText ed_productDes = view.findViewById(R.id.edProductDes);
        EditText ed_productPrice = view.findViewById(R.id.edProductPrice);


        ed_productName.setText(product.getName());
        ed_productDes.setText(product.getDescription());
        ed_productPrice.setText(product.getPrice());

        id_product = product.getId();

        builder.setView(view);
        builder.setTitle("Cập nhật sản phẩm");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strName = ed_productName.getText().toString();
                strDes = ed_productDes.getText().toString();
                strPrice = ed_productPrice.getText().toString();
                saveProductDetailsTask = new SaveProductDetailsTask(getContext());
                saveProductDetailsTask.execute(id_product, strName, strPrice, strDes);
            }
        });
        builder.setNegativeButton("cancel", null);
        dialog = builder.create();
        dialog.show();
    }


    //xóa sản phẩm
    @Override
    public void onRemoveItemClicked(Product product) {
        dialog_RemovePost(product);
    }
    private void dialog_RemovePost(Product product) {
        deleteProductTask = new DeleteProductTask(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Xóa sản phẩm");
        builder.setMessage("Bạn có muốn xoá sản phẩm này không?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id_product = product.getId();
                deleteProductTask.execute(id_product);
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Tạo và hiển thị hộp thoại
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 100) {
                Intent intent = requireActivity().getIntent(); // Lấy Intent mà đã được sử dụng để bắt đầu hoạt động hiện tại.
                requireActivity().finish(); // Đóng hoạt động hiện tại.
                requireActivity().startActivity(intent); // Khởi chạy lại hoạt động hiện tại bằng Intent đã lấy ở bước trước.
            }
        });
    }

}