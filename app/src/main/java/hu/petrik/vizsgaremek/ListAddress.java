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

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ListAddress extends Fragment {
    private String url = "http://10.0.2.2:3000/user-adress";
    private List<Address> addressList = new ArrayList<>();
    private ListView listViewAdress;
    private MaterialButton buttonNewAdress;
    private TextView textVuewAddNewAddresspls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_adress, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        buttonNewAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addressList.size() >= 3) {
                    DynamicToast.makeWarning(getActivity(), "Már felvettél három szállítási címet").show();
                    return;
                }
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new  Add_addressFragment())
                        .commit();
            }
        });
        return view;

    }
    public void init(View view) {
        listViewAdress = view.findViewById(R.id.listViewAdress);
        buttonNewAdress = view.findViewById(R.id.buttonNewAdress);
        textVuewAddNewAddresspls = view.findViewById(R.id.textVuewAddNewAddresspls);
    }
    private class AddressAdapter extends ArrayAdapter<Address> {

        public AddressAdapter() {
            super(listViewAdress.getContext(), R.layout.list_adress_items, addressList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_adress_items, null, false);
            Address actualAddress = addressList.get(position);
            Log.d("deleteUrl", "getView: " + actualAddress.getId());

            TextView textViewItemAdress = view.findViewById(R.id.textViewItemAdress);
            TextView textViewItemPostalCode = view.findViewById(R.id.textViewItemPostalCode);
            TextView textViewiItemCity = view.findViewById(R.id.textViewItemCity);
            TextView textViewiItemPhone = view.findViewById(R.id.textViewItemPhone);
            ImageView imgaeViewDeleteAdress = view.findViewById(R.id.imgaeViewDeleteAdress);
            String deleteUrl = "http://10.0.2.2:3000/user-adress/" + String.valueOf(actualAddress.getId());
            Log.d("deleteUrl", "getView: " + deleteUrl);
            RequestTask task = new RequestTask(deleteUrl, "DELETE");
            textViewItemAdress.setText(actualAddress.getAddress());
            textViewItemPostalCode.setText(String.valueOf(actualAddress.getPostalCode()));
            textViewiItemCity.setText(actualAddress.getCity());
            textViewiItemPhone.setText(actualAddress.getMobileNumber());
            imgaeViewDeleteAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.execute();
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
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl, sharedPreferences.getString("token", null));
                        Log.d("delete", "doInBackground: " + response.getContent());
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
//            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            //Log.d("deleteUrl", "onPostExecute: " + response.getContent());
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
                    AddressListHelper addressListHelper = converter.fromJson(response.getContent(), AddressListHelper.class);
                    addressList.clear();
                    addressList.addAll(addressListHelper.getAddresses());
                    if (addressList.size() == 0) {

                        textVuewAddNewAddresspls.setVisibility(View.VISIBLE);
                        buttonNewAdress.setVisibility(View.VISIBLE);
                        listViewAdress.setVisibility(View.GONE);
                        return;
                    }

                    textVuewAddNewAddresspls.setVisibility(View.GONE);
                    buttonNewAdress.setVisibility(View.VISIBLE);
                    listViewAdress.setVisibility(View.VISIBLE);
                    AddressAdapter adapter = new AddressAdapter();
                    listViewAdress.setAdapter(adapter);
                    break;
                case "DELETE":
                    RequestTask task = new RequestTask(url, "GET");
                    task.execute();
                    if(response.getResponseCode() == 200) {
                        DynamicToast.makeSuccess(getActivity(), "Cím törölve").show();

                    } else {
                        DynamicToast.makeError(getActivity(), "Cím törlése sikertelen").show();

                    }

                    break;
            }


        }
    }
}