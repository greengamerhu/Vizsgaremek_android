package hu.petrik.vizsgaremek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class MenuItemFragment extends Fragment {
    private TextView textViewShowMenuItemTitle;
    private TextView textViewShowMenuItemDesc;
    private TextView textViewShowMenuItemCategory;
    private TextView textViewShowMenuItemPrice;
    private TextView buttonAddToCart;
    private ImageView imageViewShowItemImg;
    private CircularProgressIndicator addToCartProgress;
    private String url = "http://10.0.2.2:3000/cart";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_item, container, false);
        init(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Menu receivedItem = bundle.getParcelable("menuItem"); // Key
            Picasso.get().load("http://10.0.2.2:3000/burgers/" + receivedItem.getFood_image()).into(imageViewShowItemImg);
            textViewShowMenuItemTitle.setText(receivedItem.getFood_name());
            textViewShowMenuItemDesc.setText(receivedItem.getFood_description());
            textViewShowMenuItemCategory.setText(receivedItem.getFood_category());
            textViewShowMenuItemPrice.setText(String.valueOf(receivedItem.getFood_price()) + " Ft");

            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToCartProgress.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    CartItems cartItems = new CartItems();
                    cartItems.menuItem = receivedItem;
                    cartItems.quantity = 1;
                    RequestTask task = new RequestTask(url, "POST", gson.toJson(cartItems));
                    task.execute();
                }
            });
        }
        return view;
    }


    public void init(View view) {
        textViewShowMenuItemTitle = view.findViewById(R.id.textViewShowMenuItemTitle);
        textViewShowMenuItemDesc = view.findViewById(R.id.textViewShowMenuItemDesc);
        textViewShowMenuItemCategory = view.findViewById(R.id.textViewShowMenuItemCategory);
        textViewShowMenuItemPrice = view.findViewById(R.id.textViewShowMenuItemPrice);
        addToCartProgress = view.findViewById(R.id.addToCartProgress);
        buttonAddToCart = view.findViewById(R.id.buttonAddToCart);
        imageViewShowItemImg = view.findViewById(R.id.imageViewShowItemImg);
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
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Important", Context.MODE_PRIVATE);
            try {
                switch (requestType) {
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams, sharedPreferences.getString("token", null));
                }
            } catch (IOException e) {
                addToCartProgress.setVisibility(View.GONE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
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
                case "POST":
                    addToCartProgress.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                        .replace(R.id.fragmentContainer, new MenuFragment()).commit();
                    break;



            }

        }
    }
}