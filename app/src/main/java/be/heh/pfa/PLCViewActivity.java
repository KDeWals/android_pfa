package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

public class PLCViewActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private AtomicBoolean isConnected = new AtomicBoolean(false);

    private EditText apv_et_name;
    private EditText apv_et_ip;
    private EditText apv_et_rack;
    private EditText apv_et_slot;
    private CheckBox apv_cb_type_comprimes;
    private CheckBox apv_cb_type_liquide;
    private Button apv_btn_update_name;
    private Button apv_btn_update_network_info;
    private Button apv_btn_connect_plc;
    private NetworkInfo network;
    private ConnectivityManager netStatus;
    private TextView apv_tv_info;
    private ReadTaskS7 readS7;
    private Automate plc = new Automate();
    DatabaseHelper db;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plc_view);
        // StrictMode ajouté car impossible de communiquer avec l'automate sans cela
        // Erreur : android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle sessionInfo = getIntent().getExtras();
        db = new DatabaseHelper(this);

        netStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = netStatus.getActiveNetworkInfo();

        currentUser = db.getUserInfo(sessionInfo.getString("email"));

        apv_tv_info = findViewById(R.id.tv_apv_info_lambda);
        apv_et_name = findViewById(R.id.et_apv_btn_name);
        apv_et_ip = findViewById(R.id.et_apv_ip);
        apv_et_rack = findViewById(R.id.et_apv_rack);
        apv_et_slot = findViewById(R.id.et_apv_slot);
        apv_cb_type_comprimes = findViewById(R.id.apv_cb_type_comprimes);
        apv_cb_type_liquide = findViewById(R.id.apv_cb_type_liquide);
        apv_btn_update_name = findViewById(R.id.apv_btn_name);
        apv_btn_update_network_info = findViewById(R.id.apv_btn_network_info);
        apv_btn_connect_plc = findViewById(R.id.apv_btn_go_to_var);
        apv_btn_update_name.setOnClickListener(this);
        apv_btn_update_network_info.setOnClickListener(this);
        apv_btn_connect_plc.setOnClickListener(this);
        apv_cb_type_comprimes.setOnCheckedChangeListener(this);
        apv_cb_type_liquide.setOnCheckedChangeListener(this);
        plc.setName(sessionInfo.getString("name"));
        plc.setIp(sessionInfo.getString("ip"));
        plc.setRack(sessionInfo.getInt("rack"));
        plc.setSlot(sessionInfo.getInt("slot"));
        plc.setType(sessionInfo.getString("type"));
        apv_et_name.addTextChangedListener(textWatcherName);
        apv_et_ip.addTextChangedListener(textWatcherIp);

        apv_et_name.setText(plc.getName());
        apv_et_name.setTag(plc.getName());
        apv_et_ip.setText(plc.getIp());
        apv_et_ip.setTag(plc.getIp());
        apv_et_rack.setText(String.valueOf(plc.getRack()));
        apv_et_rack.setTag(plc.getRack());
        apv_et_slot.setText(String.valueOf(plc.getSlot()));
        apv_et_slot.setTag(plc.getSlot());
        if(plc.getType().equals("comprimés")) apv_cb_type_comprimes.setChecked(true);
        else apv_cb_type_liquide.setChecked(true);

        if(currentUser.getPerm().equals("RW") && !currentUser.getRole().equals(getResources().getString(R.string.admin))){
            greyOutForLambda();
            apv_tv_info.setText(R.string.rw_lambda);
        }
        else if(currentUser.getPerm().equals("R")){
            greyOutForLambda();
            apv_tv_info.setText(R.string.r_lambda);
        }
        else if(currentUser.getRole().equals(getResources().getString(R.string.admin))){
            apv_tv_info.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.apv_btn_name :
                db.changeAutomateName(plc.getIp(), apv_et_name.getText().toString());
                Toast.makeText(PLCViewActivity.this, "Nom de l'automate modifié", Toast.LENGTH_SHORT).show();
                plc.setName(apv_et_name.getText().toString());
                apv_btn_update_name.setVisibility(View.INVISIBLE);
                break;
            case R.id.apv_btn_network_info :
                db.changeAutomateNetworkInfo(
                        plc.getIp(),
                        String.valueOf(apv_et_ip.getText()),
                        Integer.valueOf(String.valueOf(apv_et_rack.getText())),
                        Integer.valueOf(String.valueOf(apv_et_slot.getText())));
                Toast.makeText(PLCViewActivity.this, "Informations réseau de l'automate modifiés", Toast.LENGTH_SHORT).show();
                plc.setIp(apv_et_ip.getText().toString());
                plc.setRack(Integer.parseInt(apv_et_rack.getText().toString()));
                plc.setSlot(Integer.parseInt(apv_et_slot.getText().toString()));
                apv_btn_update_network_info.setVisibility(View.INVISIBLE);
                break;

            case R.id.apv_btn_go_to_var :
                if(network != null && network.isConnectedOrConnecting()){
                    if(plc.getType().equals("comprimés")){
                        Intent goToPLCVar = new Intent(PLCViewActivity.this, VueComprimeActivity.class);
                        goToPLCVar.putExtra("name", plc.getName());
                        goToPLCVar.putExtra("ip", plc.getIp());
                        goToPLCVar.putExtra("rack", plc.getRack());
                        goToPLCVar.putExtra("slot", plc.getSlot());
                        goToPLCVar.putExtra("type", plc.getType());
                        goToPLCVar.putExtra("email", currentUser.getEmail());
                        startActivity(goToPLCVar);
                    }
                }
                break;
        }


    }



    public void greyOutForLambda(){
            apv_et_name.setEnabled(false);
            apv_et_ip.setEnabled(false);
            apv_et_rack.setEnabled(false);
            apv_et_slot.setEnabled(false);
            apv_cb_type_comprimes.setEnabled(false);
            apv_cb_type_liquide.setEnabled(false);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){

            case R.id.apv_cb_type_comprimes :
                if(b) {
                    apv_cb_type_liquide.setChecked(false);
                    db.changeAutomateType(plc.getIp(), "comprimés");
                    plc.setType("comprimés");
                }
                break;
            case R.id.apv_cb_type_liquide :
                if(b){
                    apv_cb_type_comprimes.setChecked(false);
                    db.changeAutomateType(plc.getIp(), "liquide");
                    plc.setType("liquide");
                }
        }
    }

    private TextWatcher textWatcherName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(apv_et_name.getTag() != null) apv_btn_update_name.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher textWatcherIp= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(apv_et_ip.getTag() != null) apv_btn_update_network_info.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}