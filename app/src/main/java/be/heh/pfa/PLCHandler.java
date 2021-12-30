package be.heh.pfa;

import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class PLCHandler extends Handler {

    public static final int item_switch = 0;
    public static final int item_edittext = 1;
    public static final int item_numcode = 2;

    @Override
    public void handleMessage(Message msg){
    super.handleMessage(msg);
    int response = msg.what;

    switch (response){
        case item_switch :
            Switch sw = (Switch) msg.obj;
            sw.setChecked(!sw.isChecked());
            sw.jumpDrawablesToCurrentState();
            break;

        case item_edittext :
            EditText tv = (EditText) msg.obj;
            String string = msg.getData().getString("res");
            tv.setText(string);
            break;
        case item_numcode :
            TextView num = (TextView) msg.obj;
            String str = msg.getData().getString("numcode");
            num.setText(str);


    }

    }
}
