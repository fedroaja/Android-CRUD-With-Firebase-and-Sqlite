package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.SqLite.DbHelper;
import umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.SqLite.Session;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText etUsername,etPassword;
    private Button btnLogin;
    private DbHelper db;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbHelper(this);
        session = new Session(this);

        textView = findViewById(R.id.judul);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        Typeface customFont = Typeface.createFromAsset(getAssets(),"fonts/Dalgona.otf");
        textView.setTypeface(customFont);

        if (session.loggin()){
            startActivity(new Intent(MainActivity.this, HalamanPertama.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (db.getUser(username,password)){
            session.setLoggedin(true);
            startActivity(new Intent(MainActivity.this,HalamanPertama.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Incorrect Usernam/Password",Toast.LENGTH_SHORT).show();
        }
    }
}
