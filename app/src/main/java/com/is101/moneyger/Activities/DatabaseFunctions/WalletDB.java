package com.is101.moneyger.Activities.DatabaseFunctions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.is101.moneyger.Activities.model.RecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class WalletDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Walletfunction.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "orders";  // Table name is "orders"
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_AMOUNT = "amount";

    public WalletDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table query
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_AMOUNT + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to add an expense
    public void addExpense(String title, String date, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Read all data from the orders table
    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // Method to fetch all transactions
    public List<RecyclerModel> getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RecyclerModel> transactions = new ArrayList<>();

        // Query to fetch all transactions from "orders" table
        String query = "SELECT * FROM " + TABLE_NAME;  // Table name is "orders", not "transactions"
        Cursor cursor = db.rawQuery(query, null);

        // Check if cursor contains data
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Validate column indices
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);

                // Ensure the column indices are valid
                if (titleIndex != -1 && dateIndex != -1 && amountIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    String date = cursor.getString(dateIndex);
                    int amount = cursor.getInt(amountIndex);

                    // Add to the list of transactions
                    transactions.add(new RecyclerModel(title, date, amount));
                } else {
                    // Log or handle the error if columns are missing
                    Toast.makeText(context, "Invalid column indices", Toast.LENGTH_SHORT).show();
                }
            } while (cursor.moveToNext());
            cursor.close();  // Don't forget to close the cursor
        }

        db.close();
        return transactions;
    }
}

