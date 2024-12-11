package com.is101.moneyger.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.is101.moneyger.Activities.model.SavingModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneyger.db";
    private static final int DATABASE_VERSION = 15;

    private static DatabaseHelper instance;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SAVINGS = "savings";
    private static final String TABLE_GOALS = "goals";
    private static final String TABLE_MONTHLY_GOALS = "monthly_goals"; // New table for monthly goals
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_TRANSACTION_TYPES = "transaction_types";
    private static final String TABLE_WALLET = "wallet";
    private static final String TABLE_PROFITS = "profits";
    private static final String TABLE_EXPENSES = "expenses";

    // Column names
    private static final String COLUMN_USER_ID = "U_id";
    private static final String COLUMN_SAVING_ID = "S_id";
    private static final String COLUMN_GOAL_ID = "goal_id";
    private static final String COLUMN_WALLET_ID = "Wallet_ID";
    private static final String COLUMN_SAVING_NAME = "S_name";
    private static final String COLUMN_SAVING_AMOUNT = "S_amount";
    private static final String COLUMN_SAVING_START_DATE = "S_start_date";
    private static final String COLUMN_SAVING_END_DATE = "S_end_date";
    private static final String COLUMN_GOAL_AMOUNT = "M_Goal";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_TYPE_NAME = "type_name";

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
        db.execSQL(createGoalsTable());
        db.execSQL(createMonthlyGoalsTable()); // Create monthly goals table
        db.execSQL(createTransactionTypesTable());
        db.execSQL(createTransactionsTable());
        db.execSQL(createWalletTable());
        db.execSQL(createProfitsTable());
        db.execSQL(createExpensesTable());
        insertTransactionTypes(db);
    }

    private String createUsersTable() {
        return "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "U_username TEXT NOT NULL, " +
                "U_pin TEXT NOT NULL)";
    }

    private String createSavingsTable() {
        return "CREATE TABLE " + TABLE_SAVINGS + " (" +
                COLUMN_SAVING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WALLET_ID + " INTEGER NOT NULL, " +
                COLUMN_SAVING_NAME + " TEXT NOT NULL, " +
                "Description TEXT, " +
                COLUMN_SAVING_AMOUNT + " REAL NOT NULL, " +
                COLUMN_SAVING_START_DATE + " TEXT NOT NULL, " +
                COLUMN_SAVING_END_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_WALLET_ID + ") REFERENCES " + TABLE_WALLET + "(wallet_id))";
    }

    private String createGoalsTable() {
        return "CREATE TABLE " + TABLE_GOALS + " (" +
                COLUMN_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SAVING_ID + " INTEGER NOT NULL, " +
                COLUMN_GOAL_AMOUNT + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_SAVING_ID + ") REFERENCES " + TABLE_SAVINGS + "(" + COLUMN_SAVING_ID + "))";
    }

    // New method to create the monthly goals table
    private String createMonthlyGoalsTable() {
        return "CREATE TABLE " + TABLE_MONTHLY_GOALS + " (" +
                "goal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "goal_amount REAL NOT NULL, " +
                "user_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
    }

    private String createTransactionTypesTable() {
        return "CREATE TABLE " + TABLE_TRANSACTION_TYPES + " (" +
                "type_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE_NAME + " TEXT NOT NULL)";
    }

    private String createTransactionsTable() {
        return "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE_NAME + " TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "description TEXT, " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_TYPE_NAME + ") REFERENCES " + TABLE_TRANSACTION_TYPES + "(" + COLUMN_TYPE_NAME + "))";
    }

    private String createWalletTable() {
        return "CREATE TABLE " + TABLE_WALLET + " (" +
                "wallet_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "wallet_balance REAL NOT NULL, " +
                "user_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
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
                "FOREIGN KEY(transaction_id) REFERENCES " + TABLE_TRANSACTIONS + "(transaction_id))";
    }

    private void insertTransactionTypes(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_NAME, "Income");
        db.insert(TABLE_TRANSACTION_TYPES, null, values);

        values.put(COLUMN_TYPE_NAME, "Expense");
        db.insert(TABLE_TRANSACTION_TYPES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY_GOALS); // Drop monthly goals table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Monthly Goals Methods

    public boolean insertMonthlyGoal(double goalAmount, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal_amount", goalAmount);
        values.put("user_id", userId);
        long result = db.insert(TABLE_MONTHLY_GOALS, null, values);
        db.close();
        return result != -1;
    }

    public double getMonthlyGoal(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double goalAmount = 0.0; // Default value

        try {
            cursor = db.query(TABLE_MONTHLY_GOALS,
                    new String[]{"goal_amount"},
                    "user_id = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int goalAmountIndex = cursor.getColumnIndex("goal_amount");
                if (goalAmountIndex != -1) {
                    goalAmount = cursor.getDouble(goalAmountIndex); // Get the double value
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving monthly goal: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed to avoid memory leaks
            }
            db.close();
        }

        return goalAmount; // Return 0.0 if no goal is found
    }

    public boolean updateMonthlyGoal(int userId, double newGoalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal_amount", newGoalAmount);
        int rowsAffected = db.update(TABLE_MONTHLY_GOALS, values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0; // Return true if the update was successful
    }

    // Other existing methods...

    // Check if the user exists
    public boolean checkUser(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE U_username = ? AND U_pin = ?", new String[]{username, pin})) {
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
            return false;
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
        return result != -1;
    }

    // Retrieve user details by user ID
    public Object getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE U_id = ?", new String[]{String.valueOf(userId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndex("U_username");
                int pinIndex = cursor.getColumnIndex("U_pin");

                if (idIndex != -1 && usernameIndex != -1 && pinIndex != -1) {
                    // Assuming you will create a user model
                    // Replace with actual user structure
                    return new Object();
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // Check if a user exists by username
    public boolean isUserExists(String username, String pin) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE U_username = ? AND U_pin = ?", new String[]{username, pin})) {
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    // Insert a new savings entry
    public boolean insertSaving(SavingModel saving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVING_NAME, saving.getName());
        values.put(COLUMN_SAVING_AMOUNT, saving.getAmount());
        values.put(COLUMN_SAVING_START_DATE, saving.getStartDate());
        values.put(COLUMN_SAVING_END_DATE, saving.getEndDate());
        values.put(COLUMN_WALLET_ID, saving.getUserId());
        long result = db.insert(TABLE_SAVINGS, null, values);
        db.close();
        return result != -1;
    }

    // Insert a new goal entry
    public boolean insertGoal(int savingId, double goalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVING_ID, savingId);
        values.put(COLUMN_GOAL_AMOUNT, goalAmount);
        long result = db.insert(TABLE_GOALS, null, values);
        db.close();
        return result != -1;
    }

    // Retrieve current amount for a specific savings entry by its ID
    public double getCurrentAmount(int savingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT S_amount FROM " + TABLE_SAVINGS + " WHERE S_id = ?", new String[]{String.valueOf(savingId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                int amountIndex = cursor.getColumnIndex(COLUMN_SAVING_AMOUNT);
                if (amountIndex != -1) {
                    return cursor.getDouble(amountIndex);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting current amount: " + e.getMessage());
        }
        return 0;
    }

    // Retrieve total savings by user ID
    public double getTotalSavings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT SUM(S_amount) AS total FROM " + TABLE_SAVINGS + " WHERE Wallet_ID = ?", new String[]{String.valueOf(userId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                int totalIndex = cursor.getColumnIndex("total");
                if (totalIndex != -1) {
                    return cursor.getDouble(totalIndex);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting total savings: " + e.getMessage());
        }
        return 0;
    }

    // Retrieve all savings for a user
    public List<SavingModel> getSavingsByUserId(int userId) {
        List<SavingModel> savingsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_SAVINGS + " WHERE Wallet_ID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            // Log available column names
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                Log.d("DatabaseHelper", "Columns returned: " + Arrays.toString(columnNames));
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int savingIdIndex = cursor.getColumnIndex(COLUMN_SAVING_ID);
                    int savingNameIndex = cursor.getColumnIndex(COLUMN_SAVING_NAME);
                    int savingAmountIndex = cursor.getColumnIndex(COLUMN_SAVING_AMOUNT);
                    int savingStartDateIndex = cursor.getColumnIndex(COLUMN_SAVING_START_DATE);
                    int savingEndDateIndex = cursor.getColumnIndex(COLUMN_SAVING_END_DATE);

                    // Check if indices are valid
                    if (savingIdIndex != -1 && savingNameIndex != -1 && savingAmountIndex != -1 &&
                            savingStartDateIndex != -1 && savingEndDateIndex != -1) {

                        SavingModel saving = new SavingModel(
                                cursor.getInt(savingIdIndex),
                                cursor.getString(savingNameIndex),
                                cursor.getDouble(savingAmountIndex),
                                cursor.getString(savingStartDateIndex),
                                cursor.getString(savingEndDateIndex),
                                userId
                        );

                        savingsList.add(saving);
                    } else {
                        Log.e("DatabaseHelper", "One or more column indices are invalid.");
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

        return savingsList;
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
        return result != -1;
    }

    // Retrieve all transactions
    public List<Map<String, String>> getAllTransactions() {
        List<Map<String, String>> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Log available column names
                String[] columnNames = cursor.getColumnNames();
                Log.d("DatabaseHelper", "Columns returned: " + Arrays.toString(columnNames));

                do {
                    Map<String, String> transaction = new HashMap<>();
                    // Check each column index
                    int transactionIdIndex = cursor.getColumnIndex("transaction_id");
                    int typeIdIndex = cursor.getColumnIndex("type_id");
                    int amountIndex = cursor.getColumnIndex("amount");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int transactionDateIndex = cursor.getColumnIndex("transaction_date");

                    // Ensure all indices are valid
                    if (transactionIdIndex != -1 && typeIdIndex != -1 && amountIndex != -1 &&
                            descriptionIndex != -1 && transactionDateIndex != -1) {

                        transaction.put("transaction_id", cursor.getString(transactionIdIndex));
                        transaction.put("type_id", cursor.getString(typeIdIndex));
                        transaction.put("amount", cursor.getString(amountIndex));
                        transaction.put("description", cursor.getString(descriptionIndex));
                        transaction.put("transaction_date", cursor.getString(transactionDateIndex));

                        transactionList.add(transaction);
                    } else {
                        Log.e("DatabaseHelper", "One or more column indices are invalid.");
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving transactions: " + e.getMessage());
        }
        return transactionList;
    }

    // Update a savings entry
    // Update a savings entry
    public boolean updateSaving(SavingModel saving) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVING_NAME, saving.getName());
        values.put(COLUMN_SAVING_AMOUNT, saving.getAmount());
        values.put(COLUMN_SAVING_START_DATE, saving.getStartDate());
        values.put(COLUMN_SAVING_END_DATE, saving.getEndDate());

        int rowsAffected = db.update(TABLE_SAVINGS, values, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(saving.getId())});
        db.close();
        return rowsAffected > 0; // Return true if the update was successful
    }

    // Delete a savings entry
    public boolean deleteSaving(int savingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SAVINGS, COLUMN_SAVING_ID + " = ?", new String[]{String.valueOf(savingId)});
        db.close();
        return rowsAffected > 0; // Return true if the delete was successful
    }

    // Delete a monthly goal
    public boolean deleteMonthlyGoal(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_MONTHLY_GOALS, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0; // Return true if the delete was successful
    }

    // Get all transaction types
    public List<String> getAllTransactionTypes() {
        List<String> transactionTypes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT " + COLUMN_TYPE_NAME + " FROM " + TABLE_TRANSACTION_TYPES, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int typeNameIndex = cursor.getColumnIndex(COLUMN_TYPE_NAME);
                    if (typeNameIndex != -1) {
                        transactionTypes.add(cursor.getString(typeNameIndex));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving transaction types: " + e.getMessage());
        }
        return transactionTypes;
    }

    // Close the database
    @Override
    public void close() {
        super.close();
    }
}