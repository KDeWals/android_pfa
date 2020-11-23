package be.heh.pfa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class HomeLambdaActivity extends AppCompatActivity {
    private static final int TIME_INTERVAL = 1000;
    private long mBackPressed;

    private TextView lambdahome_tv_welcome;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lambda);

        lambdahome_tv_welcome = findViewById(R.id.homelambda_tv_welcome);
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String name = prefs.getString("name", null);



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

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }


}