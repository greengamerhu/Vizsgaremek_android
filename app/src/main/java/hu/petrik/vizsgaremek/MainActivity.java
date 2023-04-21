package hu.petrik.vizsgaremek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public TextView textViewToolBarTitle;

    private ActionBarDrawerToggle toggle;
    private FrameLayout frameLayout;
    private ImageView imageViewCart;
    private TextView textViewCartItemsCounter;

    private List<CartItems> cart = new ArrayList<>();
    private String url = "http://10.0.2.2:3000/menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        teljesképernyő beállítása
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //alapértelmezett actionbar elrejtése
        getSupportActionBar().hide();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MenuFragment()).commit();
                        textViewToolBarTitle.setText("Menü");
                        imageViewCart.setVisibility(View.VISIBLE);
                        textViewCartItemsCounter.setVisibility(View.VISIBLE);
                        break;
                    case R.id.adressPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ListAddress()).commit();
                        imageViewCart.setVisibility(View.GONE);
                        textViewCartItemsCounter.setVisibility(View.GONE);

                        textViewToolBarTitle.setText("Szállítási adatok");
                        break;
                    case R.id.ordersPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new OrdersFragment()).commit();
                        imageViewCart.setVisibility(View.GONE);
                        textViewCartItemsCounter.setVisibility(View.GONE);

                        textViewToolBarTitle.setText("Rendelések");
                        break;
                    case R.id.logoutPage:
                        String logoutUrl = "http://10.0.2.2:3000/auth/logout";
                        RequestTask task1 = new RequestTask(logoutUrl, "DELETE");

                        task1.execute();
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        imageViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new cartItemsFragment()).commit();
                textViewToolBarTitle.setText("Kosár");
            }
        });
        RequestTask task1 = new RequestTask("http://10.0.2.2:3000/cart", "GET");
        task1.execute();


    }


    public void init() {
//        listViewMenu = findViewById(R.id.listViewMenu);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        frameLayout = findViewById(R.id.fragmentContainer);
        textViewToolBarTitle = findViewById(R.id.textViewToolBarTitle);
        imageViewCart = findViewById(R.id.imageViewCart);
        textViewCartItemsCounter = findViewById(R.id.textViewCartItemsCounter);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.Open, R.string.Closed);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.purple) );
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MenuFragment()).commit();
        textViewToolBarTitle.setText("Menü");

    }

    @Override
    public void onBackPressed() {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof  MenuItemFragment || currentFragment instanceof cartItemsFragment ) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                    .replace(R.id.fragmentContainer, new MenuFragment()).commit();
            textViewToolBarTitle.setText("Menü");
            return;
        }
        if (currentFragment instanceof ChooseAddressFragment) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                    .replace(R.id.fragmentContainer, new cartItemsFragment()).commit();
            textViewToolBarTitle.setText("Kosár");
            return;
        }
        if(currentFragment instanceof placeOrderFragment) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                    .replace(R.id.fragmentContainer, new ChooseAddressFragment()).commit();
            textViewToolBarTitle.setText("Válassz");
            return;
        }
        if(currentFragment instanceof  Add_addressFragment) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                    .replace(R.id.fragmentContainer, new ListAddress()).commit();
            return;
        }

        super.onBackPressed();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }


        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
                SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl, sharedPreferences.getString("token", null));
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl, sharedPreferences.getString("token", null));
                        break;
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
//            progressBar.setVisibility(View.GONE);
            Gson converter = new Gson();
            if (response == null) {
                DialogBuilderHelper builderHelper = new DialogBuilderHelper(MainActivity.this);
                Dialog dialog = builderHelper.createServerErrorDialog();
                dialog.show();
                return;
            }
            if (response.getResponseCode() >= 400) {
                converter = new GsonBuilder().registerTypeAdapter(ErrorFromServer.class, new ErrorFromServerDeserializer()).create();
                ErrorFromServer error = converter.fromJson(response.getContent(), ErrorFromServer.class);
                DialogBuilderHelper dialog = new DialogBuilderHelper(error, MainActivity.this);
                dialog.createDialog().show();
            }
            switch (requestType) {
                case "GET":
                    CartItemListHelper cartItemListHelper = converter.fromJson(response.getContent(), CartItemListHelper.class);
                    cart.clear();
                    cart.addAll(cartItemListHelper.getshoppingCart());
                    textViewCartItemsCounter.setText(cart.size() + "");
                    break;
                case "DELETE":
                    SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Log.d("token", "onPostExecute: " + sharedPreferences.getString("token", null));
                    editor.remove("token");
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    editor.clear().apply();
                    finish();


            }

        }
    }
}