package be.heh.pfa;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="pfa.db";
    private static final String TABLE_NAME_USER="users";
    private static final String COL_1_U="ID";
    private static final String COL_2_U="NAME";
    private static final String COL_3_U="FIRSTNAME";
    private static final String COL_4_U="EMAIL";
    private static final String COL_5_U="PASSWORD";
    private static final String COL_6_U="ROLE";
    private static final String COL_7_U="PERM";
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
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("FIRSTNAME", firstname);
        contentValues.put("EMAIL", email);
        contentValues.put("PASSWORD", password);
        contentValues.put("ROLE", role);
        contentValues.put("PERM", perm);

        long res = db.insert("users", null, contentValues);
        db.close();
        return res;
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



//    public void changePerm(User user, String newPerm) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("PERM", newPerm);
//    }


    public User getUser(String email, String passw) {

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
            // Using a try/finally is usually the best way to avoid memory leaks.
            cursor.close();

            // close the database
            db.close();
        }
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
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;

        if(cursor.moveToFirst()) {
            do {
                user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setName(cursor.getString(1));
                user.setFirstname(cursor.getString(2));
                user.setRole(cursor.getString(3));
                users.add(user);
            }
            while (cursor.moveToNext());
        }
        return users;
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
