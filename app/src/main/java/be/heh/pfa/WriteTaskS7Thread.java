package be.heh.pfa;


import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.pfa.Simatic_S7.S7;
import be.heh.pfa.Simatic_S7.S7Client;

public class WriteTaskS7Thread implements Runnable {

    private AtomicBoolean isRunning = new AtomicBoolean(false);


    private S7Client s7Client = null;
    private Automate automate;
    byte[] dataToPLC;
    int isConnected = 0;


    public WriteTaskS7Thread(byte[] data, Automate auto) {
        this.dataToPLC = data;
        this.automate = auto;
    }

//    public void start(Automate auto) {
//        if (!writingThread.isAlive()) {
//            automate.setIp(auto.getIp());
//            automate.setRack(auto.getRack());
//            automate.setSlot(auto.getSlot());
//            writingThread.start();
//            isRunning.set(true);
//        }
//    }
//
//    public void stop() {
//        isRunning.set(false);
//        s7Client.Disconnect();
//        writingThread.interrupt();
//    }

    @Override
    public void run() {
        try {
            PLCConnect plcConnect = new PLCConnect(automate);
            isConnected = plcConnect.S7Connect();
            s7Client = plcConnect.getS7Client();
            while (isConnected == 0) {


                while (isConnected == 0){
                    s7Client.WriteArea(S7.S7AreaDB, 5, 0, 34, dataToPLC);
                }


                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public void writeByteToPLC(int _db, String val) {
        char[] c;
        c = val.toCharArray();
        byte[] o = new byte[8];
        int count = c.length;
        StringBuilder tmp = new StringBuilder(val);
        while (count != 8) {
            tmp.insert(0, "0");
            count++;
        }
        String output;
        StringBuilder t = new StringBuilder();
        output = tmp.substring(0, 8);
        c = output.toCharArray();
        for (int i = count - 1; i > 0; i--) {
            if (c[i] == '1') {
                o[i] = 0x01;
                t.append(o[i]);
            } else {
                o[i] = 0x00;
                t.append(o[i]);
            }
        }

        byte b = Byte.parseByte(tmp.toString(), 2);

        dataToPLC[_db] = b;

    }

    public void writeWordToPLC(int _db, int val) {
        S7.SetWordAt(dataToPLC, _db, val);
    }

    public void Stop(){
        try{
            if(isConnected == 0){
                s7Client.Disconnect();
                Log.i("WriteTaskS7", "Déconnexion de l'automate " + automate.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
