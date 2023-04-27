package hu.petrik.vizsgaremek;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class OrdersFragment extends Fragment {
    private List<Order> historyOrdersList = new ArrayList<>();
    private List<Order> activeOrders  = new ArrayList<>();
    private ListView listViewHistoryOrder;
    private ListView listViewActiveOrder;
    private TextView textViewActiveOrder;
    private TextView textViewHistoryOrder;
    private  TextView textViewNoOrders;
    private Order activeOrder;
    final private String  url = "http://10.0.2.2:3000/order/getOrders";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        return view;
    }

    public void init(View view) {
        listViewHistoryOrder = view.findViewById(R.id.listViewHistoryOrder);
        listViewActiveOrder = view.findViewById(R.id.listViewActiveOrder);
        textViewActiveOrder = view.findViewById(R.id.textViewActiveOrder);
        textViewHistoryOrder = view.findViewById(R.id.textViewHistoryOrder);
        textViewNoOrders = view.findViewById(R.id.textViewNoOrders  );
    }

    private class OrderHistoryAdapter extends ArrayAdapter<Order> {

        public OrderHistoryAdapter() {
            super(listViewHistoryOrder.getContext(), R.layout.list_order_items, historyOrdersList);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_order_items, null, false);
            Order actualOrder = historyOrdersList.get(position);
            TextView textViewOrderItemDate = view.findViewById(R.id.textViewOrderItemDate);
            TextView textViewOrderStatus = view.findViewById(R.id.textViewOrderStatus);
            TextView textViewOrderId = view.findViewById(R.id.textViewOrderId);
            TextView textViewOrderSummary = view.findViewById(R.id.textViewOrderSummary);

            SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            textViewOrderItemDate.setText(Dateformat.format(actualOrder.getOrderDate())  + " ");
            System.out.println(Dateformat.format(actualOrder.getOrderDate()));
            textViewOrderStatus.setText("Státusz: "+ actualOrder.getStatus());
            textViewOrderId.setText("Azon: " +actualOrder.getId());
            textViewOrderSummary.setText("Összesen: " + actualOrder.getTotal());
            return view;
        }

    } private class ActiveOrderAdapter extends ArrayAdapter<Order> {

        public ActiveOrderAdapter() {
            super(listViewActiveOrder.getContext(), R.layout.list_order_items, activeOrders);
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_order_items, null, false);
            Order actualOrder = activeOrders.get(position);
            TextView textViewOrderItemDate = view.findViewById(R.id.textViewOrderItemDate);
            TextView textViewOrderStatus = view.findViewById(R.id.textViewOrderStatus);
            TextView textViewOrderId = view.findViewById(R.id.textViewOrderId);
            TextView textViewOrderSummary = view.findViewById(R.id.textViewOrderSummary);
            SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            textViewOrderItemDate.setText(Dateformat.format(actualOrder.getOrderDate())  + " ");
            textViewOrderStatus.setText("Státusz: "+ actualOrder.getStatus());
            textViewOrderId.setText("Azon: " +actualOrder.getId());
            textViewOrderSummary.setText("Összesen: " + actualOrder.getTotal());
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
                DynamicToast.makeError(getActivity(), "Lekérdezés sikertelen").show();

            }
            switch (requestType) {
                case "GET":
                    OrderListHelper orderListHelper = converter.fromJson(response.getContent(), OrderListHelper.class);
                    historyOrdersList.addAll(orderListHelper.getOrderHistory());

                    /**
                     * Ez az aktuális / korábbi / illletve ha nincs rendelés mi jelenjen meg
                     */

                    activeOrder = orderListHelper.getActiveOrder();
                    if(activeOrder != null) {
                        activeOrders.add(activeOrder);
                    }
                    if(activeOrders.size() == 0 && historyOrdersList.size() == 0) {
                        textViewHistoryOrder.setVisibility(View.GONE);
                        textViewActiveOrder.setVisibility(View.GONE);
                        listViewActiveOrder.setVisibility(View.GONE);
                        listViewHistoryOrder.setVisibility(View.GONE);
                        textViewNoOrders.setVisibility(View.VISIBLE);
                        return;
                    }

                    if(historyOrdersList.size() !=0) {
                        textViewNoOrders.setVisibility(View.GONE);
                        textViewHistoryOrder.setVisibility(View.VISIBLE);
                        listViewHistoryOrder.setVisibility(View.VISIBLE);
                        OrderHistoryAdapter adapter = new OrderHistoryAdapter();
                        listViewHistoryOrder.setAdapter(adapter);
                    } else {
                        textViewNoOrders.setVisibility(View.GONE);
                        textViewHistoryOrder.setVisibility(View.GONE);
                        listViewHistoryOrder.setVisibility(View.GONE);
                    }

                    if(activeOrders.size() != 0) {
                        textViewNoOrders.setVisibility(View.GONE);
                        textViewActiveOrder.setVisibility(View.VISIBLE);
                        listViewActiveOrder.setVisibility(View.VISIBLE);
                        ActiveOrderAdapter adapter2 = new ActiveOrderAdapter();
                        listViewActiveOrder.setAdapter(adapter2);
                    } else {
                        textViewNoOrders.setVisibility(View.GONE);
                        textViewActiveOrder.setVisibility(View.GONE);
                        listViewActiveOrder.setVisibility(View.GONE);
                    }
                    break;



            }

        }
    }
}