package be.heh.pfa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="pfa.db";
    public static final String TABLE_NAME_USER="users";
    public static final String COL_1_U="ID";
    public static final String COL_2_U="NAME";
    public static final String COL_3_U="FIRSTNAME";
    public static final String COL_4_U="EMAIL";
    public static final String COL_5_U="PASSWORD";
    public static final String COL_6_U="ROLE";
    public static final String COL_7_U="PERM";
    private static final String[] COLUMNS = {
            COL_1_U, COL_2_U, COL_3_U, COL_4_U, COL_5_U, COL_6_U, COL_7_U
    };

    private static final String TABLE_NAME_AUTO="automates";
    private static final String COL_1_A="ID";
    private static final String COL_2_A="NAME";
    private static final String COL_3_A="IP";
    private static final String COL_4_A="RACK";
    private static final String COL_5_A="SLOT";
    private static final String COL_6_A="TYPE";
    private static final String[] COLUMNS_A = {
            COL_1_A, COL_2_A, COL_3_A, COL_4_A, COL_5_A, COL_6_A
    };


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, FIRSTNAME TEXT, EMAIL TEXT UNIQUE, PASSWORD TEXT, ROLE TEXT, PERM TEXT)");
        db.execSQL("CREATE TABLE automates (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, IP TEXT UNIQUE, RACK INT, SLOT INT, TYPE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_AUTO);
        onCreate(db);
    }

    public long addUser(String name, String firstname, String email, String password, String role, String perm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("NAME", name);
        userValues.put("FIRSTNAME", firstname);
        email = email.toLowerCase();
        userValues.put("EMAIL", email);
        userValues.put("PASSWORD", password);
        userValues.put("ROLE", role);
        userValues.put("PERM", perm);
        long res = db.insert("users", null, userValues);
        db.close();
        return res;
    }

    public User setUser(String email, String passw) {

        User user = new User();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USER,
                COLUMNS,
                "EMAIL = ? AND PASSWORD = ? ",
                new String[] {String.valueOf(email),String.valueOf(passw)},
                null, null, null, null);
        final int idIndex = cursor.getColumnIndex(COL_1_U);
        final int nameIndex = cursor.getColumnIndex(COL_2_U);
        final int firstnameIndex = cursor.getColumnIndex(COL_3_U);
        final int emailIndex = cursor.getColumnIndex(COL_4_U);
        final int pwIndex = cursor.getColumnIndex(COL_5_U);
        final int roleIndex = cursor.getColumnIndex(COL_6_U);
        final int permIndex = cursor.getColumnIndex(COL_7_U);
        try {

            // If moveToFirst() returns false then cursor is empty
            if(!cursor.moveToFirst()) { return null; }
            do {
                user.setId(cursor.getInt(idIndex));
                user.setName(cursor.getString(nameIndex));
                user.setFirstname(cursor.getString(firstnameIndex));
                user.setEmail(cursor.getString(emailIndex));
                user.setPassword(cursor.getString(pwIndex));
                user.setRole(cursor.getString(roleIndex));
                user.setPerm(cursor.getString(permIndex));
            } while (cursor.moveToNext());

            return user;

        } finally {
            // Using a try/finally is usually the best way to avoid memory leaks.
            cursor.close();

            // close the database
            db.close();
        }
    }

    public int countUsers(){
        int acc = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_USER;
        Cursor cursor = db.rawQuery(query, null);
        acc = cursor.getCount();
        return acc;
    }

    public void changeUserPermission(String email, String perm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PERM", perm);
        db.update(TABLE_NAME_USER, values, "EMAIL = ?", new String [] {email});
        db.close();
    }

    public void changeUserName(String mail, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", newName);
        db.update(TABLE_NAME_USER, contentValues, "EMAIL = ?", new String[] {mail});
        db.close();
    }

    public void changeUserFirstname(String mail, String newFirstname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FIRSTNAME", newFirstname);
        db.update(TABLE_NAME_USER, contentValues, "EMAIL = ?", new String[] {mail});
        db.close();
    }
    public void changeUserPassword(String mail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PASSWORD", newPassword);
        db.update(TABLE_NAME_USER, contentValues, "EMAIL = ?", new String[] {mail});
        db.close();
    }



    public void removeUser(String email)
    {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME_USER, "EMAIL = ?", new String[] {email});
        }

    }

    public long addAutomate(String name, String ip, int rack, int slot, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("IP", ip);
        contentValues.put("RACK", rack);
        contentValues.put("SLOT", slot);
        contentValues.put("TYPE", type);

        long res = db.insert("automates", null, contentValues);
        db.close();
        return res;
    }

    public void changeAutomateName(String ip, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        db.update(TABLE_NAME_AUTO, contentValues, "IP = ?", new String[] {ip});
        db.close();
    }

    public void changeAutomateNetworkInfo(String oldIp, String newIp, int r, int s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IP", newIp);
        contentValues.put("RACK", r);
        contentValues.put("SLOT", s);
        db.update(TABLE_NAME_AUTO, contentValues, "IP = ?", new String[]{oldIp});
        db.close();
    }

    public void changeAutomateType(String ip, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TYPE", type);
        db.update(TABLE_NAME_AUTO, contentValues, "IP = ?", new String[] {ip});
        db.close();
    }

    public void deleteAutomateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_AUTO, null, null);
        db.close();
    }


    public User checkUserLogin(String email, String passw) {

        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USER,
                COLUMNS,
                "EMAIL = ? AND PASSWORD = ? ",
                new String[] {String.valueOf(email),String.valueOf(passw)},
                null, null, null, null);
        final int idIndex = cursor.getColumnIndex(COL_1_U);
        final int nameIndex = cursor.getColumnIndex(COL_2_U);
        final int firstnameIndex = cursor.getColumnIndex(COL_3_U);
        final int emailIndex = cursor.getColumnIndex(COL_4_U);
        final int pwIndex = cursor.getColumnIndex(COL_5_U);
        final int roleIndex = cursor.getColumnIndex(COL_6_U);
        final int permIndex = cursor.getColumnIndex(COL_7_U);
        try {

            // If moveToFirst() returns false then cursor is empty
            if(!cursor.moveToFirst()) { return null; }
            do {
                user.setId(cursor.getInt(idIndex));
                user.setName(cursor.getString(nameIndex));
                user.setFirstname(cursor.getString(firstnameIndex));
                user.setEmail(cursor.getString(emailIndex));
                user.setPassword(cursor.getString(pwIndex));
                user.setRole(cursor.getString(roleIndex));
                user.setPerm(cursor.getString(permIndex));
            } while (cursor.moveToNext());

            return user;

        } finally {

            cursor.close();

            // close the database
            db.close();
        }
    }

    public User getUserInfo(String email) {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USER,
                COLUMNS,
                "EMAIL = ? ",
                new String[] {String.valueOf(email)},
                null, null, null, null);
        final int idIndex = cursor.getColumnIndex(COL_1_U);
        final int nameIndex = cursor.getColumnIndex(COL_2_U);
        final int firstnameIndex = cursor.getColumnIndex(COL_3_U);
        final int emailIndex = cursor.getColumnIndex(COL_4_U);
        final int roleIndex = cursor.getColumnIndex(COL_6_U);
        final int permIndex = cursor.getColumnIndex(COL_7_U);
        try {

            // If moveToFirst() returns false then cursor is empty
            if(!cursor.moveToFirst()) { return null; }
            do {
                user.setId(cursor.getInt(idIndex));
                user.setName(cursor.getString(nameIndex));
                user.setFirstname(cursor.getString(firstnameIndex));
                user.setEmail(cursor.getString(emailIndex));
                user.setRole(cursor.getString(roleIndex));
                user.setPerm(cursor.getString(permIndex));
            } while (cursor.moveToNext());

            return user;


        } finally {

            cursor.close();
            db.close();
        }
    }



    public ArrayList<User> checkAdmin(){
        ArrayList<User> adminList = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT EMAIL FROM " + TABLE_NAME_USER + " WHERE ROLE = 'admin'";
        Cursor cursor = db.rawQuery(query, null);
        User admin = null;

        if(cursor.moveToFirst()) {
            do {
                admin = new User();
                admin.setEmail(cursor.getString(0));
                adminList.add(admin);
            }
            while (cursor.moveToNext());
        }
        return adminList;
    }

    public Automate getAutomate(String ip) {

        Automate automate = new Automate();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_AUTO,
                COLUMNS_A,
                "IP = ? ",
                new String[] {String.valueOf(ip)},
                null, null, null, null);
