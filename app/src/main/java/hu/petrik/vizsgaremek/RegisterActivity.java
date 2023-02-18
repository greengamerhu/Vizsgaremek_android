package hu.petrik.vizsgaremek;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private EditText editTextEmail;
    private EditText editTextPw;
    private EditText editTextRePw;
    private EditText editTextFullName;
    private Button buttonRegSubmit;
    private String BASE_URL="http://10.0.2.2:3000/restaurant/register";

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
                // TODO: megirni a validációt
                if (email.isEmpty()){
                    emailLayout.setError("Nem lehet üres");
                    return;
                } else {
                    emailLayout.setError(null);
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
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl + "/" + requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(RegisterActivity.this,
                        e.toString(), Toast.LENGTH_SHORT).show();
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
            if (response.getResponseCode() >= 400) {
                Toast.makeText(RegisterActivity.this,
                        response.getContent(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Sikeres regisztráció", Toast.LENGTH_SHORT).show();
                // TODO: indítani kell egy login actvity-t
            }
            switch (requestType) {
                case "GET":

                    break;
                case "POST":

                    break;
                case "PUT":

                    break;
                case "DELETE":

                    break;
            }
        }
    }
}