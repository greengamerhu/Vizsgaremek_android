package hu.petrik.vizsgaremek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private ListView listViewMenu;

    private Button buttonLogout;

    public List<Menu> menuList = new ArrayList<>();
    private String url =  "http://10.0.2.2:3000/menu";

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
        RequestTask task = new RequestTask(url, "GET");
        task.execute();



        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logoutUrl =  "http://10.0.2.2:3000/auth/logout";
                RequestTask task1 = new RequestTask(logoutUrl, "DELETE");
                task1.execute();

            }
        });
    }


    public void init() {
        listViewMenu = findViewById(R.id.listViewMenu);
        buttonLogout = findViewById(R.id.buttonLogout);


    }



    private class MenuAdapter extends ArrayAdapter<Menu> {

        public MenuAdapter() {
            super(MenuActivity.this, R.layout.list_menu_items, menuList);


        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            Toast.makeText(MenuActivity.this, "" + menuList.size(), Toast.LENGTH_SHORT).show();
//            Log.d("MenuAdapter", "getView called for position: " + position);
            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_menu_items, null, false);
            Menu actualMenu = menuList.get(position);
            TextView textViewTitle = view.findViewById(R.id.textViewItemTitle);
            TextView textViewDescrip = view.findViewById(R.id.textViewItemDesc);
            TextView textViewPrice = view.findViewById(R.id.textViewItemPrice);

            Log.d("MenuAdapter", "Title: " + actualMenu.getFood_name());
            Log.d("MenuAdapter", "Description: " + actualMenu.getFood_description());
            Log.d("MenuAdapter", "Price: " + actualMenu.getFood_price());
            textViewTitle.setText(actualMenu.getFood_name());
            textViewDescrip.setText(actualMenu.getFood_description());

            textViewPrice.setText(String.valueOf(actualMenu.getFood_price()));
            return view;
        }
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
                        response = RequestHandler.get(requestUrl,null);
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl, sharedPreferences.getString("token", null));
                        break;
                }
            }catch (IOException e) {
                Toast.makeText(MenuActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
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
            Log.d("responeGet", "" +response.getContent()) ;
            if (response.getResponseCode() >= 400) {
                Toast.makeText(MenuActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            switch (requestType) {
                case "GET":
                    Menu[] menuArray = converter.fromJson(response.getContent(), Menu[].class);
                    menuList.clear();
                    menuList.addAll(Arrays.asList(menuArray));
                    MenuAdapter adapter = new MenuAdapter();
                    listViewMenu.setAdapter(adapter);
                    break;
                case "DELETE":
                    SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Log.d("token", "onPostExecute: " + sharedPreferences.getString("token", null));
                    editor.remove("token");
                    editor.apply();
                    Intent intent = new Intent( MenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                    editor.clear().apply();
                    finish();


            }

        }
    }
    }