package be.heh.pfa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.heh.pfa.DatabaseHelper;
import be.heh.pfa.EditUserActivity;
import be.heh.pfa.ListAdapterUsers;
import be.heh.pfa.R;
import be.heh.pfa.User;

public class ManageUsersActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ArrayList<User> users_list;

    private String email;
    private String role;
    private User sess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        sess = db.getUserInfo(email);
        role = sess.getRole();

        users_list = db.getAllUsers();
      //  getUsers();
        getUsersVersion2();

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
            goToAddUser.putExtra("email", email);
            goToAddUser.putExtra("role", role);
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
                    if(role.compareTo(getResources().getString(R.string.admin)) == 0){
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
                    else {
                        Toast.makeText(getBaseContext(),
                                "Vous n'avez pas les droits suffisants pour cette action",
                                Toast.LENGTH_SHORT).show();
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

    private void getUsersVersion2() {
            ListView listView = (ListView) findViewById(R.id.lv_users);
            ListAdapterUsers adapter = new ListAdapterUsers(ManageUsersActivity.this, users_list);

            if(db.countUsers() > 0){
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        User user;
                        user = (User) adapter.getItem(position);

                        if(user.getRole().equals(getResources().getString(R.string.admin))){
                            Toast.makeText(ManageUsersActivity.this, "Impossible de changer la permission d'un administrateur", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showChangePermDialog(user.getEmail());
                        }

                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        User user;
                        user = (User) adapter.getItem(position);

//                        if (email.equals(user.getEmail())) {
//                            Toast.makeText(ManageUsersActivity.this, "Impossible d'afficher cette fenêtre pour un super-utilisateur", Toast.LENGTH_SHORT).show();
//                            Log.i("ManageUsersActivity", "current email : " + email + " selected email : " + user.getEmail());
//                        } else {



                            AlertDialog.Builder dialog = new AlertDialog.Builder(ManageUsersActivity.this);
                            LayoutInflater layoutInflater = ManageUsersActivity.this.getLayoutInflater();
                            View dialogView = layoutInflater.inflate(R.layout.layout_user_quick_look, null);
                            dialog.setView(dialogView);
                            String tmp = getResources().getString(R.string.delete_lambda);
                            if(user.getRole().compareTo(getResources().getString(R.string.admin)) == 0){
                                tmp = getResources().getString(R.string.delete_admin);
                            }
                            dialog.setTitle("Informations d'un utilisateur");
                            dialog.setMessage("Voici les informations de l'utilisateur " + user.getFistname() + tmp);

//                            dialog.setNegativeButton("Modifier", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int i) {
//
//                                    Toast.makeText(ManageUsersActivity.this,"WIP", Toast.LENGTH_SHORT).show();
////                                    Intent intent = new Intent(ManageUsersActivity.this, EditUserActivity.class);
////                                    intent.putExtra("editUserMail", user.getEmail());
////                                    startActivity(intent);
//
//                                }
//                            })

                                    dialog.setCancelable(true)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                            showDeleteDialog(user.getEmail());
                                        }
                                    });
                            AlertDialog alert = dialog.create();
                            alert.show();
                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                            nbutton.setTextColor(Color.rgb(255, 0, 0));
                            if (user.getRole().compareTo(getResources().getString(R.string.admin)) == 0) {
                                nbutton.setEnabled(false);
                                nbutton.setTextColor(Color.rgb(0x9b, 0x9b, 0x9b));
                            }
                            TextView name = (TextView) alert.findViewById(R.id.et_luql_name);
                            name.setText(user.getName());
                            TextView firstname = (TextView) alert.findViewById(R.id.et_luql_firstname);
                            firstname.setText(user.getFistname());
                            TextView email = (TextView) alert.findViewById(R.id.et_luql_email_address);
                            TextView role = (TextView) alert.findViewById(R.id.et_luql_role);
                            role.setText(user.getRole());
                            email.setText(user.getEmail());
                            TextView rights = (TextView) alert.findViewById(R.id.et_luql_rights);
                            if(user.getPerm().equals("RW")) { rights.setText("Lecture et écriture"); }
                            else { rights.setText("Lecture uniquement"); }



//                        }
                        return true;
                    }
                });

            }

    }

    public void showChangePermDialog(String userMail) {
        final String[] tmp = new String[1];
        final String[] newPerm = new String[1];
        ArrayList<User> tmpUsers = db.getAllUsers();
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
        new AlertDialog.Builder(ManageUsersActivity.this)
                .setTitle("Changement de permission d'un utilisateur")
                .setMessage("Confirmer le changement de permission (" + tmp[0] + " -> " + newPerm[0] + ") de l'utilisateur" +
                        " ayant l'adresse mail : " + userMail)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        db.changeUserPermission(userMail, newPerm[0]);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Suppression d'un utilisateur");
                dialog.setMessage("Confirmer la suppression de l'utilisateur" +
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
                });

                dialog.setCancelable(true);
                dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
            AlertDialog alert = dialog.create();
            alert.show();
            Button dbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            dbutton.setTextColor(Color.rgb(255,0,0));

    }
}
