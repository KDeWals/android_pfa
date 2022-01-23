package be.heh.pfa.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.pfa.Automate;
import be.heh.pfa.DatabaseHelper;
import be.heh.pfa.EnumComprimes;
import be.heh.pfa.PLCHandler;
import be.heh.pfa.R;
import be.heh.pfa.ReadTaskS7Thread;
import be.heh.pfa.Simatic_S7.S7Client;
import be.heh.pfa.User;
import be.heh.pfa.WriteTaskS7Thread;

public class VueComprimeActivity extends AppCompatActivity implements View.OnClickListener {

    public final AtomicBoolean isConnected = new AtomicBoolean(false);
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

    private EditText input;

    private ReadTaskS7Thread readTaskS7Thread;
    private WriteTaskS7Thread writeTaskS7Thread;
    private Thread readingThread;
    private Thread writingThread;
    private byte[] dataToPLC = new byte[512];

    private Automate plc = new Automate();
    private String email;
    private User currentUser;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vue_comprime);
        db = new DatabaseHelper(this);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // StrictMode ajouté car impossible de communiquer avec l'automate sans cela
        // Erreur : android.os.NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
        currentUser = db.getUserInfo(email);

        netStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = netStatus.getActiveNetworkInfo();

        tv_ac_plc_infos = findViewById(R.id.tv_ac_plc_infos);
        tv_ac_numcode = findViewById(R.id.tv_ac_numcode_placeholder);
        btn_connect_plc = findViewById(R.id.btn_ac_connect);
        btn_connect_plc.setOnClickListener(this);
        rl_ac_write = findViewById(R.id.rl_ac_plc_write);
        et_ac_dbb5 = findViewById(R.id.et_ac_dbb5);
        btn_ac_dbb5 = findViewById(R.id.btn_ac_dbb5_write);
        et_ac_dbb6 = findViewById(R.id.et_ac_dbb6);
        btn_ac_dbb6 = findViewById(R.id.btn_ac_dbb6_write);
        et_ac_dbb7 = findViewById(R.id.et_ac_dbb7);
        btn_ac_dbb7 = findViewById(R.id.btn_ac_dbb7_write);
        et_ac_dbb8 = findViewById(R.id.et_ac_dbb8);
        btn_ac_dbb8 = findViewById(R.id.btn_ac_dbb8_write);
        et_ac_dbw18 = findViewById(R.id.et_ac_dbw18);
        btn_ac_dbw18 = findViewById(R.id.btn_ac_dbw18_write);


        Bundle plcinfo = getIntent().getExtras();
        plc.setName(plcinfo.getString("name"));
        plc.setIp(plcinfo.getString("ip"));
        plc.setRack(plcinfo.getInt("rack"));
        plc.setSlot(plcinfo.getInt("slot"));

        tv_ac_plc_infos.setText("IP : " + plc.getIp() + " | r: " + plc.getRack() + " | s: " + plc.getSlot());

        changeStateOfEditFields(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.activity_browser, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        String url = "http://" + plc.getIp();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onClick(View view) {

        if (currentUser.getPerm().equals("RW")) {
            changeStateOfEditFields(true);
        } else {
            changeStateOfEditFields(false);
        }

        if (btn_connect_plc.getText().toString().compareTo(getResources().getString(R.string.connect_to_plc)) == 0) {
            btn_connect_plc.setText(R.string.disconnect_from_plc);


            readTaskS7Thread = new ReadTaskS7Thread(VueComprimeActivity.this, plc, findViewById(R.id.tv_ac_numcode_placeholder), findViewById(R.id.rl_ac_plc_read), findViewById(R.id.rl_ac_plc_write), EnumComprimes.class, new PLCHandler());
            //    readS7 = new ReadTaskS7(view, btn_connect_plc, tv_ac_numcode, plc);
            //    readS7.Start();
            readingThread = new Thread(readTaskS7Thread);
            readingThread.start();

            if(currentUser.getPerm().equals("RW")){
                writeTaskS7Thread = new WriteTaskS7Thread(dataToPLC, plc);
                writingThread = new Thread(writeTaskS7Thread);
                writingThread.start();
            }


            //  Log.i("VueComprimeActivity", "Thread ID : " + th.getId() + " | Nom : " + th.getName());
            //  readS7.Start();
            isConnected.set(true);
        } else if (btn_connect_plc.getText().toString().compareTo(getResources().getString(R.string.disconnect_from_plc)) == 0 && view.getId() == R.id.btn_ac_connect) {
            //  if(readS7 != null) {
            if (readTaskS7Thread != null) {
                readingThread.interrupt();
                readTaskS7Thread.Stop();
                if(currentUser.getPerm().equals("RW")){
                    writeTaskS7Thread.Stop();
                }
                isConnected.set(false);
                changeStateOfEditFields(false);
                Toast.makeText(VueComprimeActivity.this, "Vous vous êtes déconnecté de l'automate", Toast.LENGTH_SHORT).show();
                btn_connect_plc.setText(R.string.connect_to_plc);
                tv_ac_numcode.setText("");
            }

        }

    }

    protected void onStop() {
        super.onStop();
        if (isConnected.get()) {
            readTaskS7Thread.Stop();
            if(currentUser.getPerm().equals("RW")) {
                writeTaskS7Thread.Stop();
            }
            Toast.makeText(VueComprimeActivity.this, "Vous avez été déconnecté de l'automate", Toast.LENGTH_SHORT).show();
            btn_connect_plc.setText(R.string.connect_to_plc);
            tv_ac_numcode.setText("");
            changeStateOfEditFields(false);
        }

    }

    public void OnClickWritingAction(View view) {

        S7Client s7Client = new S7Client();
        int result = s7Client.ConnectTo(plc.getIp(), plc.getRack(), plc.getSlot());
        if(result == 0){

            switch (view.getId()) {
                case R.id.btn_ac_dbb5_write:
                    input = (EditText) findViewById(R.id.et_ac_dbb5);
                    break;
                case R.id.btn_ac_dbb6_write:
                    input = (EditText) findViewById(R.id.et_ac_dbb6);
                    break;
                case R.id.btn_ac_dbb7_write:
                    input = (EditText) findViewById(R.id.et_ac_dbb7);
                    break;
                case R.id.btn_ac_dbb8_write:
                    input = (EditText) findViewById(R.id.et_ac_dbb8);
                    break;
                case R.id.btn_ac_dbw18_write:
                    input = (EditText) findViewById(R.id.et_ac_dbw18);
                    break;
            }

            if (input.getText().toString().isEmpty()) {
                Toast.makeText(VueComprimeActivity.this, "Veuillez entrer une valeur à écrire", Toast.LENGTH_SHORT).show();
            }
            else {
                String tag = input.getTag().toString().substring(1);

                EnumComprimes enumComprimes = EnumComprimes.valueOf(EnumComprimes.class, tag);

                int _db = enumComprimes.getPlcDb();

                if(!tag.equals("DBW18")){
                    // if tag is DBB5, DBB6, DBB7 or DBB8

                    int value = Integer.parseInt(input.getText().toString(), 16);
                    if(value <= 127){
                    value = Integer.parseInt(Integer.toBinaryString(Integer.parseInt(String.valueOf(value))));

                        writeTaskS7Thread.writeByteToPLC(_db, String.valueOf(value));
                        Toast.makeText(VueComprimeActivity.this, "Valeur (byte) envoyée à l'automate", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(VueComprimeActivity.this, "La valeur entrée dépasse '127' (7F). Veuillez choisir une valeur plus petite.", Toast.LENGTH_SHORT).show();
                    }

                    // writeTaskS7Thread.WriteByteToPLC(_db, Byte.parseByte(String.valueOf(v)));


                }
                else {

                    int v = Integer.parseInt(input.getText().toString());
                    if(v <= 32767){
                        writeTaskS7Thread.writeWordToPLC(_db, v);
                        Toast.makeText(VueComprimeActivity.this, "Valeur (word) envoyée à l'automate", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(VueComprimeActivity.this, "La valeur entrée dépasse '32767'. Veuillez choisir une valeur plus petite.", Toast.LENGTH_SHORT).show();

                    }
                }
                input.setText("");
            }


        }

    }

    public void changeStateOfEditFields ( boolean b){
        et_ac_dbb5.setEnabled(b);
        btn_ac_dbb5.setEnabled(b);
        et_ac_dbb6.setEnabled(b);
        btn_ac_dbb6.setEnabled(b);
        et_ac_dbb7.setEnabled(b);
        btn_ac_dbb7.setEnabled(b);
        et_ac_dbb8.setEnabled(b);
        btn_ac_dbb8.setEnabled(b);
        et_ac_dbw18.setEnabled(b);
        btn_ac_dbw18.setEnabled(b);

    }

}
