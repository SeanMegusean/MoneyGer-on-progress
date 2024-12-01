package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.is101.moneyger.Activities.model.RecyclerModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 4; // Incremented version for changes

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_SAVINGS = "savings";

    // Column names for users table
    private static final String UCOLUMN_ID = "U_id";
    private static final String UCOLUMN_USERNAME = "U_username";
    private static final String UCOLUMN_PIN = "U_pin";

    // Column names for wallet table
    private static final String WCOLUMN_ID = "W_id";
    private static final String WCOLUMN_BALANCE = "W_balance";
    private static final String WCOLUMN_DESCRIPTION = "W_description";
    private static final String WCOLUMN_USER_ID = "UserID"; // User ID column

    // Column names for expense table
    private static final String ECOLUMN_ID = "E_id";
    private static final String ECOLUMN_NAME = "E_name";
    private static final String ECOLUMN_AMOUNT = "E_amount";
    private static final String ECOLUMN_DESCRIPTION = "E_description";
    private static final String ECOLUMN_DATE = "E_date";
    private static final String ECOLUMN_USER_ID = "UserID"; // User ID column

    // Column names for savings table
    private static final String SCOLUMN_ID = "S_id";
    private static final String SCOLUMN_NAME = "S_name";
    private static final String SCOLUMN_AMOUNT = "S_amount";
    private static final String SCOLUMN_DATE = "S_date";

    // Singleton instance
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
        db.execSQL(createExpenseTable());
        db.execSQL(createSavingsTable()); // Create savings table

        // Log table creation
        Log.d("DatabaseHelper", "Tables created successfully.");
    }

    // SQL table creation methods
    private String createUsersTable() {
        return "CREATE TABLE " + TABLE_USERS + " (" +
                UCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UCOLUMN_USERNAME + " TEXT NOT NULL, " +
                UCOLUMN_PIN + " TEXT NOT NULL)";
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

    private String createWalletTable() {
        return "CREATE TABLE " + TABLE_WALLET + " (" +
                WCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WCOLUMN_BALANCE + " REAL DEFAULT 0, " +
                WCOLUMN_DESCRIPTION + " TEXT, " +
                WCOLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + WCOLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + UCOLUMN_ID + "))";
    }

    private String createExpenseTable() {
        return "CREATE TABLE " + TABLE_EXPENSE + " (" +
                ECOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ECOLUMN_NAME + " TEXT NOT NULL, " +
                ECOLUMN_DESCRIPTION + " TEXT, " +
                ECOLUMN_AMOUNT + " REAL NOT NULL, " +
                ECOLUMN_DATE + " TEXT, " +
                ECOLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + ECOLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + UCOLUMN_ID + "))";
    }

    private String createSavingsTable() {
        return "CREATE TABLE " + TABLE_SAVINGS + " (" +
                SCOLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCOLUMN_NAME + " TEXT NOT NULL, " +
                SCOLUMN_AMOUNT + " REAL NOT NULL, " +
                SCOLUMN_DATE + " TEXT NOT NULL)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS); // Drop savings table

        // Create tables again
        onCreate(db);
    }

    // Method to get all savings
    public List<RecyclerModel> getAllSavings() {
        List<RecyclerModel> savingsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SAVINGS, null);

        // Log the column names
        String[] columnNames = cursor.getColumnNames();
        Log.d("DatabaseHelper", "Columns in cursor: " + Arrays.toString(columnNames));

        if (cursor.moveToFirst()) {
            do {
                try {
                    int idIndex = cursor.getColumnIndexOrThrow(SCOLUMN_ID);
                    int nameIndex = cursor.getColumnIndexOrThrow(SCOLUMN_NAME);
                    int amountIndex = cursor.getColumnIndexOrThrow(SCOLUMN_AMOUNT);
                    int dateIndex = cursor.getColumnIndexOrThrow(SCOLUMN_DATE);

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String date = cursor.getString(dateIndex);
                    savingsList.add(new RecyclerModel(id, name, amount, date));
                } catch (IllegalArgumentException e) {
                    Log.e("DatabaseHelper", "Column not found: " + e.getMessage());
                }
            } while (cursor.moveToNext());
        } else {
            Log.w("DatabaseHelper", "No savings found.");
        }
        cursor.close();
        db.close();
        return savingsList;
    }

    // Method to get total savings
    public double getTotalSavings() {
        double total = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + SCOLUMN_AMOUNT + ") AS total FROM " + TABLE_SAVINGS, null);

        // Log the column names to debug
        String[] columnNames = cursor.getColumnNames();
        Log.d("DatabaseHelper", "Columns in cursor: " + Arrays.toString(columnNames));

        if (cursor.moveToFirst()) {
            try {
                // Use getColumnIndexOrThrow to safely get the column index
                int totalIndex = cursor.getColumnIndexOrThrow("total");
                total = cursor.getDouble(totalIndex);
            } catch (IllegalArgumentException e) {
                Log.e("DatabaseHelper", "Column 'total' not found: " + e.getMessage());
            }
        } else {
            Log.w("DatabaseHelper", "No savings found in the database.");
        }

        cursor.close();
        db.close();
        return total;
    }

    // Method to insert savings
    public boolean insertSavings(String name, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCOLUMN_NAME, name);
        values.put(SCOLUMN_AMOUNT, amount);
        values.put(SCOLUMN_DATE, date);

        long result = db.insert(TABLE_SAVINGS, null, values);
        db.close(); // Close database to free resources
        return result != -1; // Return true if insertion was successful
    }

    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UCOLUMN_USERNAME, username);
        values.put(UCOLUMN_PIN, pin); // Consider hashing the PIN for security

        long result = db.insert(TABLE_USERS, null, values);
        db.close(); // Close database to free resources
        return result != -1; // Return true if insertion was successful
    }

    public boolean insertExpense(String name, double amount, String description, String date, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ECOLUMN_NAME, name);
        values.put(ECOLUMN_DESCRIPTION, description);
        values.put(ECOLUMN_AMOUNT, amount);
        values.put(ECOLUMN_DATE, date);
        values.put(ECOLUMN_USER_ID, userId); // Include UserID

        long result = db.insert(TABLE_EXPENSE, null, values);
        db.close(); // Close database to free resources
        return result != -1; // Return true if insertion was successful
    }

    public List<ExpenseModel> getUserExpenses(int userId) {
        List<ExpenseModel> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSE,
                null,
                ECOLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        // Log the column names
        String[] columnNames = cursor.getColumnNames();
        Log.d("DatabaseHelper", "Columns in cursor: " + Arrays.toString(columnNames));

        if (cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ECOLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ECOLUMN_NAME));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(ECOLUMN_AMOUNT));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(ECOLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(ECOLUMN_DATE));
                    expenseList.add(new ExpenseModel(id, name, amount, description, date));
                } catch (IllegalArgumentException e) {
                    Log.e("DatabaseHelper", "Column not found: " + e.getMessage());
                }
            } while (cursor.moveToNext());
        } else {
            Log.w("DatabaseHelper", "No expenses found for user ID: " + userId);
        }
        cursor.close();
        db.close();
        return expenseList;
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
}

//this comment is used for me to push this version. Kindly dismiss this