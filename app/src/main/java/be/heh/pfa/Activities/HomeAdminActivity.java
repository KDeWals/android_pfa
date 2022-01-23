package be.heh.pfa.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import be.heh.pfa.DatabaseHelper;
import be.heh.pfa.R;

public class HomeAdminActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 500;
    private long mBackPressed;
    private String email;
    private String role;
    DatabaseHelper db;

    private Button btn_ha_disconnect;
    private androidx.cardview.widget.CardView cv_automate_admin;
    private androidx.cardview.widget.CardView cv_users_admin;
    private androidx.cardview.widget.CardView cv_profile_admin;
    private androidx.cardview.widget.CardView cv_manage_users;
    private androidx.cardview.widget.CardView cv_manage_plcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        email =  bundle.getString("email");
        role = bundle.getString("role");


        btn_ha_disconnect = (Button) findViewById(R.id.btn_ha_disconnect);
        cv_automate_admin = (androidx.cardview.widget.CardView) findViewById(R.id.cv_ha_automates);
        cv_users_admin = (androidx.cardview.widget.CardView) findViewById(R.id.cv_ha_users);
        cv_profile_admin = (androidx.cardview.widget.CardView) findViewById(R.id.cv_ha_profile);
        cv_manage_users = (androidx.cardview.widget.CardView) findViewById(R.id.cv_ha_users);
        cv_manage_plcs = (androidx.cardview.widget.CardView) findViewById(R.id.cv_ha_automates);




        btn_ha_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisconnectDialog();
            }

        });

        cv_profile_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent goToMyProfile = new Intent(HomeAdminActivity.this, ProfileActivity.class);
                goToMyProfile.putExtra("email", email);
                startActivity(goToMyProfile);
            }
        });


        cv_automate_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAutomateAdmin= new Intent(HomeAdminActivity.this, ManageAutomatesActivity.class);
                goToAutomateAdmin.putExtra("email", email);
                goToAutomateAdmin.putExtra("role", role);
                startActivity(goToAutomateAdmin);
            }
        });

        cv_users_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToUsers = new Intent(HomeAdminActivity.this, ManageUsersActivity.class);
                goToUsers.putExtra("email", email);
                goToUsers.putExtra("role", role);
                startActivity(goToUsers);
            }
        });

        cv_users_admin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteAllLambdaUsers();
                return true;
            }
        });

        cv_automate_admin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteAllPLCs();
                return true;
            }
        });



    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            showDisconnectDialog();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Cliquez deux fois pour vous déconnecter",    Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void showDisconnectDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Confirmer votre déconnexion")
                .setPositiveButton("Je me déconnecte", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("accountAdress", "non");
                        editor.putString("accountName", "none");
                        editor.putBoolean("isConnected", false);
                        editor.apply();

                        Intent goToLogin = new Intent(HomeAdminActivity.this, LoginActivity.class);
                        finish();
                        startActivity(goToLogin);

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

//        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("firstStart", false);
//        editor.apply();
    }

    public void showDeleteAllLambdaUsers(){
        new android.app.AlertDialog.Builder(this)
                .setTitle("Suppression de tous les utilisateurs 'Lambda'")
                .setMessage("Confirmer la suppression de TOUS les utilisateurs 'lamda'")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        db.deleteAllLambdaUsers();
                        Log.i("HomeAdminActivity", "Tous les utilisateurs 'lambda' ont été supprimés ! ");
                        Toast.makeText(HomeAdminActivity.this, "Tous les utilisateurs 'lambda' ont été supprimés !", Toast.LENGTH_SHORT).show();

                    }
                })

                .setCancelable(true)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void showDeleteAllPLCs() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Suppression de tous les automates")
                .setMessage("Confirmer la suppression de TOUS les automates ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        db.deleteAllPLCs();
                        Toast.makeText(HomeAdminActivity.this, "Tous les automates ont été supprimés !", Toast.LENGTH_SHORT).show();
                        Log.i("HomeAdminActivity", "Tous les automates ont été supprimés !");

                    }
                })

                .setCancelable(true)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
