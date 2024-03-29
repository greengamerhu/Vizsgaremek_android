package hu.petrik.vizsgaremek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class cartItemsFragment extends Fragment {
    final private String  url = "http://10.0.2.2:3000/cart";
    private ListView listViewCart;
    private List<CartItems> cartItemsList = new ArrayList<>();
    private String sumTotal;
    private MaterialButton buttonOrder;
    private TextView textViewEmptyCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cart_items, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        return view;
    }

    public void init(View view) {
        listViewCart = view.findViewById(R.id.listViewCart);
        buttonOrder = view.findViewById(R.id.buttonOrder);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);

    }

    /**
     * A módosított mennyiséget update-li az adatbázisban
     * @param cartItem a módosított a mennyisége az adott kosár itemnek
     */
    public void addTodb(CartItems cartItem) {
        Gson gson = new Gson();

        RequestTask task = new RequestTask(url, "PATCH", gson.toJson(cartItem));
        task.execute();
    }
    private class CartItemsAdapter extends ArrayAdapter<CartItems> {

        public CartItemsAdapter() {
            super(listViewCart.getContext(), R.layout.list_cart_items, cartItemsList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_cart_items, null, false);
            CartItems actualCartItem = cartItemsList.get(position);

            TextView textViewCartItemTittle = view.findViewById(R.id.textViewCartItemTittle);
            TextView textViewCartItemPrice = view.findViewById(R.id.textViewCartItemPrice);
            TextView textViewCartItemDesc = view.findViewById(R.id.textViewCartItemDesc);
            TextView textViewCartItemQuantity = view.findViewById(R.id.textViewCartItemQuantity);
            MaterialButton buttonCartLowerQuantity = view.findViewById(R.id.buttonCartLowerQuantity);
            MaterialButton buttonCartAddQuantity = view.findViewById(R.id.buttonCartAddQuantity);
            textViewCartItemTittle.setText(actualCartItem.menuItem.getFood_name());
            textViewCartItemPrice.setText(actualCartItem.total + " Ft");
            textViewCartItemDesc.setText(actualCartItem.menuItem.getFood_description());
            textViewCartItemQuantity.setText(String.valueOf(actualCartItem.quantity));
            buttonCartLowerQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actualCartItem.quantity -=1;
                    if (actualCartItem.quantity == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Szeretnéd törölni a kosaradból?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RequestTask task = new RequestTask(url + "/" + actualCartItem.id, "DELETE");
                                task.execute();
                            }
                        });
                        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                actualCartItem.quantity +=1;
                                addTodb(actualCartItem);

                            }
                        });
                        builder.create();
                        builder.show();

                    } else {
                        addTodb(actualCartItem);

                    }

//
                }
            });
            buttonCartAddQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actualCartItem.quantity +=1;

                    addTodb(actualCartItem);

                }
            });
            buttonOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fragmentslide_in,R.anim.fragnentfade_out, R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                            .replace(R.id.fragmentContainer, new ChooseAddressFragment())
                            .commit();
                }
            });

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
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Important", Context.MODE_PRIVATE);
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl, sharedPreferences.getString("token", null));
                        break;
                    case "PATCH":
                        response = RequestHandler.put(requestUrl, requestParams, sharedPreferences.getString("token", null));
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl, sharedPreferences.getString("token", null));
                        break;
                }
            } catch (IOException e) {
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
                    CartItemListHelper cartItemListHelper = converter.fromJson(response.getContent(), CartItemListHelper.class);
                    sumTotal = cartItemListHelper.getSumTotal();
                    cartItemsList.clear();
                    cartItemsList.addAll(cartItemListHelper.getshoppingCart());

                    TextView textViewCartItemsCounter = getActivity().findViewById(R.id.textViewCartItemsCounter);
                    textViewCartItemsCounter.setText(cartItemsList.size() + "");

                    if (cartItemsList.size() == 0){
                        buttonOrder.setVisibility(View.GONE);
                        listViewCart.setVisibility(View.GONE);
                        textViewEmptyCart.setVisibility(View.VISIBLE);
                        return;
                    }
                    CartItemsAdapter adapter = new CartItemsAdapter();
                    listViewCart.setAdapter(adapter);
                    buttonOrder.setText("Megrendelés: "+ Integer.parseInt(cartItemListHelper.getSumTotal()) + " Ft");
                    break;

                case "PATCH":
                case "DELETE":
                    RequestTask task = new RequestTask(url, "GET");
                    task.execute();
                    adapter = new CartItemsAdapter();
                    listViewCart.setAdapter(adapter);
                    break;
            }

        }
    }
}