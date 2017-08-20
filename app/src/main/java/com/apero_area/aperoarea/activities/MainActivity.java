package com.apero_area.aperoarea.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.apero_area.aperoarea.ApiClient;
import com.apero_area.aperoarea.R;
import com.apero_area.aperoarea.adapters.RecyclerAdapter;
import com.apero_area.aperoarea.interfaces.ApiInterface;
import com.apero_area.aperoarea.models.Product;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private List<Product> products;
    private ApiInterface apiInterface;
    private ProgressDialog progress;
    private AlertDialog alertDialog;
    private Menu mMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setHasFixedSize(true);

        

        progress = ProgressDialog.show(this, "Chargement des données",
                "En cours de chargement, veuillez patienter", true);

        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Pas de connexion");
        alertDialog.setMessage("Merci d'activer votre connexion internet");

        // Build of the retrofit object
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        // Make the request
        Call<List<Product>> call = apiInterface.getProduct();
        //
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progress.dismiss();

                products = response.body();
                if (products.size() != 0) {
                    adapter = new RecyclerAdapter(MainActivity.this, products, new RecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Product products) {
                            Log.d("test","click " + products);

                            Intent intent = new Intent(MainActivity.this, ProductViewActivity.class);
                            Gson gson = new Gson();
                            String productJson = gson.toJson(products);
                            intent.putExtra("productJson", productJson);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progress.dismiss();
                alertDialog.show();
                Log.d("test", "echec" + t);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mMenu = menu;
        return true;
    }


    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_checkout:
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                startActivity(intent);

            case R.id.cart:
                Intent intent2 = new Intent(this, Cart.class);
                this.startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setBadgeCount(String count) {

        Context context = getApplicationContext();
        MenuItem itemCart = mMenu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

}
