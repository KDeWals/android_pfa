package be.heh.pfa;

import be.heh.pfa.Simatic_S7.S7Client;

public class PLCConnect {

    private Automate automate;
    private S7Client s7Client;

    public PLCConnect(Automate automate){
        this.automate = automate;
        s7Client = new S7Client();
    }

    public void S7Disconnect(){
        s7Client.Disconnect();

    }

    public int S7Connect(){
       return s7Client.ConnectTo(automate.getIp(), automate.getRack(), automate.getSlot());
    }
}
