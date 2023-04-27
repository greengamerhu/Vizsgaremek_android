package hu.petrik.vizsgaremek;

import android.app.Dialog;
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
import com.google.gson.GsonBuilder;
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
    private TextView textViewToolBarTitle;



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
                        .setCustomAnimations(R.anim.fragmentslide_in,R.anim.fragnentfade_out, R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                        .replace(R.id.fragmentContainer, itemFragment)
                        .commit();
            }
        });
        return view;

    }

    public void init(View view){
        textViewToolBarTitle = getActivity().findViewById(R.id.textViewToolBarTitle);
        textViewToolBarTitle.setText("Menü");
        listViewMenu = view.findViewById(R.id.listViewMenu);
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



            Picasso.get().load("http://10.0.2.2:3000/burgers/" + actualMenu.getFood_image()).into(imageViewListMenuItems);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null) {
                DialogBuilderHelper builderHelper = new DialogBuilderHelper(getActivity());
                Dialog dialog = builderHelper.createServerErrorDialog();
                dialog.show();
                return;
            }
            if (response.getResponseCode() >= 400) {
                converter = new GsonBuilder().registerTypeAdapter(ErrorFromServer.class, new ErrorFromServerDeserializer()).create();
                ErrorFromServer error = converter.fromJson(response.getContent(), ErrorFromServer.class);

                DialogBuilderHelper dialog = new DialogBuilderHelper(error, getActivity());
                dialog.createDialog().show();
            }
            switch (requestType) {
                case "GET":
                    MenuListHelper menuListHelper = converter.fromJson(response.getContent(), MenuListHelper.class);
                    menuList.clear();
                    menuList.addAll(menuListHelper.getMenus());


                    MenuAdapter adapter = new MenuAdapter();
                    listViewMenu.setAdapter(adapter);
                    break;



            }

        }
    }
}