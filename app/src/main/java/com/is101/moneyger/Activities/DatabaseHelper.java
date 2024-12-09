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
    private static final int DATABASE_VERSION = 12; // Increment version for schema change

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
                "S_amount REAL NOT NULL, " +
                "S_start_date TEXT NOT NULL, " +
                "S_end_date TEXT, " +
                "M_Goal REAL, " + // Allow NULL values for M_Goal
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
                "amount REAL NOT NULL, " +
                "description TEXT, " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
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

    // Check if the user exists
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

    // Insert a new user
    public boolean insertUser(String username, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("U_username", username);
        values.put("U_pin", pin);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1; // Return true if insertion is successful
    }

    public boolean isUserExists(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE U_username = ? AND U_pin = ?";
            cursor = db.rawQuery(query, new String[]{username, pin});
            exists = cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return exists;
    }


    // Retrieve user details by user ID
    public Object getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Object user = null; // Assuming a User model class exists

        try {
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE U_id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("U_id");
                int usernameIndex = cursor.getColumnIndex("U_username");
                int pinIndex = cursor.getColumnIndex("U_pin");

                if (idIndex != -1 && usernameIndex != -1 && pinIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String username = cursor.getString(usernameIndex);
                    String pin = cursor.getString(pinIndex);

                    user = new Object(); // Replace with actual user structure if needed
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return user; // Return the user object or null if not found
    }

    // Insert a new savings entry
    public boolean insertSaving(SavingModel saving) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("S_name", saving.getName());
            values.put("S_amount", saving.getAmount());
            values.put("S_start_date", saving.getStartDate());
            values.put("S_end_date", saving.getEndDate());
            values.put("Wallet_ID", saving.getUserId()); // Ensure correct column name is used

            // If M_Goal is set, include it; otherwise, it will be NULL
            if (saving.getGoal() != null) {
                values.put("M_Goal", saving.getGoal());
            }

            long result = db.insert(TABLE_SAVINGS, null, values);
            return result != -1; // Return true if insertion is successful
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting savings: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Retrieve the current amount for a specific savings entry by its ID
    public double getCurrentAmount(int savingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double currentAmount = 0;

        try {
            String query = "SELECT S_amount FROM " + TABLE_SAVINGS + " WHERE S_id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(savingId)});

            if (cursor != null && cursor.moveToFirst()) {
                int amountIndex = cursor.getColumnIndex("S_amount");
                if (amountIndex != -1) {
                    currentAmount = cursor.getDouble(amountIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'S_amount' not found in the result set.");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting current amount: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return currentAmount; // Return the current amount
    }

    // Retrieve total savings by user ID
    public double getTotalSavings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double totalSavings = 0;

        try {
            String query = "SELECT SUM(S_amount) AS total FROM " + TABLE_SAVINGS + " WHERE Wallet_ID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                int totalIndex = cursor.getColumnIndex("total"); // Get the index of the total column
                if (totalIndex != -1) { // Check if the index is valid
                    totalSavings = cursor.getDouble(totalIndex); // Retrieve the total savings
                } else {
                    Log.e("DatabaseHelper", "Column 'total' not found in the result set.");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting total savings: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return totalSavings; // Return total savings
    }

    // Retrieve all savings for a user
    public List<SavingModel> getSavingsByUserId(int userId) {
        List<SavingModel> savingsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_SAVINGS + " WHERE Wallet_ID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex("S_id");
                    int nameIndex = cursor.getColumnIndex("S_name");
                    int amountIndex = cursor.getColumnIndex("S_amount");
                    int startDateIndex = cursor.getColumnIndex("S_start_date");
                    int endDateIndex = cursor.getColumnIndex("S_end_date");
                    int goalIndex = cursor.getColumnIndex("M_Goal");

                    // Check if indices are valid before accessing
                    if (idIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex != -1 ? nameIndex : 0);
                        double amount = cursor.getDouble(amountIndex != -1 ? amountIndex : 0);
                        String startDate = cursor.getString(startDateIndex != -1 ? startDateIndex : 0);
                        String endDate = cursor.getString(endDateIndex != -1 ? endDateIndex : null);
                        Double goal = cursor.isNull(goalIndex) ? null : cursor.getDouble(goalIndex);

                        SavingModel saving = new SavingModel(id, name, amount, startDate, endDate, userId, goal);
                        savingsList.add(saving);
                    }
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

        return savingsList; // Return the list of savings
    }

    // Insert a new transaction
    public boolean insertTransaction(int typeId, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type_id", typeId);
        values.put("amount", amount);
        values.put("description", description);
        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result != -1; // Return true if insertion is successful
    }

    // Retrieve all transactions
    public List<Map<String, String>> getAllTransactions() {
        List<Map<String, String>> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_TRANSACTIONS;
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Map<String, String> transaction = new HashMap<>();
                    int transactionIdIndex = cursor.getColumnIndex("transaction_id");
                    int typeIdIndex = cursor.getColumnIndex("type_id");
                    int amountIndex = cursor.getColumnIndex("amount");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int transactionDateIndex = cursor.getColumnIndex("transaction_date");

                    if (transactionIdIndex != -1) {
                        transaction.put("transaction_id", cursor.getString(transactionIdIndex));
                    }
                    if (typeIdIndex != -1) {
                        transaction.put("type_id", cursor.getString(typeIdIndex));
                    }
                    if (amountIndex != -1) {
                        transaction.put("amount", cursor.getString(amountIndex));
                    }
                    if (descriptionIndex != -1) {
                        transaction.put("description", cursor.getString(descriptionIndex));
                    }
                    if (transactionDateIndex != -1) {
                        transaction.put("transaction_date", cursor.getString(transactionDateIndex));
                    }

                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving transactions: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return transactionList; // Return the list of transactions
    }

    // Update a savings entry
    public boolean updateSaving(SavingModel saving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("S_name", saving.getName());
        values.put("S_amount", saving.getAmount());
        values.put("S_start_date", saving.getStartDate());
        values.put("S_end_date", saving.getEndDate());

        if (saving.getGoal() != null) {
            values.put("M_Goal", saving.getGoal());
        } else {
            values.putNull("M_Goal");
        }

        int rowsAffected = db.update(TABLE_SAVINGS, values, "S_id = ?", new String[]{String.valueOf(saving.getId())});
        db.close();
        return rowsAffected > 0; // Return true if update was successful
    }

    // Delete a savings entry
    public boolean deleteSaving(int savingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SAVINGS, "S_id = ?", new String[]{String.valueOf(savingId)});
        db.close();
        return rowsAffected > 0; // Return true if deletion was successful
    }

    // Set a monthly savings goal
    public boolean setMonthlyGoal(double goalAmount) {
        // Assuming there's a way to store or relate this monthly goal to a user or savings
        // This could be implemented based on your application's requirements
        // For example, you could have a separate table for monthly goals
        return true; // Replace with actual implementation
    }

    // Other methods for managing profits and expenses can be added similarly
}