package be.heh.pfa;

import android.os.Handler;
import android.os.Message;
import android.widget.Switch;
import android.widget.TextView;

public class PLCHandler extends Handler {

    public static final int item_switch = 0;
    public static final int item_edittext = 1;

    @Override
    public void handleMessage(Message msg){
    super.handleMessage(msg);

    int response = msg.what;

    switch (response){

        case item_switch :
            Switch sw = (Switch) msg.obj;
            sw.setChecked(!sw.isChecked());
            break;

        case item_edittext :
            TextView tv = (TextView) msg.obj;
            String string = msg.getData().getString("valeur");
            tv.setText(string);
            break;

    }

    }
}
