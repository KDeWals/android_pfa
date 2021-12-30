package be.heh.pfa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class HomeLambdaActivity extends AppCompatActivity {
    private static final int TIME_INTERVAL = 1000;
    private long mBackPressed;
    private String email;
    private String role;
    DatabaseHelper db;

    private Button btn_hl_disconnect;
    private androidx.cardview.widget.CardView cv_profile_lambda;
    private androidx.cardview.widget.CardView cv_automates_lambda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lambda);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        role = bundle.getString("role");

        db = new DatabaseHelper(this);

        btn_hl_disconnect = (Button) findViewById(R.id.btn_hl_disconnect);
        cv_profile_lambda = (androidx.cardview.widget.CardView) findViewById(R.id.cv_hl_profile);
        cv_automates_lambda = (androidx.cardview.widget.CardView) findViewById(R.id.cv_hl_automates);

        btn_hl_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showDisconnectDialog(); }
        });

        cv_profile_lambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMyProfile = new Intent(HomeLambdaActivity.this, ProfileActivity.class);
                goToMyProfile.putExtra("email", email);
                goToMyProfile.putExtra("role", role);
                startActivity(goToMyProfile);
            }
        });

        cv_automates_lambda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMyAutomates = new Intent(HomeLambdaActivity.this, ManageAutomatesActivity.class);
                goToMyAutomates.putExtra("email", email);
                goToMyAutomates.putExtra("role", role);
                startActivity(goToMyAutomates);
            }
        });


    }




    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            showDisconnectDialog();
            //super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Cliquez deux fois pour vous déconnecter",    Toast.LENGTH_SHORT).show();


        }
        mBackPressed = System.currentTimeMillis();
    }

    private void showDisconnectDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Confirmer votre déconnexion")
                .setPositiveButton("Je me déconnecte", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("accountName", "none");
                        editor.putBoolean("isConnected", false);
                        editor.apply();

                        Intent goToLogin = new Intent(HomeLambdaActivity.this, LoginActivity.class);
                        startActivity(goToLogin);
                        finish();

                        Intent superAdminIntent = new Intent(HomeLambdaActivity.this, LoginActivity.class);
                        startActivity(superAdminIntent);
                    }
                })
                .setCancelable(true)
                .setNegativeButton("Je reste connecté", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create().show();

    }


}