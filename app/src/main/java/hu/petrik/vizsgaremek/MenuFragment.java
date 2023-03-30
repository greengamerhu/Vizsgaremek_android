package hu.petrik.vizsgaremek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MenuFragment extends Fragment {
    private ListView listViewMenu;
    public List<Menu> menuList = new ArrayList<>();
    private String url = "http://10.0.2.2:3000/menu";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                MenuItemFragment itemFragment = new MenuItemFragment();
                Menu menuItem = (Menu) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("menuItem",  menuItem);
                itemFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, itemFragment)
                        .commit();
                Toast.makeText(getActivity(), "" + menuItem.getFood_name(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    public void init(View view){
        listViewMenu = view.findViewById(R.id.listViewMenu);
//        recyclerView = view.findViewById(R.id.menuRecyclerView);
        Toast.makeText(getActivity(), "szia", Toast.LENGTH_SHORT).show();

    }

    private class MenuAdapter extends ArrayAdapter<Menu> {

        public MenuAdapter() {
            super(listViewMenu.getContext(), R.layout.list_menu_items, menuList);


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_menu_items, null, false);
            Menu actualMenu = menuList.get(position);
            TextView textViewTitle = view.findViewById(R.id.textViewItemTitle);
            TextView textViewDescrip = view.findViewById(R.id.textViewItemDesc);
            TextView textViewPrice = view.findViewById(R.id.textViewItemPrice);
            ImageView imageViewListMenuItems = view.findViewById(R.id.imageViewListMenuItems);

            Log.d("MenuAdapter", "Title: " + actualMenu.getFood_name());
            Log.d("MenuAdapter", "Description: " + actualMenu.getFood_description());
            Log.d("MenuAdapter", "Price: " + actualMenu.getFood_price());

            Picasso.get().load("http://10.0.2.2:3000/burgers/burger1.png").into(imageViewListMenuItems);
            textViewTitle.setText(actualMenu.getFood_name());
            textViewDescrip.setText(actualMenu.getFood_description());
            textViewPrice.setText(actualMenu.getFood_price() + " Ft");
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
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl, null);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
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
            Gson converter = new Gson();
            Log.d("responeGet", "" + response.getContent());
            if (response.getResponseCode() >= 400) {
                Toast.makeText(getActivity(),
                        "Hiba történt a kérés feldolgozása során",
                        Toast.LENGTH_SHORT).show();
            }
            switch (requestType) {
                case "GET":
                    MenuListHelper menuListHelper = converter.fromJson(response.getContent(), MenuListHelper.class);
                    menuList.clear();
                    menuList.addAll(menuListHelper.getMenus());
                    for (Menu m : menuList) {
                        Log.d("MenuAdapter", "" + m.getFood_name());

                    }

                    MenuAdapter adapter = new MenuAdapter();
                    listViewMenu.setAdapter(adapter);
                    break;



            }

        }
    }
}