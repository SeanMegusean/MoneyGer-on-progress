package com.is101.moneyger.Activities.DatabaseFunctions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.is101.moneyger.Activities.model.RecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class WalletDB extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Walletfunction.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESC = "description";

    public WalletDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_AMOUNT + " INTEGER, " +
                COLUMN_DESC + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Add expense
    public void addExpense(String title, String date, int amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_DESC, description);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public List<RecyclerModel> getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RecyclerModel> transactions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                int amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESC);

                if (idIndex != -1 && titleIndex != -1 && dateIndex != -1 && amountIndex != -1 && descriptionIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String date = cursor.getString(dateIndex);
                    int amount = cursor.getInt(amountIndex);
                    String description = cursor.getString(descriptionIndex);

                    transactions.add(new RecyclerModel(id,title, date, amount, description));
                } else {
                    Log.e("WalletDB", "One or more columns are missing from the cursor");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return transactions;
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}

