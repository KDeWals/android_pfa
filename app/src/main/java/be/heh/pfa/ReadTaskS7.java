package be.heh.pfa;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.heh.pfa.Simatic_S7.S7;
import be.heh.pfa.Simatic_S7.S7Client;
import be.heh.pfa.Simatic_S7.S7OrderCode;

public class ReadTaskS7 {

    private View view;
    private Button button;
    private TextView apv_tv_ordercode;
    private Automate automate;
    private S7Client s7Client;
    private int isConnected;
    private int numCPU = -1;


    public ReadTaskS7(View v, Button b, TextView tv, Automate auto) {
        s7Client = new S7Client();
        this.view = v;
        this.button = b;
        this.apv_tv_ordercode = tv;
        this.automate = auto;
    }

    public void Start() {
        try{
            s7Client.SetConnectionType(S7.S7_BASIC);
            Integer result = s7Client.ConnectTo(automate.getIp(), automate.getRack(), automate.getSlot());
            Log.i("ReadTaskS7", "Connexion à l'automate " + automate.getName() + " avec l'adresse IP " + automate.getIp());
            S7OrderCode orderCode = new S7OrderCode();
            PLCConnect plcConnect = new PLCConnect(automate);
            Integer resOC = s7Client.GetOrderCode(orderCode);
            isConnected = plcConnect.S7Connect();
            if(result.equals(0) && resOC.equals(0)){
                // no error

                numCPU = Integer.parseInt(orderCode.Code().substring(5, 8));
            }
            else numCPU = 0000;

            apv_tv_ordercode.setText(apv_tv_ordercode.getText() + String.valueOf(numCPU));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Stop(){
        s7Client.Disconnect();
        Log.i("ReadTaskS7", "Déconnexion de l'automate " + automate.getName());
    }
}