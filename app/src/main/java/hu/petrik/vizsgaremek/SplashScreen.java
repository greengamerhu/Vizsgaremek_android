package hu.petrik.vizsgaremek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    int responsecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //teljesképernyő beállítása
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //alapértelmezett actionbar elrejtése
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
        RequestTask task = new RequestTask("http://10.0.2.2:3000/user/profile", "GET");
        task.execute();

        Log.d("response", "onCreate: " + responsecode);
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
            SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);

            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl, sharedPreferences.getString("token", ""));
                        responsecode = response.getResponseCode();
                        break;

                }
            } catch (IOException e) {
                Log.d("connectionError", "doInBackground:" + e.getMessage());

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
            Log.d("response", "onPostExecute: "+ responsecode);
            if (response == null) {
                DialogBuilderHelper dialog = new DialogBuilderHelper(new ErrorFromServer(), SplashScreen.this);
                Dialog dialog1 = dialog.createServerErrorDialog();
                dialog1.show();
                return;
            }
            if(response.getResponseCode() == 401) {
                SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                editor.clear().apply();
                finish();
            } else {
                if (response.getResponseCode() >= 400) {
                    Toast.makeText(SplashScreen.this,
                            response.getContent(), Toast.LENGTH_SHORT).show();
                }
                SplashScreenActivation();
            }


        }
    }

    public void SplashScreenActivation() {
        SharedPreferences sharedPreferences = getSharedPreferences("Important", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("token", "").isEmpty()) {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, SPLASH_TIME_OUT);
        } else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, SPLASH_TIME_OUT);
        }
    }


}

