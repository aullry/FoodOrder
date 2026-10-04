package com.example.foodorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME =
            "foodorder.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MENU =
            "menu";

    private static final String TABLE_TRANSACTIONS =
            "transactions";

    private static final String TABLE_ITEMS =
            "transaction_items";

    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createMenuTable =
                "CREATE TABLE " + TABLE_MENU + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "price INTEGER NOT NULL, " +
                        "category TEXT NOT NULL)";

        String createTransactionTable =
                "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "total INTEGER NOT NULL, " +
                        "note TEXT, " +
                        "date_time TEXT NOT NULL)";

        String createItemsTable =
                "CREATE TABLE " + TABLE_ITEMS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "transaction_id INTEGER NOT NULL, " +
                        "menu_id INTEGER NOT NULL, " +
                        "menu_name TEXT NOT NULL, " +
                        "price INTEGER NOT NULL, " +
                        "quantity INTEGER NOT NULL, " +
                        "subtotal INTEGER NOT NULL)";

        db.execSQL(createMenuTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createItemsTable);

        insertDefaultMenus(db);
    }

    private void insertDefaultMenus(SQLiteDatabase db) {

        insertMenu(
                db,
                "Burger",
                15000,
                "Makanan"
        );

        insertMenu(
                db,
                "Mie Ayam",
                12000,
                "Makanan"
        );

        insertMenu(
                db,
                "Nasi Goreng",
                15000,
                "Makanan"
        );

        insertMenu(
                db,
                "Kentang Goreng",
                10000,
                "Snack"
        );

        insertMenu(
                db,
                "Es Teh",
                5000,
                "Minuman"
        );

        insertMenu(
                db,
                "Es Jeruk",
                7000,
                "Minuman"
        );
    }

    private void insertMenu(
            SQLiteDatabase db,
            String name,
            int price,
            String category
    ) {

        ContentValues values =
                new ContentValues();

        values.put(
                "name",
                name
        );

        values.put(
                "price",
                price
        );

        values.put(
                "category",
                category
        );

        db.insert(
                TABLE_MENU,
                null,
                values
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_ITEMS
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_TRANSACTIONS
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_MENU
        );

        onCreate(db);
    }

    public ArrayList<Menu> getAllMenus() {

        ArrayList<Menu> menuList =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_MENU +
                                " ORDER BY id ASC",
                        null
                );

        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                int price =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                String category =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "category"
                                )
                        );

                menuList.add(
                        new Menu(
                                id,
                                name,
                                price,
                                category
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return menuList;
    }

    public Menu getMenuById(
            int menuId
    ) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_MENU +
                                " WHERE id = ?",
                        new String[]{
                                String.valueOf(menuId)
                        }
                );

        Menu menu = null;

        if (cursor.moveToFirst()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            String name =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "name"
                            )
                    );

            int price =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "price"
                            )
                    );

            String category =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "category"
                            )
                    );

            menu = new Menu(
                    id,
                    name,
                    price,
                    category
            );
        }

        cursor.close();

        return menu;
    }

    public long insertTransaction(
            int total,
            String note,
            String dateTime,
            HashMap<Integer, Integer> cart
    ) {

        SQLiteDatabase db =
                getWritableDatabase();

        long transactionId = -1;

        db.beginTransaction();

        try {

            ContentValues transactionValues =
                    new ContentValues();

            transactionValues.put(
                    "total",
                    total
            );

            transactionValues.put(
                    "note",
                    note
            );

            transactionValues.put(
                    "date_time",
                    dateTime
            );

            transactionId =
                    db.insert(
                            TABLE_TRANSACTIONS,
                            null,
                            transactionValues
                    );

            if (transactionId == -1) {

                throw new Exception(
                        "Transaction failed"
                );
            }

            for (Integer menuId :
                    cart.keySet()) {

                int quantity =
                        cart.get(menuId);

                Menu menu =
                        getMenuById(
                                menuId
                        );

                if (menu == null) {
                    continue;
                }

                int subtotal =
                        menu.getPrice() *
                                quantity;

                ContentValues itemValues =
                        new ContentValues();

                itemValues.put(
                        "transaction_id",
                        transactionId
                );

                itemValues.put(
                        "menu_id",
                        menu.getId()
                );

                itemValues.put(
                        "menu_name",
                        menu.getName()
                );

                itemValues.put(
                        "price",
                        menu.getPrice()
                );

                itemValues.put(
                        "quantity",
                        quantity
                );

                itemValues.put(
                        "subtotal",
                        subtotal
                );

                db.insert(
                        TABLE_ITEMS,
                        null,
                        itemValues
                );
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            transactionId = -1;

        } finally {

            db.endTransaction();
        }

        return transactionId;
    }

    public Cursor getTransactions() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_TRANSACTIONS +
                        " ORDER BY id DESC",
                null
        );
    }

    public Cursor getTransactionItems(
            int transactionId
    ) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_ITEMS +
                        " WHERE transaction_id = ?",
                new String[]{
                        String.valueOf(
                                transactionId
                        )
                }
        );
    }

    public int getTransactionCount() {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM " +
                                TABLE_TRANSACTIONS,
                        null
                );

        int count = 0;

        if (cursor.moveToFirst()) {

            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }
}