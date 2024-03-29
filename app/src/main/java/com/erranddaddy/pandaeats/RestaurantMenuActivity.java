package com.erranddaddy.pandaeats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.erranddaddy.pandaeats.adapters.MenuListAdapter;
import com.erranddaddy.pandaeats.model.Menu;
import com.erranddaddy.pandaeats.model.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity implements MenuListAdapter.MenuListClickListener {
    private List<Menu> menuList = null;
    private MenuListAdapter menuListAdapter;
    private List<Menu> itemsInCartList;
    private int totalItemInCart = 0;
    private TextView buttonCheckout;

//    enum RequestCode {
//        SUCCESS = 1000,
//        FAIL
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        menuList = restaurantModel.getMenus();
        initRecyclerView();

        buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(view -> {
            if (itemsInCartList != null && itemsInCartList.size() <= 0) {
                Toast.makeText(RestaurantMenuActivity.this, "Please add some items in cart", Toast.LENGTH_SHORT).show();
                return;
            }
            restaurantModel.setMenus(itemsInCartList);
            Intent intent = new Intent(RestaurantMenuActivity.this, PlaceYourOrderActivity.class);
            intent.putExtra("RestaurantModel", restaurantModel);
            startActivityForResult(intent, 1000);
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        menuListAdapter = new MenuListAdapter(menuList, this);

        recyclerView.setAdapter(menuListAdapter);
    }

//    @Override
//    public void onItemClick(Menu menu) {
//
//    }

    @Override
    public void onAddToCartClick(Menu menu) {
        if (itemsInCartList == null) {
            itemsInCartList = new ArrayList<>();
        }
        itemsInCartList.add(menu);
//        totalItemInCart = 0;
//
//        for (Menu m: itemsInCartList) {
//            totalItemInCart = totalItemInCart +  m.getTotalInCart();
//        }
        totalItemInCart += menu.getTotalInCart();
        buttonCheckout.setText(getString(R.string.CheckoutItems) + totalItemInCart + getString(R.string.Items) );
    }

    @Override
    public void onUpdateCartClick(Menu menu) {
        if (itemsInCartList.contains(menu)) {
            int index = itemsInCartList.indexOf(menu);
            itemsInCartList.remove(index);
            itemsInCartList.add(index, menu);
            totalItemInCart = 0;

            for (Menu m: itemsInCartList) {
                totalItemInCart = totalItemInCart + m.getTotalInCart();
            }
            buttonCheckout.setText(getString(R.string.CheckoutItems) + totalItemInCart + getString(R.string.Items) );
        }
    }

    @Override
    public void onRemoveFromCartClick(Menu menu) {
        if (itemsInCartList.contains(menu)) {
            itemsInCartList.remove(menu);
//            totalItemInCart = 0;
//
//            for (Menu m: itemsInCartList) {
//                totalItemInCart = totalItemInCart + m.getTotalInCart();
//            }
            totalItemInCart -= menu.getTotalInCart();
            buttonCheckout.setText(getString(R.string.CheckoutItems) + totalItemInCart + getString(R.string.Items) );
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                // nothing happen
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == RequestCode.SUCCESS && resultCode == Activity.RESULT_OK) {
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

}