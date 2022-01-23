package be.heh.pfa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import java.text.DecimalFormat;

import be.heh.pfa.Simatic_S7.S7;
import be.heh.pfa.Simatic_S7.S7Client;
import be.heh.pfa.Simatic_S7.S7OrderCode;

public class ReadTaskS7Thread implements Runnable {

    private final Activity activity;
    private final RelativeLayout relativeLayoutRead;
    private RelativeLayout relativeLayoutWrite = null;
    private final Class enumType;
    private final TextView apv_tv_ordercode;
    private int isConnected = 0;
    private int numCPU = -1;

    private final Automate automate;
    private S7Client s7Client;
    private final PLCHandler plcHandler;
    byte[] dataFromPLC = new byte[512];
    char[] tab;


    public ReadTaskS7Thread(Activity act, Automate auto, TextView tv, RelativeLayout rlr, Class enumType, PLCHandler handler) {
        this.activity = act;
        this.automate = auto;
        this.apv_tv_ordercode = tv;
        this.relativeLayoutRead = rlr;
        this.enumType = enumType;
        this.plcHandler = handler;
    }
    public ReadTaskS7Thread(Activity act, Automate auto, TextView tv, RelativeLayout rlr, RelativeLayout rlrw, Class enumType, PLCHandler handler) {
        this.activity = act;
        this.automate = auto;
        this.apv_tv_ordercode = tv;
        this.relativeLayoutRead = rlr;
        this.relativeLayoutWrite = rlrw;
        this.enumType = enumType;
        this.plcHandler = handler;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void run() {
        try {
                s7Client = new S7Client();
                s7Client.SetConnectionType(S7.S7_BASIC);
                Integer result = s7Client.ConnectTo(automate.getIp(), automate.getRack(), automate.getSlot());
                if(result == 0){
                    Log.i("ReadTaskS7Thread", "Connexion à l'automate " + automate.getName() + " avec l'adresse IP " + automate.getIp());
                }
                else {
                    Log.i("ReadTaskS7Thread", "Échec de connexion à l'automate avec l'erreur " + result);
                }
                S7OrderCode orderCode = new S7OrderCode();
                PLCConnect plcConnect = new PLCConnect(automate);
                isConnected = plcConnect.S7Connect();

                if (isConnected == 0) {


                    S7OrderCode oc = new S7OrderCode();
                    s7Client.GetOrderCode(oc);
                    Integer resOC = s7Client.GetOrderCode(orderCode);
                    isConnected = plcConnect.S7Connect();
                    if(result.equals(0) && resOC.equals(0)){
                        numCPU = Integer.parseInt(orderCode.Code().substring(5, 8));
                    }

                    TextView tv = (TextView) apv_tv_ordercode;
                    if(tv instanceof TextView) {
                        Message msg = new Message();
                        msg.what = PLCHandler.item_numcode;
                        msg.obj = tv;
                        Bundle bundle = new Bundle();
                        bundle.putString("numcode", String.valueOf(numCPU));
                        msg.setData(bundle);
                        plcHandler.sendMessage(msg);
                      //  apv_tv_ordercode.setText(String.valueOf(numCPU));
                    }

                }

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                int childCount = relativeLayoutRead.getChildCount();


                    while (isConnected == 0) {
                        try {
                            isConnected = s7Client.ReadArea(S7.S7AreaDB, 5, 0, 34, dataFromPLC);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    for(int i = 0; i < childCount; i++){
                    View v = relativeLayoutRead.getChildAt(i);
                        if(v.getTag() != null){
                            IEnumerationPLC enumerationPLC = (IEnumerationPLC) Enum.valueOf(enumType, v.getTag().toString());

                            int _db = enumerationPLC.getPlcDb();
                            int _dbb = enumerationPLC.getPlcDbb();
                         // Log.i("ReadTaskS7Thread", v.getTag().toString() + " db : " + _db + " | dbb : " + _dbb);



                            if(v instanceof Switch){
                                boolean bool = S7.GetBitAt(dataFromPLC, _db, _dbb);
                                Switch sw = (Switch) v;
                                if(sw.isChecked() != bool){
                                    Message msg = new Message();
                                    msg.what = PLCHandler.item_switch;
                                    msg.obj = sw;
                                    plcHandler.sendMessage(msg);
                                    //Log.i("ReadTaskS7Thread", v.getTag().toString() + " : " + bool);
                                }
                            }

                            else if (v instanceof EditText){
                                int value = S7.GetWordAt(dataFromPLC, _db);
                                EditText et = (EditText) v;
                                Message msg = new Message();
                                msg.what = PLCHandler.item_edittext;
                                msg.obj = et;
                                Bundle bundle = new Bundle();
                                if(v.getTag().equals("SETPOINT") || v.getTag().equals("NIVEAU_LIQUIDE")) {
                                    float f;
                                    f = (float) value / 100;
                                    bundle.putString("res", String.valueOf(f));
                                }
                                else { bundle.putString("res", String.valueOf(value)); }

                                msg.setData(bundle);
                                plcHandler.sendMessage(msg);
                                //Log.i("ReadTaskS7Thread", v.getTag().toString() + " : " + value);
                            }

                        }

                    }

                    if(relativeLayoutWrite != null){
                        int childCountW = relativeLayoutWrite.getChildCount();
                        for(int i = 0; i < childCountW; i++){
                            View v = relativeLayoutWrite.getChildAt(i);
                            if(v.getTag() != null && v.getTag().toString().startsWith("DB")){
                                IEnumerationPLC enumerationPLC = (IEnumerationPLC) Enum.valueOf(enumType, v.getTag().toString());

                                int _db = enumerationPLC.getPlcDb();


                                if (v instanceof EditText){
                                    int value = S7.GetWordAt(dataFromPLC, _db);
                                    String t = Integer.toBinaryString(value);
                             //       Log.i("ReadTaskS7Thread", t);
                                    EditText et = (EditText) v;
                                    Message msg = new Message();
                                    msg.what = PLCHandler.item_edittext;
                                    msg.obj = et;
                                    Bundle bundle = new Bundle();
                                    if(!v.getTag().toString().startsWith("DBW")){

                                        // getting word at left
                                        String str = Integer.toString(value, 2);

                                        tab = str.toCharArray();
                                        int count = tab.length;
                                        StringBuilder tmp = new StringBuilder(str);
                                        while(count != 16){
                                            tmp.insert(0, "0");
                                            count++;
                                        }
                                        String test;
                                        test = tmp.substring(0, 8);
                                        String hex = String.valueOf(Byte.parseByte(test, 2));
                                       // Log.i("ReadTaskS7Thread", v.getTag() + Integer.toHexString(Integer.parseInt(hex)));

                                        bundle.putString("res", Integer.toHexString(Integer.parseInt(hex)).toUpperCase());

                                    }
                                    else bundle.putString("res", Integer.toString(value));
                                //    Log.i("ReadTaskS7Thread", v.getTag().toString() + Integer.toString(value, 16));
                                    msg.setData(bundle);
                                    plcHandler.sendMessage(msg);
                                    //Log.i("ReadTaskS7Thread", v.getTag().toString() + " : " + value);
                                }

                            }
                        }
                    }

                }


                }




    public void Stop(){
        if(isConnected == 0){
            s7Client.Disconnect();
            Log.i("ReadTaskS7", "Déconnexion de l'automate " + automate.getName());
        }
    }
}
