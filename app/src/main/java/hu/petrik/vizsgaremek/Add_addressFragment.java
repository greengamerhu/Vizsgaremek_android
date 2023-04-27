package hu.petrik.vizsgaremek;


import android.content.Context;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;


public class Add_addressFragment extends Fragment {
    private String url = "http://10.0.2.2:3000/user-address";

    private EditText editTextAdressPostalCode;
    private EditText editTextAdressCity;
    private EditText editTextAdressName;
    private EditText editTextAdressPhone;
    private TextInputLayout postalCodelayout;
    private TextInputLayout cityLayout;
    private TextInputLayout AdrdessLayout;
    private TextInputLayout phoneLayout;
    private Button buttonAdressSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);
        init(view);
        buttonAdressSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postalCode =  editTextAdressPostalCode.getText().toString();
                Log.d("address", "onClick: " + postalCode);
                String city = editTextAdressCity.getText().toString().trim();
                String address = editTextAdressName.getText().toString().trim();
                String phone = editTextAdressPhone.getText().toString();
                if(postalCode.isEmpty()) {
                    postalCodelayout.setError("A Irányítószám nem lehet üres");
                    return;
                } else {
                    postalCodelayout.setError(null);
                }
                if(city.isEmpty()) {
                    cityLayout.setError("A város nem lehet üres");
                    return;

                } else {
                    cityLayout.setError(null);
                }
                if(address.isEmpty()) {
                    AdrdessLayout.setError("A város nem lehet üres");
                    return;
                } else {
                    AdrdessLayout.setError(null);
                }
                Gson gson = new Gson();
                Address newAdress = new Address(0, address, city, postalCode, phone);

                RequestTask task = new RequestTask(url, "POST", gson.toJson(newAdress));
                task.execute();
            }
        });
        return view;
    }

    public void init(View view) {
        editTextAdressPostalCode = view.findViewById(R.id.editTextAdressPostalCode);
        editTextAdressCity = view.findViewById(R.id.editTextAdressCity);
        editTextAdressName = view.findViewById(R.id.editTextAdressName);
        editTextAdressPhone = view.findViewById(R.id.editTextAdressPhone);
        postalCodelayout = view.findViewById(R.id.postalCodelayout);
        cityLayout = view.findViewById(R.id.cityLayout);
        AdrdessLayout = view.findViewById(R.id.AdrdessLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        buttonAdressSubmit = view.findViewById(R.id.buttonAdressSubmit);

    }
    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }


        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Important", Context.MODE_PRIVATE);

            try {
                switch (requestType) {
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams, sharedPreferences.getString("token", null));
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
            Gson gson = new Gson();
            if (response.getResponseCode() >= 400) {
                gson = new GsonBuilder().registerTypeAdapter(ErrorFromServer.class, new ErrorFromServerDeserializer()).create();

                ErrorFromServer error = gson.fromJson(response.getContent(), ErrorFromServer.class);

                DialogBuilderHelper dialog = new DialogBuilderHelper(error, getActivity());
                dialog.createDialog().show();
            }
            switch (requestType) {
                case "POST":
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragmentfade_in, R.anim.fragmentslide_out)
                            .replace(R.id.fragmentContainer, new ListAddress()).commit();
                    if(response.getResponseCode() == 201) {
                        DynamicToast.makeSuccess(getActivity(), "Cím felvétel sikeres").show();

                    }
                    break;

            }
        }
    }
}