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


import be.heh.pfa.Simatic_S7.S7;
import be.heh.pfa.Simatic_S7.S7Client;
import be.heh.pfa.Simatic_S7.S7OrderCode;

public class ReadTaskS7Thread implements Runnable {

    private Activity activity;
    private RelativeLayout relativeLayoutRead;
    private RelativeLayout relativeLayoutWrite = null;
    private Class enumType;
    private TextView apv_tv_ordercode;
    private int isConnected = 0;
    private int numCPU = -1;

    private Automate automate;
    private S7Client s7Client;
    private PLCHandler plcHandler;
    byte[] dataFromPLC = new byte[512];


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
                while (isConnected == 0){
                    try
                    {
                        isConnected = s7Client.ReadArea(S7.S7AreaDB,5,0,34,dataFromPLC);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    for(int i = 0; i < childCount; i++){
                    View v = relativeLayoutRead.getChildAt(i);
                    if(v.getTag() != null){

                        IEnumerationPLC enumerationPLC = (IEnumerationPLC) Enum.valueOf(enumType, v.getTag().toString());

                        int _db = enumerationPLC.getPlcDb();
                        int _dbb = enumerationPLC.getPlcDbb();
                //        Log.i("ReadTaskS7Thread", v.getTag().toString() + " db : " + _db + " | dbb : " + _dbb);



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
                            bundle.putString("res", String.valueOf(value));
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
                            if(v.getTag() != null){
                                IEnumerationPLC enumerationPLC = (IEnumerationPLC) Enum.valueOf(enumType, v.getTag().toString());

                                int _db = enumerationPLC.getPlcDb();
                                int _dbb = enumerationPLC.getPlcDbb();

                                if (v instanceof EditText){
                                    int value = S7.GetWordAt(dataFromPLC, _db);
                                    EditText et = (EditText) v;
                                    Message msg = new Message();
                                    msg.what = PLCHandler.item_edittext;
                                    msg.obj = et;
                                    Bundle bundle = new Bundle();
                                    if(!v.getTag().equals("DBW18")){
                                        bundle.putString("res", Integer.toHexString(Integer.parseInt(String.valueOf(value))).toUpperCase());
                                    }
                                    else bundle.putString("res", String.valueOf(Integer.parseInt(String.valueOf(value))));
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
