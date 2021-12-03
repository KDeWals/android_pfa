package be.heh.pfa;


import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminProfileActivity extends AppCompatActivity {

    private TextView tv_welcome_admin;
    private EditText et_ap_username;
    private Button btn_edit;

    private User user;

    private int cpt = 0;
    SharedPreferences prefs;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        prefs =  getSharedPreferences("prefs", MODE_PRIVATE);
        tv_welcome_admin = (TextView) findViewById(R.id.tv_welcome_admin);
        et_ap_username = (EditText) findViewById(R.id.et_ap_username);
        btn_edit = (Button) findViewById(R.id.btn_ap_edit_username);


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.getString("name", "NA");




        String str = tv_welcome_admin.getText().toString();
        tv_welcome_admin.setText(str);

/*
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                    Toast.makeText(AdminProfileActivity.this, "Changement confirmé", Toast.LENGTH_SHORT).show();

                }


        });

*/
    }
}
