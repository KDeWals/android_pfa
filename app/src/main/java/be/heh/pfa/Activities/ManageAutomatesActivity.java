package be.heh.pfa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

import be.heh.pfa.Automate;
import be.heh.pfa.DatabaseHelper;
import be.heh.pfa.ListAdapter;
import be.heh.pfa.R;
import be.heh.pfa.User;


public class ManageAutomatesActivity extends AppCompatActivity {

    DatabaseHelper db;
    ArrayList<Automate> automateList;

    private String email;
    private String role;
    private User sess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_automates);

        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        email =  bundle.getString("email");

        sess = db.getUserInfo(email);
        role = sess.getRole();

        automateList = db.getAllAutomates();
        getAutomates();

        try {

            ArrayList<Automate> automateArrayList = db.getAllAutomates();
            for (Automate au : automateArrayList) {
                Log.i("TAG", au.getName() + " " + au.getIp()+ " " + au.getRack() + " " + au.getSlot() + " " + au.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(db.countAutomates() == 0){
            Toast.makeText(ManageAutomatesActivity.this, "Il n'y aucun automate. Veuillez en ajouter un avec le bouton + en haut à droite.", Toast.LENGTH_LONG).show();

        }
//        else {
//            Toast.makeText(ManageAutomatesActivity.this, "Nombre de API : " + db.countAutomates(), Toast.LENGTH_SHORT).show();
//        }


    }

    protected void onRestart() {
        super.onRestart();
        automateList = db.getAllAutomates();
        getAutomates();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(role.compareTo(getResources().getString(R.string.admin)) == 0) {
            getMenuInflater().inflate(R.menu.activity_add_automate, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.i_mma_add_plc) {
            Intent intent = new Intent(ManageAutomatesActivity.this, AddAutomateActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(menuItem);
    }


    public void getAutomates(){
        ListView listView = (ListView) findViewById(R.id.lv_plcs);
        ListAdapter adapter = new ListAdapter(ManageAutomatesActivity.this, automateList);

        if(db.countAutomates() > 0){
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Automate plc;
                    plc = (Automate) adapter.getItem(position);
                    Intent goToPLCView = new Intent(ManageAutomatesActivity.this, PLCViewActivity.class);
                    goToPLCView.putExtra("name", plc.getName());
                    goToPLCView.putExtra("ip", plc.getIp());
                    goToPLCView.putExtra("rack", plc.getRack());
                    goToPLCView.putExtra("slot", plc.getSlot());
                    goToPLCView.putExtra("type", plc.getType());
                    goToPLCView.putExtra("role", role);
                    goToPLCView.putExtra("email", email);
                    startActivity(goToPLCView);

                }
            });
            if(role.compareTo(getResources().getString(R.string.admin)) == 0) {
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   int position, long id) {
                        Automate plc;
                        plc = (Automate) adapter.getItem(position);
                        showDeleteDialog(plc.getIp());
                        return true;
                    }
                });
            }

        }
    }

    public void showDeleteDialog(String plcIp){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Suppression d'un automate")
                .setMessage("Confirmer la suppression de l'automate" +
                        " ayant l'adresse IP : " + plcIp)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        db.deletePLC(plcIp);
                        Log.i("ManageAutomatesActivity", "Automate ayant l'adresse IP " + plcIp + " supprimé !");
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
                });
                AlertDialog alert = dialog.create();
                alert.show();
                Button dbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                dbutton.setTextColor(Color.rgb(255,0,0));

    }


}