//        private static final String COL_1_A="ID";
//        private static final String COL_2_A="NAME";
//        private static final String COL_3_A="IP";
//        private static final String COL_4_A="RACK";
//        private static final String COL_5_A="SLOT";
//        private static final String COL_6_A="TYPE";

        final int idIndex = cursor.getColumnIndex(COL_1_A);
        final int nameIndex = cursor.getColumnIndex(COL_2_A);
        final int ipIndex = cursor.getColumnIndex(COL_3_A);
        final int rackIndex = cursor.getColumnIndex(COL_4_A);
        final int slotIndex = cursor.getColumnIndex(COL_5_A);
        final int typeIndex = cursor.getColumnIndex(COL_6_A);
        try {

            // If moveToFirst() returns false then cursor is empty
            if(!cursor.moveToFirst()) { return null; }
            do {
                automate.setId(cursor.getInt(idIndex));
                automate.setName(cursor.getString(nameIndex));
                automate.setIp(cursor.getString(ipIndex));
                automate.setRack(cursor.getInt(rackIndex));
                automate.setSlot(cursor.getInt(slotIndex));
                automate.setType(cursor.getString(typeIndex));
            } while (cursor.moveToNext());

            return automate;

        } finally {
            // Using a try/finally is usually the best way to avoid memory leaks.
            cursor.close();

            // close the database
            db.close();
        }
    }

    public ArrayList<User> getAllUsers() {

        ArrayList<User> users = new ArrayList<User>();
        String query = "SELECT * FROM " + TABLE_NAME_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;

        if(cursor.moveToFirst()) {
            do {
                user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setName(cursor.getString(1));
                user.setFirstname(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setRole(cursor.getString(5));
                user.setPerm(cursor.getString(6));
                users.add(user);
            }
            while (cursor.moveToNext());
        }
        return users;
    }

    public void deleteAllLambdaUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_USER, "ROLE = ?", new String[] {"lamda"});

    }



    public ArrayList<Automate> getAllAutomates() {

        ArrayList<Automate> automates = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME_AUTO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Automate automate = null;

        try {
            if(!cursor.moveToFirst()) {
                return null;
            }
                do {
                    /*
                    HashMap<String, String> plc = new HashMap<>();
                    plc.put("name", cursor.getString(cursor.getColumnIndex(COL_2_A)));
                    plc.put("ip", cursor.getString(cursor.getColumnIndex(COL_3_A)));
                    plc.put("rack", cursor.getString(cursor.getColumnIndex(COL_4_A)));
                    plc.put("slot", cursor.getString(cursor.getColumnIndex(COL_5_A)));
                    plc.put("type", cursor.getString(cursor.getColumnIndex(COL_6_A)));
                    automates.add(plc);
                */
                    automate = new Automate();
                    automate.setId(Integer.parseInt(cursor.getString(0)));
                    automate.setName(cursor.getString(1));
                    automate.setIp(cursor.getString(2));
                    automate.setRack(Integer.parseInt(cursor.getString(3)));
                    automate.setSlot(Integer.parseInt(cursor.getString(4)));
                    automate.setType(cursor.getString(5));
                    automates.add(automate);


                }
                while (cursor.moveToNext());
            return automates;
        }
        finally {
            cursor.close();
            db.close();
        }
    }

    public void deleteAllPLCs(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_AUTO,null, null);
    }

    public void deletePLC(String ip){

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME_AUTO, "IP = ?", new String[] {ip});
        }
    }

    public long countAutomates(){


        long PLCcount = 0;
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME_AUTO;

        Cursor cur = getReadableDatabase().rawQuery(sql, null);

        if(cur.getCount() > 0) {
            cur.moveToFirst();
            PLCcount = cur.getInt(0);
        }
        else return 0;

        cur.close();

        return PLCcount;


    }



//    public boolean checkUser(String username, String password){
//        String[] columns = { COL_1 , COL_2, COL_3};
//        SQLiteDatabase db = getReadableDatabase();
//        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
//        String[] selectionArgs =  { username, password };
//        Cursor cursor = db.query(TABLE_NAME, columns,selection, selectionArgs, null, null, null);
//        int count = cursor.getCount();
//        cursor.close();
//        db.close();
//
//        if(count > 0) {
//            return true;
//        }
//        else {
//            return false;
//        }
//
//
//
//    }
}
