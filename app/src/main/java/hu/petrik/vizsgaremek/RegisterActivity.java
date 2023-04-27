package hu.petrik.vizsgaremek;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout fullNameLayout;
    private TextInputLayout pwLayout;
    private TextInputLayout rePwLayout;

    private EditText editTextEmail;
    private EditText editTextPw;
    private EditText editTextRePw;
    private EditText editTextFullName;
    private Button buttonRegSubmit;
    private String BASE_URL="http://10.0.2.2:3000/user/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //teljesképernyő beállítása
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //alapértelmezett actionbar elrejtése
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        buttonRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String pw = editTextPw.getText().toString().trim();
                String rePw = editTextRePw.getText().toString().trim();
                String fullName = editTextFullName.getText().toString().trim();
                if (email.isEmpty()){
                    emailLayout.setError("Nem lehet üres");
                    return;
                } else {
                    emailLayout.setError(null);
                }
                if (fullName.isEmpty()){
                    fullNameLayout.setError("Nem lehet üres");
                    return;
                } else {
                    fullNameLayout.setError(null);
                }
                if (pw.isEmpty()){
                    pwLayout.setError("Nem lehet üres");
                    return;
                } else {
                    emailLayout.setError(null);
                }
                if (rePw.isEmpty()){
                    rePwLayout.setError("Nem lehet üres");
                    return;
                } else {
                    rePwLayout.setError(null);
                }
                if (!rePw.equals(pw)) {
                    rePwLayout.setError("Jelszavaknak egyezniük kell");
                    return;
                } else {
                    rePwLayout.setError(null);
                }
                Gson json = new Gson();
                Users newUser = new Users(email,fullName, pw,rePw);
                RequestTask task = new RequestTask(BASE_URL,"POST",json.toJson(newUser));
                task.execute();
            }
        });
    }

    public void init() {
        emailLayout = findViewById(R.id.emailLayout);
        fullNameLayout = findViewById(R.id.fullNameLayout);
        pwLayout = findViewById(R.id.pwLayout);
        rePwLayout = findViewById(R.id.rePwLayout);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPw = findViewById(R.id.editTextPw);
        editTextRePw = findViewById(R.id.editTextRePw);
        editTextFullName = findViewById(R.id.editTextFullName);
        buttonRegSubmit = findViewById(R.id.buttonRegSubmit);
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
            try {
                switch (requestType) {
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams, null);
                        break;

                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this,
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
            if (response == null) {
                DialogBuilderHelper builderHelper = new DialogBuilderHelper(RegisterActivity.this);
                Dialog dialog = builderHelper.createServerErrorDialog();
                dialog.show();
                return;
            }
            if (response.getResponseCode() >= 400) {
                gson = new GsonBuilder().registerTypeAdapter(ErrorFromServer.class, new ErrorFromServerDeserializer()).create();
                ErrorFromServer error = gson.fromJson(response.getContent(), ErrorFromServer.class);
                DialogBuilderHelper dialog = new DialogBuilderHelper(error, RegisterActivity.this);
                dialog.createDialog().show();
                Toast.makeText(RegisterActivity.this,
                        response.getContent(), Toast.LENGTH_SHORT).show();
            } else {
                DynamicToast.makeSuccess(RegisterActivity.this,"Sikeres Regisztráció").show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
//            switch (requestType) {
//                case "GET":
//
//                    break;
//                case "POST":
//
//                    break;
//                case "PUT":
//
//                    break;
//                case "DELETE":
//
//                    break;
//            }
        }
    }
}