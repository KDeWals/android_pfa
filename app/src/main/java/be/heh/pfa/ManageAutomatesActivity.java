package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ManageAutomatesActivity extends AppCompatActivity {

    DatabaseHelper db;
    ArrayList<Automate> automateList;

    private String email;
    private String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_automates);

        Bundle bundle = getIntent().getExtras();
        email =  bundle.getString("email");
        role = bundle.getString("role");

        db = new DatabaseHelper(this);
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

        Toast.makeText(ManageAutomatesActivity.this, "Nombre de API : " + db.countAutomates(), Toast.LENGTH_SHORT).show();


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
        new AlertDialog.Builder(this)
                .setTitle("Suppression d'un automate")
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
                })
                .create().show();

    }


}