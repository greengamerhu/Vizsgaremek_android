package hu.petrik.vizsgaremek;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Layout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChooseAddressFragment extends Fragment {
    private String url = "http://10.0.2.2:3000/user-address";
    private ListView listViewchooseAddress;
    private List<Address> addressList = new ArrayList<>();
    private TextView textViewAdressChoice;
    private TextView textViewToolBarTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_address, container, false);
        init(view);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();


        listViewchooseAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                placeOrderFragment itemFragment = new placeOrderFragment();
                Address address = (Address) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("choosenAddress",  address);
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

    public void init(View view) {
        textViewToolBarTitle = getActivity().findViewById(R.id.textViewToolBarTitle);
        listViewchooseAddress = view.findViewById(R.id.listViewchooseAddress);
        textViewAdressChoice = view.findViewById(R.id.textViewAdressChoice);
       textViewToolBarTitle.setText("Válassz");
    }
    private class AddressAdapter extends ArrayAdapter<Address> {

        public AddressAdapter() {
            super(listViewchooseAddress.getContext(), R.layout.list_adress_items, addressList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();

            View view = layoutInflater.inflate(R.layout.list_adress_items, null, false);
            Address actualAddress = addressList.get(position);
            Log.d("address", "getView: " + actualAddress.getId());
            TextView textViewItemAdress = view.findViewById(R.id.textViewItemAdress);
            TextView textViewItemPostalCode = view.findViewById(R.id.textViewItemPostalCode);
            TextView textViewiItemCity = view.findViewById(R.id.textViewItemCity);
            TextView textViewiItemPhone = view.findViewById(R.id.textViewItemPhone);
            ImageView imgaeViewDeleteAdress = view.findViewById(R.id.imgaeViewDeleteAdress);
            imgaeViewDeleteAdress.setVisibility(View.GONE);
            textViewItemAdress.setText(actualAddress.getAddress());
            textViewItemPostalCode.setText(String.valueOf(actualAddress.getPostalCode()));
            textViewiItemCity.setText(actualAddress.getCity());
            textViewiItemPhone.setText(actualAddress.getMobileNumber());

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
                        textViewAdressChoice.setText("Még nem vettél fel szállítási címet");
                    } else {
                        textViewAdressChoice.setText("Válasz egy Szállítási címet:");
                    }
                    AddressAdapter adapter = new AddressAdapter();
                    listViewchooseAddress.setAdapter(adapter);
                    break;

            }


        }
    }
}