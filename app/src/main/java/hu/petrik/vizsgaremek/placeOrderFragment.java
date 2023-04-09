package hu.petrik.vizsgaremek;

import android.content.Context;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class placeOrderFragment extends Fragment {
    private Address recivedAddress;
    private ListView listViewOrderSummary;
    private List<CartItems> orderItemsList = new ArrayList<>();
    private TextView textViewOrderSummary;
    final private String  url = "http://10.0.2.2:3000/cart";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_order, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
             recivedAddress= bundle.getParcelable("choosenAddress");
        } else {
            Toast.makeText(getActivity(), "Hiba", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void init(View view) {

        listViewOrderSummary = view.findViewById(R.id.listViewOrderSummary);
        textViewOrderSummary = view.findViewById(R.id.textViewOrderSummary);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private class OrderItemsAdapter extends ArrayAdapter<CartItems> {

        public OrderItemsAdapter() {
            super(listViewOrderSummary.getContext(), R.layout.list_order_summary, orderItemsList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_order_summary, null, false);
            CartItems actualOrderItem = orderItemsList.get(position);
            TextView textViewOrderItemName = view.findViewById(R.id.textViewOrderItemName);
            TextView textViewOrderItemQuantity = view.findViewById(R.id.textViewOrderItemQuantity);
            TextView textViewOrderItemprice = view.findViewById(R.id.textViewOrderItemprice);
            textViewOrderItemName.setText(actualOrderItem.menuItem.getFood_name());
            textViewOrderItemQuantity.setText(actualOrderItem.quantity + " db");
            textViewOrderItemprice.setText(actualOrderItem.total + "Ft");


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
            Log.d("cartError", "" + response.getContent());
            if (response.getResponseCode() >= 400) {
                Toast.makeText(getActivity(),
                        "Hiba történt a kérés feldolgozása során" ,
                        Toast.LENGTH_SHORT).show();
            }
            switch (requestType) {
                case "GET":
                    String sumTotal;
                    CartItemListHelper cartItemListHelper = converter.fromJson(response.getContent(), CartItemListHelper.class);
                    sumTotal = cartItemListHelper.getSumTotal();
                    orderItemsList.clear();
                    orderItemsList.addAll(cartItemListHelper.getshoppingCart());
                    OrderItemsAdapter adapter = new OrderItemsAdapter();
                    listViewOrderSummary.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(listViewOrderSummary);
                    listViewOrderSummary.setScrollContainer(false);
                    textViewOrderSummary.setText("Összesen: " + sumTotal + " Ft");
                    break;

            }

        }
    }
}