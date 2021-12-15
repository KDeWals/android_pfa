package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddAutomateActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText add_plc_et_name;
    private EditText add_plc_et_ip;
    private EditText add_plc_et_rack;
    private EditText add_plc_et_slot;
    private CheckBox add_plc_cb_type_comprimes;
    private CheckBox add_plc_cb_type_liquide;
    private Button plc_btn_add;
    private String type = "";
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_automate);

        db = new DatabaseHelper(this);

        add_plc_et_name = findViewById(R.id.add_plc_et_name);
        add_plc_et_ip = findViewById(R.id.add_plc_et_ip);
        add_plc_et_rack = findViewById(R.id.add_plc_et_rack);
        add_plc_et_slot = findViewById(R.id.add_plc_et_slot);
        add_plc_cb_type_comprimes = findViewById(R.id.add_plc_cb_type_comprimes);
        add_plc_cb_type_liquide = findViewById(R.id.add_plc_cb_type_liquide);
        plc_btn_add = findViewById(R.id.plc_btn_add);
        plc_btn_add.setOnClickListener(this);
        add_plc_cb_type_comprimes.setOnCheckedChangeListener(this);
        add_plc_cb_type_liquide.setOnCheckedChangeListener(this);


    }


    @Override
    public void onClick(View view) {
        String name = add_plc_et_name.getText().toString().trim();
        String ip = add_plc_et_ip.getText().toString();
        String rack = add_plc_et_rack.getText().toString();
        String slot = add_plc_et_slot.getText().toString();
        boolean isValid = false;

        if(!name.isEmpty()){
            if(!ip.isEmpty()){
                if(isValidIP(ip)){
                    if(!rack.isEmpty()) {
                        if (!slot.isEmpty()) {
                            if (add_plc_cb_type_comprimes.isChecked() || add_plc_cb_type_liquide.isChecked()) {
                                isValid = true;
                            } else Toast.makeText(this, "Veuillez sélectionner un type d'automate", Toast.LENGTH_SHORT).show();
                        } else add_plc_et_slot.setError("Entrez un numéro de slot");
                    } else add_plc_et_rack.setError("Entrez un numéro de rack");
                } else add_plc_et_ip.setError("Entrez une adresse IP valide");
            } else add_plc_et_ip.setError("Entrez une adresse IP");
        } else add_plc_et_name.setError("Entrez un nom");

            if(isValid) {
                long res = db.addAutomate(add_plc_et_name.getText().toString(),
                        add_plc_et_ip.getText().toString(),
                        Integer.valueOf(add_plc_et_rack.getText().toString()),
                        Integer.valueOf(add_plc_et_slot.getText().toString()),
                        type);
                if(res > 0) {
                    Toast.makeText(this, "Automate ajouté !", Toast.LENGTH_SHORT).show();
                    Intent goToManageAutomates = new Intent(AddAutomateActivity.this, ManageAutomatesActivity.class);
                    finish();
                    goToManageAutomates.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToManageAutomates);
                }
                else {
                    Toast.makeText(AddAutomateActivity.this, "Erreur d'ajout :  une adresse IP identique existe déjà !", Toast.LENGTH_LONG).show();
                }

            }
        }




    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()){

            case R.id.add_plc_cb_type_comprimes :
                if(b) {
                    add_plc_cb_type_liquide.setChecked(false);
                    type = "comprimés";
                }
                break;
            case R.id.add_plc_cb_type_liquide :
                if(b){
                    add_plc_cb_type_comprimes.setChecked(false);
                    type = "liquide";
                }
        }

    }

    public boolean isValidIP(final String ip){
        String[] ipPart = ip.split("\\.");
        Pattern pattern;
        Matcher matcher;

        final String IP_PATTERN = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";

        pattern = Pattern.compile(IP_PATTERN);
        matcher = pattern.matcher(ip);


        return matcher.matches() && Integer.parseInt(ipPart[0]) <= 255 &&
                Integer.parseInt(ipPart[1]) <= 255 &&
                Integer.parseInt(ipPart[2]) <= 255 &&
                Integer.parseInt(ipPart[3]) <= 255;
    }




}