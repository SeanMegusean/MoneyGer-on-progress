package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";

    // Columns for users table
    private static final String UCOLUMN_ID = "U_id";
    private static final String UCOLUMN_USERNAME = "U_username";
    private static final String UCOLUMN_PIN = "U_pin";

    // Columns for wallet table
    private static final String WCOLUMN_ID = "W_id";
    private static final String WCOLUMN_BALANCE = "W_balance";
    private static final String WCOLUMN_DESCRIPTION = "W_description";

    // Columns for income table
    private static final String ICOLUMN_ID = "I_id";
    private static final String ICOLUMN_NAME = "I_name";
    private static final String ICOLUMN_AMOUNT = "I_amount";
    private static final String ICOLUMN_DESCRIPTION = "I_description";
    private static final String ICOLUMN_DATE = "I_date";

    // Columns for expense table
    private static final String ECOLUMN_ID = "E_id";
    private static final String ECOLUMN_NAME = "E_name";
    private static final String ECOLUMN_AMOUNT = "E_amount";
    private static final String ECOLUMN_DESCRIPTION = "E_description";
    private static final String ECOLUMN_DATE = "E_date";

    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // method para pang isahan nalang pag tatawagin :)
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                UCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UCOLUMN_USERNAME + " TEXT, " +
                UCOLUMN_PIN + " TEXT)";

        String createWalletTable = "CREATE TABLE " + TABLE_WALLET + " (" +
                WCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WCOLUMN_BALANCE + " REAL, " +
                WCOLUMN_DESCRIPTION + " TEXT)";

        String createIncomeTable = "CREATE TABLE " + TABLE_INCOME + " (" +
                ICOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ICOLUMN_NAME + " TEXT, " +
                ICOLUMN_DESCRIPTION + " TEXT, " +
                ICOLUMN_AMOUNT + " REAL, " +
                ICOLUMN_DATE + " TEXT)";

        String createExpenseTable = "CREATE TABLE " + TABLE_EXPENSE + " (" +
                ECOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ECOLUMN_NAME + " TEXT, " +
                ECOLUMN_DESCRIPTION + " TEXT, " +
                ECOLUMN_AMOUNT + " REAL, " +
                ECOLUMN_DATE + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createWalletTable);
        db.execSQL(createIncomeTable);
        db.execSQL(createExpenseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        onCreate(db);
    }

    // Insert a new user into the Users table
    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UCOLUMN_USERNAME, username);
        values.put(UCOLUMN_PIN, pin);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if a user exists in the Users table
    public boolean checkUser(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                UCOLUMN_USERNAME + " = ? AND " + UCOLUMN_PIN + " = ?", new String[]{username, pin});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Insert a record in the Income table
    public boolean insertIncome(String name, double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ICOLUMN_NAME, name);
        values.put(ICOLUMN_AMOUNT, amount);
        values.put(ICOLUMN_DESCRIPTION, description);
        values.put(ICOLUMN_DATE, date);
        long result = db.insert(TABLE_INCOME, null, values);
        db.close();
        return result != -1;
    }

    // Insert a record in the Expense table
    public boolean insertExpense(String name, double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ECOLUMN_NAME, name);
        values.put(ECOLUMN_AMOUNT, amount);
        values.put(ECOLUMN_DESCRIPTION, description);
        values.put(ECOLUMN_DATE, date);
        long result = db.insert(TABLE_EXPENSE, null, values);
        db.close();
        return result != -1;
    }

    // Insert initial balance in Wallet table
    public boolean insertInitialBalance(double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WCOLUMN_BALANCE, balance);
        long result = db.insert(TABLE_WALLET, null, values);
        db.close();
        return result != -1;
    }

    // Get current balance from Wallet table
    public double getCurrentBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + WCOLUMN_BALANCE + " FROM " + TABLE_WALLET, null);
        double balance = 0.0;
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return balance;
    }

    // Update balance in Wallet table
    public boolean updateBalance(double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WCOLUMN_BALANCE, newBalance);
        int rows = db.update(TABLE_WALLET, values, null, null);
        db.close();
        return rows > 0;
    }
}
