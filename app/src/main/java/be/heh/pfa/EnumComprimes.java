package be.heh.pfa;

public enum EnumComprimes implements IEnumerationPLC {

    EN_MARCHE(0,0),
    B_5_COMPRIMES(0,1),
    B_10_COMPRIMES(0,2),
    B_15_COMPRIMES(0,3),
    RAZ_CPT(1,2),
    MARCHE_MOTEUR(4,1),
    L_5_COMPRIMES(4,3),
    L_10_COMPRIMES(4,4),
    L_15_COMPRIMES(4,5),
    CPT_BOUTEILLES(16,0),
    DBB5(5,0),
    DBB6(6,0),
    DBB7(7,0),
    DBB8(8,0),
    DBW18(18,0);

    private int db;
    private int dbb;

    EnumComprimes(int db, int dbb){
        this.db = db;
        this.dbb = dbb;
    }


    @Override
    public int getPlcDb() {
        return db;
    }

    @Override
    public int getPlcDbb() {
        return dbb;
    }
}
