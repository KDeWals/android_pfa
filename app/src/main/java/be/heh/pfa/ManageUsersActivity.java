package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ArrayList<User> users_list;
    final String TAG = "ManageUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        db = new DatabaseHelper(this);
        users_list = db.getAllUsers();
        getUsers();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_add_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.i_mmu_add_user) {
            Intent goToAddUser = new Intent(getApplicationContext(), AddUserActivity.class);
            startActivity(goToAddUser);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void getUsers() {

        GridLayout data = findViewById(R.id.gl_users);


        for (int i = 0; i < users_list.size(); i++) {
            //
            TextView Name = new TextView(this);
            Name.setText(users_list.get(i).getName());
            GridLayout.LayoutParams paramsN = new GridLayout.LayoutParams();
            paramsN.setGravity(1);
            data.addView(Name, paramsN);
            //
            TextView FirstName = new TextView(this);
            FirstName.setText(users_list.get(i).getFistname());
            GridLayout.LayoutParams paramsF = new GridLayout.LayoutParams();
            paramsF.setGravity(1);
            data.addView(FirstName, paramsF);
            //
            TextView Email = new TextView(this);
            Email.setText(users_list.get(i).getEmail());
            GridLayout.LayoutParams paramsE = new GridLayout.LayoutParams();
            paramsE.setGravity(1);
            data.addView(Email, paramsE);
            //
            TextView Permission = new TextView(this);
            if (i == 6) i -= 1;
            Permission.setText(users_list.get(i).getPerm());
            Permission.setWidth(75);
            GridLayout.LayoutParams paramsP = new GridLayout.LayoutParams();
            paramsP.setGravity(1);
            data.addView(Permission, paramsP);

            final String selectedUserMail = users_list.get(i).getEmail();
            Email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(User u : db.checkAdmin()){
                        if(u.getEmail() != null && u.getEmail().contains(selectedUserMail) &&
                        db.checkAdmin().size() == 1)
                        {
                            // il doit rester au moins un admin valide
                            Toast.makeText(getBaseContext(),
                                    "Il doit rester au moins un administrateur valide",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // suppression de l'utilisateur
                                showDeleteDialog(selectedUserMail);


                        }
                    }

                }


            });


            Permission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                    for(User u : db.checkAdmin()){
                        if(u.getEmail() != null && u.getEmail().contains(selectedUserMail) &&
                                db.checkAdmin().size() == 1)
                        {
                            // impossible de changer la permission du dernier admin
                            Toast.makeText(getBaseContext(),
                                    "Impossible de changer la permission d'un administrateur",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else { showChangePermDialog(selectedUserMail); }
                    }
                }
            });
        }


    }

    public void showChangePermDialog(String userMail) {
        final String[] tmp = new String[1];
        final String[] newPerm = new String[1];
        ArrayList<User> tmpUsers = db.getAllUsers();
        new AlertDialog.Builder(this)
                .setTitle("Changement de permission d'un utilisateur")
                .setMessage("Confirmer le changement de permission de l'utilisateur" +
                        " ayant l'adresse mail : " + userMail)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        for (User u : tmpUsers) {
                            if (u.getEmail().equals(userMail)) {
                                tmp[0] = u.getPerm();
                            }
                        }


                        if (tmp[0].equals("R")) {
                            newPerm[0] = "RW";
                        } else {
                            newPerm[0] = "R";
                        }
                        db.changeUserPermission(newPerm[0], userMail);
                        Log.i("ManageUsersActivity", "Permission de l'utilisateur " + userMail + " modifié !");

                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)
                .create().show();
    }


    public void showDeleteDialog(String userMail){
        new AlertDialog.Builder(this)
                .setTitle("Suppression d'un utilisateur")
                .setMessage("Confirmer la suppression de l'utilisateur" +
                        " ayant l'adresse mail : " + userMail)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                            db.removeUser(userMail);
                            Log.i("ManageUsersActivity", "Utilisateur " + userMail + " supprimé !");
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);

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
