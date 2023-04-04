package hu.petrik.vizsgaremek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private TextView textViewToRegisterActivity;
    private EditText editTextLoginEmail;
    private EditText editTextLoginPw;
    private Button buttonLoginSubmit;
//    SharedPreferences pref = getApplicationContext().getSharedPreferences("TokenPref", 0); // 0 - for private mode
//    SharedPreferences.Editor editor = pref.edit();
    private String BASE_URL="http://10.0.2.2:3000/auth/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //teljesképernyő beállítása
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //alapértelmezett actionbar elrejtése
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        textViewToRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        buttonLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextLoginEmail.getText().toString().trim();
                String pw = editTextLoginPw.getText().toString().trim();
                Gson json = new Gson();
                LoginHelper loginData = new LoginHelper(email, pw);
                Toast.makeText(LoginActivity.this, json.toJson(loginData), Toast.LENGTH_SHORT).show();
                RequestTask task = new RequestTask(BASE_URL, "POST", json.toJson(loginData));
                task.execute();

            }
        });
    }

    public  void init() {
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPw = findViewById(R.id.editTextLoginPw);
        textViewToRegisterActivity = findViewById(R.id.textViewToRegisterActivity);
        buttonLoginSubmit = findViewById(R.id.buttonLoginSubmit);
    }

    @Override
    public void onBackPressed() {
        finish();
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
                Toast.makeText(LoginActivity.this,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
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

            if (response.getResponseCode() >= 400) {
                ErrorFromServer error = converter.fromJson(response.getContent(), ErrorFromServer.class);

                DialogBuilderHelper dialog = new DialogBuilderHelper(error, LoginActivity.this);
                dialog.createDialog().show();
                Toast.makeText(LoginActivity.this,
                        response.getContent(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Sikres Bejelentkezés", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            switch (requestType) {
                case "GET":

                    break;
                case "POST":
                    TokenHelper token =converter.fromJson(response.getContent(), TokenHelper.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token" , token.getToken());
                    editor.commit();

                    break;
                case "PUT":

                    break;
                case "DELETE":

                    break;
            }
        }
    }
}