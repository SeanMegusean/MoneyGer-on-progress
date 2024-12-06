package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.is101.moneyger.Activities.model.SavingModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 11;

    private static DatabaseHelper instance;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SAVINGS = "savings";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_TRANSACTION_TYPES = "transaction_types";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_PROFITS = "profits";
    private static final String TABLE_EXPENSES = "expenses";

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
        db.execSQL(createUsersTable());
        db.execSQL(createSavingsTable());
        db.execSQL(createTransactionTypesTable());
        db.execSQL(createTransactionsTable());
        db.execSQL(createWalletTable());
        db.execSQL(createProfitsTable());
        db.execSQL(createExpensesTable());
        insertTransactionTypes(db); // Insert initial transaction types
    }

    private String createUsersTable() {
        return "CREATE TABLE " + TABLE_USERS + " (" +
                "U_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "U_username TEXT NOT NULL, " +
                "U_pin TEXT NOT NULL)";
    }

    private String createSavingsTable() {
        return "CREATE TABLE " + TABLE_SAVINGS + " (" +
                "S_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Wallet_ID INTEGER NOT NULL, " +
                "S_name TEXT NOT NULL, " +
                "Description TEXT, " +
                "M_Goal REAL NOT NULL, " +
                "S_amount REAL NOT NULL, " +
                "S_start_date TEXT NOT NULL, " +
                "S_end_date TEXT, " +
                "FOREIGN KEY(Wallet_ID) REFERENCES " + TABLE_WALLET + "(wallet_id))";
    }

    private String createTransactionTypesTable() {
        return "CREATE TABLE " + TABLE_TRANSACTION_TYPES + " (" +
                "type_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type_name TEXT NOT NULL)";
    }

    private String createTransactionsTable() {
        return "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type_id INTEGER, " +
                "FOREIGN KEY(type_id) REFERENCES " + TABLE_TRANSACTION_TYPES + "(type_id))";
    }

    private String createWalletTable() {
        return "CREATE TABLE " + TABLE_WALLET + " (" +
                "wallet_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "wallet_balance REAL NOT NULL, " +
                "user_id INTEGER, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(U_id))";
    }

    private String createProfitsTable() {
        return "CREATE TABLE " + TABLE_PROFITS + " (" +
                "profit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "transaction_id INTEGER, " +
                "profit_name TEXT NOT NULL, " +
                "profit_description TEXT, " +
                "profit_category TEXT NOT NULL, " +
                "profit_amount REAL NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TEXT, " +
                "FOREIGN KEY(transaction_id) REFERENCES " + TABLE_TRANSACTIONS + "(transaction_id))";
    }

    private String createExpensesTable() {
        return "CREATE TABLE " + TABLE_EXPENSES + " (" +
                "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "transaction_id INTEGER, " +
                "expense_name TEXT NOT NULL, " +
                "expense_description TEXT, " +
                "expense_category TEXT NOT NULL, " +
                "expense_amount REAL NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TEXT, " +
                "FOREIGN KEY(transaction_id) REFERENCES " + TABLE_TRANSACTIONS + "(transaction_id))";
    }

    // Insert initial transaction types
    private void insertTransactionTypes(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("type_name", "Income");
        db.insert(TABLE_TRANSACTION_TYPES, null, values);

        values.put("type_name", "Expense");
        db.insert(TABLE_TRANSACTION_TYPES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    //Every code and stuff for users. Add nalang kayo dito ng iba

    public boolean checkUser(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE U_username = ? AND U_pin = ?";
            cursor = db.rawQuery(query, new String[]{username, pin});

            return cursor != null && cursor.moveToFirst(); // Returns true if user exists
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
            return false; // In case of error, assume user does not exist
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // Check if the user exists
    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE U_username = ?";
            cursor = db.rawQuery(query, new String[]{username});

            return cursor != null && cursor.getCount() > 0; // Returns true if user exists
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if user exists: " + e.getMessage());
            return false; // In case of error, assume user does not exist
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Insert a new user
    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("U_username", username);
            values.put("U_pin", pin);
            long result = db.insert(TABLE_USERS, null, values);
            return result != -1; // Return true if insertion is successful
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
            return false; // Return false if insertion fails
        } finally {
            db.close(); // Close the database connection
        }
    }


    // Insert a new savings entry
    // Insert a new savings entry.
    public boolean insertSaving(SavingModel saving) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("S_name", saving.getName());
            values.put("S_amount", saving.getAmount());
            values.put("S_start_date", saving.getStartDate());
            values.put("S_end_date", saving.getEndDate());
            values.put("UserID", saving.getUserId());
            long result = db.insert(TABLE_SAVINGS, null, values);
            return result != -1; // Return true if insertion is successful
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting savings: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Get total savings for a user
    public double getTotalSavings(int userId) {
        double totalSavings = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT SUM(S_amount) AS total FROM " + TABLE_SAVINGS + " WHERE UserID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int totalIndex = cursor.getColumnIndex("total");
                if (totalIndex != -1) { // Check if the column exists
                    totalSavings = cursor.getDouble(totalIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'total' not found in query result");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving total savings: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return totalSavings;
    }

    public double getCurrentAmount(int userId) {
        double currentAmount = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT SUM(S_amount) AS currentAmount FROM " + TABLE_SAVINGS + " WHERE UserID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int currentAmountIndex = cursor.getColumnIndex("currentAmount");
                if (currentAmountIndex != -1) { // Check if the column exists
                    currentAmount = cursor.getDouble(currentAmountIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'currentAmount' not found in query result");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving current amount: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return currentAmount;
    }

    // Get all savings for a user
    public List<SavingModel> getSavingsByUserId(int userId) {
        List<SavingModel> savingModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_SAVINGS, null, "UserID=?",
                    new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int savingIdIndex = cursor.getColumnIndex("S_id");
                int nameIndex = cursor.getColumnIndex("S_name");
                int amountIndex = cursor.getColumnIndex("S_amount");
                int startDateIndex = cursor.getColumnIndex("S_start_date");
                int endDateIndex = cursor.getColumnIndex("S_end_date");

                // Check if any of the indices are -1
                if (savingIdIndex == -1 || nameIndex == -1 || amountIndex == -1 ||
                        startDateIndex == -1 || endDateIndex == -1) {
                    Log.e("DatabaseHelper", "One or more columns are missing in the result set");
                    return savingModels; // Exit early if columns are missing
                }

                do {
                    int savingId = cursor.getInt(savingIdIndex);
                    String name = cursor.getString(nameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String startDate = cursor.getString(startDateIndex);
                    String endDate = cursor.getString(endDateIndex);

                    savingModels.add(new SavingModel(savingId, name, amount, startDate, endDate, String.valueOf(userId)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving savings: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return savingModels;
    }

    // Get all savings grouped by user
    public Map<Integer, List<SavingModel>> getAllSavingsGroupedByUser() {
        Map<Integer, List<SavingModel>> savingsMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_SAVINGS, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int userIdIndex = cursor.getColumnIndex("UserID");
                int savingIdIndex = cursor.getColumnIndex("S_id");
                int nameIndex = cursor.getColumnIndex("S_name");
                int amountIndex = cursor.getColumnIndex("S_amount");
                int startDateIndex = cursor.getColumnIndex("S_start_date");
                int endDateIndex = cursor.getColumnIndex("S_end_date");

                // Check if any of the indices are -1
                if (userIdIndex == -1 || savingIdIndex == -1 || nameIndex == -1 ||
                        amountIndex == -1 || startDateIndex == -1 || endDateIndex == -1) {
                    Log.e("DatabaseHelper", "One or more columns are missing in the result set");
                    return savingsMap; // Exit early if columns are missing
                }

                do {
                    int userId = cursor.getInt(userIdIndex);
                    int savingId = cursor.getInt(savingIdIndex);
                    String name = cursor.getString(nameIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String startDate = cursor.getString(startDateIndex);
                    String endDate = cursor.getString(endDateIndex);

                    SavingModel saving = new SavingModel(savingId, name, amount, startDate, endDate, String.valueOf(userId));
                    savingsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(saving);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving savings: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return savingsMap;
    }

}