package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.is101.moneyger.Activities.model.RecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_SAVINGS = "savings";

    // Column names for each table
    private static final String UCOLUMN_ID = "U_id";
    private static final String UCOLUMN_USERNAME = "U_username";
    private static final String UCOLUMN_PIN = "U_pin";

    private static final String WCOLUMN_ID = "W_id";
    private static final String WCOLUMN_BALANCE = "W_balance";
    private static final String WCOLUMN_DESCRIPTION = "W_description";

    private static final String ICOLUMN_ID = "I_id";
    private static final String ICOLUMN_NAME = "I_name";
    private static final String ICOLUMN_AMOUNT = "I_amount";
    private static final String ICOLUMN_DESCRIPTION = "I_description";
    private static final String ICOLUMN_DATE = "I_date";

    private static final String ECOLUMN_ID = "E_id";
    private static final String ECOLUMN_NAME = "E_name";
    private static final String ECOLUMN_AMOUNT = "E_amount";
    private static final String ECOLUMN_DESCRIPTION = "E_description";
    private static final String ECOLUMN_DATE = "E_date";

    private static final String TCOLUMN_ID = "T_id";
    private static final String TCOLUMN_AMOUNT = "T_amount";
    private static final String TCOLUMN_TYPE = "T_type";
    private static final String TCOLUMN_DESCRIPTION = "T_description";
    private static final String TCOLUMN_DATE = "T_date";

    private static final String SCOLUMN_ID = "S_id";
    private static final String SCOLUMN_NAME = "S_name";
    private static final String SCOLUMN_AMOUNT = "S_amount";
    private static final String SCOLUMN_DATE = "S_date";

    private static DatabaseHelper instance;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(createUsersTable());
        db.execSQL(createWalletTable());
        db.execSQL(createIncomeTable());
        db.execSQL(createExpenseTable());
        db.execSQL(createTransactionsTable());
        db.execSQL(createSavingsTable());
    }

    // SQL table creation methods
    private String createUsersTable() {
        return "CREATE TABLE " + TABLE_USERS + " (" +
                UCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UCOLUMN_USERNAME + " TEXT NOT NULL, " +
                UCOLUMN_PIN + " TEXT NOT NULL)";
    }

    private String createWalletTable() {
        return "CREATE TABLE " + TABLE_WALLET + " (" +
                WCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WCOLUMN_BALANCE + " REAL DEFAULT 0, " +
                WCOLUMN_DESCRIPTION + " TEXT)";
    }

    private String createIncomeTable() {
        return "CREATE TABLE " + TABLE_INCOME + " (" +
                ICOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ICOLUMN_NAME + " TEXT NOT NULL, " +
                ICOLUMN_DESCRIPTION + " TEXT, " +
                ICOLUMN_AMOUNT + " REAL NOT NULL, " +
                ICOLUMN_DATE + " TEXT)";
    }

    private String createExpenseTable() {
        return "CREATE TABLE " + TABLE_EXPENSE + " (" +
                ECOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ECOLUMN_NAME + " TEXT NOT NULL, " +
                ECOLUMN_DESCRIPTION + " TEXT, " +
                ECOLUMN_AMOUNT + " REAL NOT NULL, " +
                ECOLUMN_DATE + " TEXT)";
    }

    private String createTransactionsTable() {
        return "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                TCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TCOLUMN_AMOUNT + " REAL NOT NULL, " +
                TCOLUMN_TYPE + " TEXT NOT NULL, " +
                TCOLUMN_DESCRIPTION + " TEXT, " +
                TCOLUMN_DATE + " TEXT)";
    }

    private String createSavingsTable() {
        return "CREATE TABLE " + TABLE_SAVINGS + " (" +
                SCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCOLUMN_NAME + " TEXT NOT NULL, " +
                SCOLUMN_AMOUNT + " REAL NOT NULL, " +
                SCOLUMN_DATE + " TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        onCreate(db);
    }

    public boolean checkUser(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean userExists = false;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{UCOLUMN_ID},
                    UCOLUMN_USERNAME + " = ? AND " + UCOLUMN_PIN + " = ?",
                    new String[]{username, pin},
                    null, null, null);

            userExists = (cursor != null && cursor.moveToFirst());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userExists;
    }

    public boolean insertSavings(String name, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCOLUMN_NAME, name);
        values.put(SCOLUMN_AMOUNT, amount);
        values.put(SCOLUMN_DATE, date);
        long result = db.insert(TABLE_SAVINGS, null, values);
        db.close(); // Close database to free resources
        return result != -1;
    }

    public List<RecyclerModel> getAllSavings() {
        List<RecyclerModel> savingsList = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SAVINGS, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(SCOLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(SCOLUMN_NAME);
                    int amountIndex = cursor.getColumnIndex(SCOLUMN_AMOUNT);
                    int dateIndex = cursor.getColumnIndex(SCOLUMN_DATE);

                    // Check if indices are valid
                    if (idIndex == -1 || nameIndex == -1 || amountIndex == -1 || dateIndex == -1) {
                        Log.e("DatabaseHelper", "Column index not found. Check table definition.");
                        continue; // Skip this iteration if any index is invalid
                    }

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String date = cursor.getString(dateIndex);

                    savingsList.add(new RecyclerModel(id, name, amount, date)); // Adjust constructor as necessary
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Get All Savings Error: " + e.getMessage());
        }
        // Ensure cursor is closed
        // Close database to free resources
        return savingsList;
    }

    public double getTotalSavings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double total = 0.0;

        try {
            cursor = db.rawQuery("SELECT SUM(" + SCOLUMN_AMOUNT + ") FROM " + TABLE_SAVINGS, null);
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Get Total Savings Error: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure cursor is closed
            }
            db.close(); // Close database to free resources
        }
        return total;
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean userExists = false;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{UCOLUMN_ID},
                    UCOLUMN_USERNAME + " = ?",
                    new String[]{username},
                    null, null, null);

            userExists = (cursor != null && cursor.moveToFirst());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if user exists: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userExists;
    }

    // Method to insert a new user
    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UCOLUMN_USERNAME, username);
        values.put(UCOLUMN_PIN, pin); // Consider hashing the PIN for security

        long result = db.insert(TABLE_USERS, null, values);
        db.close(); // Close database to free resources
        return result != -1; // Return true if insertion was successful
    }

}