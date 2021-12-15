package be.heh.pfa;

import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.heh.pfa.Simatic_S7.S7Client;
import be.heh.pfa.Simatic_S7.S7OrderCode;

public class PLCReadThread implements Runnable {

    private RelativeLayout rl;
    private Class enumerationType;
    private Handler PLCHandler;
    private Automate automate;
    private TextView plcnumcode;
    private S7Client s7Client;
    byte PLCdata[];

    public PLCReadThread(RelativeLayout rl, Class enumerationType, Handler handler,
                         TextView numcode, byte[] d, Automate auto){
        this.rl = rl;
        this.enumerationType = enumerationType;
        this.PLCHandler = handler;
        this.plcnumcode = numcode;
        this.PLCdata = d;
        this.automate = auto;
    }

    @Override
    public void run() {

        int isConnectedToPLC = 0;

        PLCConnect plcConnect = new PLCConnect(automate);
        isConnectedToPLC = plcConnect.S7Connect();

        if(isConnectedToPLC == 0){
            S7OrderCode orderCode = new S7OrderCode();

        }

    }
}
