package be.heh.pfa;

public enum EnumLiquide implements IEnumerationPLC {

    B_VANNE_1_PRINCIPALE(0,1),
    B_VANNE_2_GAUCHE(0,2),
    B_VANNE_3_CENTRE(0,3),
    B_VANNE_4_DROITE(0,4),
    C_AUTO_MANU(0,5),
    VANNE_1_PRINCIPALE(0, 1),
    VANNE_2_GAUCHE(0, 2),
    VANNE_3_CENTRE(0, 3),
    VANNE_4_DROITE(0, 4),
    NIVEAU_LIQUIDE(16, 0),
    SETPOINT(18, 0),
    CONSIGNE(20, 0),
    SORTIE(22, 0),
    DBB2(2, 0),
    DBB3(3, 0),
    DBW24(24, 0),
    DBW26(26, 0),
    DBW28(28, 0),
    DBW30(30, 0);

    private int db;
    private int dbb;

    EnumLiquide(int db, int dbb){
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
