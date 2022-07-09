package com.erranddaddy.pandaeats;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.erranddaddy.pandaeats.adapter.MenuListAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retaurant_menu);

        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        menuList = restaurantModel.getMenus();
        initRecyclerView();

        TextView buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
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
        totalItemInCart = 0;

        for (Menu m: itemsInCartList) {
            totalItemInCart = totalItemInCart +  menu.getTotalInCart();
        }
        buttonCheckout.setText("Checkout (" + totalItemInCart + ") items" );
    }

    @Override
    public void onUpdateCartClick(Menu menu) {
        if (itemsInCartList.contains(menu)) {
            int index =
        }
    }

    @Override
    public void onRemoveFromCartClick(Menu menu) {

    }
}