package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ManageAutomatesActivity extends AppCompatActivity {


    EditText et_name;
    EditText et_ip;
    EditText et_rack;
    EditText et_slot;
    CheckBox cb_type_compr;
    CheckBox cb_type_liqu;
    DatabaseHelper db;
    ArrayList<Automate> automateList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_automates);
        et_name = (EditText) findViewById(R.id.et_aa_name);
        et_ip = (EditText) findViewById(R.id.et_aa_ip);
        et_rack = (EditText) findViewById(R.id.et_aa_rack);
        et_slot = (EditText) findViewById(R.id.et_aa_slot);
        cb_type_compr = (CheckBox) findViewById(R.id.cb_aa_compr);
        cb_type_liqu = (CheckBox) findViewById(R.id.cb_aa_liqu);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_add_automate, menu);
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


                }
            });
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