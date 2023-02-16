package hu.petrik.vizsgaremek;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPw;
    private EditText editTextRePw;
    private Button buttonRegSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    public void init() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPw = findViewById(R.id.editTextPw);
        editTextRePw = findViewById(R.id.editTextRePw);
        buttonRegSubmit = findViewById(R.id.buttonRegSubmit);
    }
}