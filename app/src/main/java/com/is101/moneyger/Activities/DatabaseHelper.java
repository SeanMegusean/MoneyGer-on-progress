package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // User table columns
    private static final String UCOLUMN_ID = "U_id";
    private static final String UCOLUMN_USERNAME = "U_username";
    private static final String UCOLUMN_PIN = "U_pin";

    // Wallet table columns
    private static final String WCOLUMN_ID = "W_id";
    private static final String WCOLUMN_BALANCE = "W_balance";
    private static final String WCOLUMN_DESCRIPTION = "W_description";

    // Income table columns
    private static final String ICOLUMN_ID = "I_id";
    private static final String ICOLUMN_NAME = "I_name";
    private static final String ICOLUMN_AMOUNT = "I_amount";
    private static final String ICOLUMN_DESCRIPTION = "I_description";
    private static final String ICOLUMN_DATE = "I_date";

    // Expense table columns
    private static final String ECOLUMN_ID = "E_id";
    private static final String ECOLUMN_NAME = "E_name";
    private static final String ECOLUMN_AMOUNT = "E_amount";
    private static final String ECOLUMN_DESCRIPTION = "E_description";
    private static final String ECOLUMN_DATE = "E_date";

    // Transaction table columns
    private static final String TCOLUMN_ID = "T_id";
    private static final String TCOLUMN_AMOUNT = "T_amount";
    private static final String TCOLUMN_TYPE = "T_type";
    private static final String TCOLUMN_DESCRIPTION = "T_description";
    private static final String TCOLUMN_DATE = "T_date";

    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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
                UCOLUMN_USERNAME + " TEXT NOT NULL, " +
                UCOLUMN_PIN + " TEXT NOT NULL)";

        String createWalletTable = "CREATE TABLE " + TABLE_WALLET + " (" +
                WCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WCOLUMN_BALANCE + " REAL DEFAULT 0, " +
                WCOLUMN_DESCRIPTION + " TEXT)";

        String createIncomeTable = "CREATE TABLE " + TABLE_INCOME + " (" +
                ICOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ICOLUMN_NAME + " TEXT NOT NULL, " +
                ICOLUMN_DESCRIPTION + " TEXT, " +
                ICOLUMN_AMOUNT + " REAL NOT NULL, " +
                ICOLUMN_DATE + " TEXT)";

        String createExpenseTable = "CREATE TABLE " + TABLE_EXPENSE + " (" +
                ECOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ECOLUMN_NAME + " TEXT NOT NULL, " +
                ECOLUMN_DESCRIPTION + " TEXT, " +
                ECOLUMN_AMOUNT + " REAL NOT NULL, " +
                ECOLUMN_DATE + " TEXT)";

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                TCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TCOLUMN_AMOUNT + " REAL NOT NULL, " +
                TCOLUMN_TYPE + " TEXT NOT NULL, " +
                TCOLUMN_DESCRIPTION + " TEXT, " +
                TCOLUMN_DATE + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createWalletTable);
        db.execSQL(createIncomeTable);
        db.execSQL(createExpenseTable);
        db.execSQL(createTransactionsTable);
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0; // True if username is found, False otherwise
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Consider a more sophisticated upgrade strategy to preserve data.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UCOLUMN_USERNAME, username);
        values.put(UCOLUMN_PIN, pin);
        try {
            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Insert User Error: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean checkUser(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                    UCOLUMN_USERNAME + " = ? AND " + UCOLUMN_PIN + " = ?", new String[]{username, pin});
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean insertIncome(String name, double amount, String description, String date) {
        return insertRecord(TABLE_INCOME, ICOLUMN_NAME, name, ICOLUMN_AMOUNT, amount, ICOLUMN_DESCRIPTION, description, ICOLUMN_DATE, date);
    }

    public boolean insertExpense(String name, double amount, String description, String date) {
        return insertRecord(TABLE_EXPENSE, ECOLUMN_NAME, name, ECOLUMN_AMOUNT, amount, ECOLUMN_DESCRIPTION, description, ECOLUMN_DATE, date);
    }

    public boolean insertInitialBalance(double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WCOLUMN_BALANCE, balance);
        try {
            long result = db.insert(TABLE_WALLET, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Insert Initial Balance Error: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public double getCurrentBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double balance = 0.0;
        try {
            cursor = db.rawQuery("SELECT " + WCOLUMN_BALANCE + " FROM " + TABLE_WALLET, null);
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return balance;
    }

    public boolean updateBalance(double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WCOLUMN_BALANCE, newBalance);
        try {
            int rows = db.update(TABLE_WALLET, values, null, null);
            return rows > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Update Balance Error: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insertTransaction(double amount, String type, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TCOLUMN_AMOUNT, amount);
        values.put(TCOLUMN_TYPE, type);
        values.put(TCOLUMN_DESCRIPTION, description);
        values.put(TCOLUMN_DATE, date);
        try {
            long result = db.insert(TABLE_TRANSACTIONS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Insert Transaction Error: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);
    }

    private boolean insertRecord(String table, String columnName, String name, String columnAmount, double amount,
                                 String columnDescription, String description, String columnDate, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnName, name);
        values.put(columnAmount, amount);
        values.put(columnDescription, description);
        values.put(columnDate, date);
        try {
            long result = db.insert(table, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Insert Record Error: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
}