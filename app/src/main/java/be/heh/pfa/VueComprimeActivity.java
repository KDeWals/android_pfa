package be.heh.pfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class VueComprimeActivity extends AppCompatActivity implements View.OnClickListener {

    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private NetworkInfo network;
    private ConnectivityManager netStatus;

    private RelativeLayout rl_ac_write;
    private TextView tv_ac_plc_infos;
    private TextView tv_ac_numcode;
    private Button btn_connect_plc;
    private EditText et_ac_dbb5;
    private Button btn_ac_dbb5;
    private EditText et_ac_dbb6;
    private Button btn_ac_dbb6;
    private EditText et_ac_dbb7;
    private Button btn_ac_dbb7;
    private EditText et_ac_dbb8;
    private Button btn_ac_dbb8;
    private EditText et_ac_dbw18;
    private Button btn_ac_dbw18;


    private ReadTaskS7 readS7;
    private PLCReadThread plcReadThread;
    private Thread th;
    private Automate plc = new Automate();
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue_comprime);
        db = new DatabaseHelper(this);

        // StrictMode ajouté car impossible de communiquer avec l'automate sans cela
        // Erreur : android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        netStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = netStatus.getActiveNetworkInfo();

        tv_ac_plc_infos = findViewById(R.id.tv_ac_plc_infos);
        tv_ac_numcode = findViewById(R.id.tv_ac_numcode);
        btn_connect_plc = findViewById(R.id.btn_ac_connect);
        btn_connect_plc.setOnClickListener(this);
        rl_ac_write = findViewById(R.id.rl_ac_write);
        et_ac_dbb5 = findViewById(R.id.et_ac_dbb5);
        btn_ac_dbb5 = findViewById(R.id.btn_ac_dbb5_write);
        btn_ac_dbb6 = findViewById(R.id.btn_ac_dbb6_write);
        btn_ac_dbb7 = findViewById(R.id.btn_ac_dbb7_write);
        btn_ac_dbb8 = findViewById(R.id.btn_ac_dbb8_write);
        btn_ac_dbw18 = findViewById(R.id.btn_ac_dbw18_write);


        Bundle plcinfo = getIntent().getExtras();
        plc.setName(plcinfo.getString("name"));
        plc.setIp(plcinfo.getString("ip"));
        plc.setRack(plcinfo.getInt("rack"));
        plc.setSlot(plcinfo.getInt("slot"));

        tv_ac_plc_infos.setText("IP : " + plc.getIp() + " | r: " + plc.getRack() + " | s: " + plc.getSlot());

        if(sharedPreferences.getBoolean("isAdmin", false)){
            rl_ac_write.setVisibility(View.VISIBLE);
        }
        else {
            rl_ac_write.setVisibility(View.GONE);
        }



    }

    @Override
    public void onClick(View view) {

            if (btn_connect_plc.getText().toString().compareTo(getResources().getString(R.string.connect_to_plc)) == 0) {
                btn_connect_plc.setText(R.string.disconnect_from_plc);
                changeStateOfEditFields(true);


                readS7 = new ReadTaskS7(view, btn_connect_plc, tv_ac_numcode, plc);
              //  th = new Thread(readS7);
              //  th.start();
              //  Log.i("VueComprimeActivity", "Thread ID : " + th.getId());
                readS7.run();
                isConnected.set(true);
            }
            else {
                if(readS7 != null) {
                //    th.interrupt();
                    readS7.Stop();
                    isConnected.set(false);
                    changeStateOfEditFields(true);
                    Toast.makeText(VueComprimeActivity.this, "Vous vous êtes déconnecté de l'automate", Toast.LENGTH_SHORT).show();
                    btn_connect_plc.setText(R.string.connect_to_plc);
                    tv_ac_numcode.setText("Numcode : ");
                }

            }
    }

    protected void onStop() {
        super.onStop();
        if(isConnected.get()) {
            readS7.Stop();
            Toast.makeText(VueComprimeActivity.this, "Vous avez été déconnecté de l'automate", Toast.LENGTH_SHORT).show();
            btn_connect_plc.setText(R.string.connect_to_plc);
            tv_ac_numcode.setText("Numcode : ");
            changeStateOfEditFields(true);
        }

    }

    private void changeStateOfEditFields(boolean b) {


    }


}