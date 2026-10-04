package com.example.foodorder;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME =
            "foodorder_profile";

    private static final int PURPLE =
            Color.rgb(105, 73, 255);

    private static final int PURPLE_LIGHT =
            Color.rgb(242, 238, 255);

    private static final int TEXT_DARK =
            Color.rgb(42, 35, 61);

    private static final int TEXT_GRAY =
            Color.rgb(139, 132, 153);

    private static final int PINK =
            Color.rgb(222, 86, 181);

    private DatabaseHelper databaseHelper;

    private SharedPreferences preferences;

    private LinearLayout contentContainer;

    private TextView tvGreeting;
    private TextView tvStore;
    private TextView tvTotal;

    private TextView navMenu;
    private TextView navHistory;
    private TextView navProfile;

    private Button btnOrder;

    private ArrayList<Menu> menuList;

    private HashMap<Integer, Integer> cart =
            new HashMap<>();

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_main
        );

        databaseHelper =
                new DatabaseHelper(
                        this
                );

        preferences =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        contentContainer =
                findViewById(
                        R.id.contentContainer
                );

        tvGreeting =
                findViewById(
                        R.id.tvGreeting
                );

        tvStore =
                findViewById(
                        R.id.tvStore
                );

        tvTotal =
                findViewById(
                        R.id.tvTotal
                );

        btnOrder =
                findViewById(
                        R.id.btnOrder
                );

        navMenu =
                findViewById(
                        R.id.navMenu
                );

        navHistory =
                findViewById(
                        R.id.navHistory
                );

        navProfile =
                findViewById(
                        R.id.navProfile
                );

        loadProfile();

        navMenu.setOnClickListener(
                v -> showMenuPage()
        );

        navHistory.setOnClickListener(
                v -> showHistoryPage()
        );

        navProfile.setOnClickListener(
                v -> showProfilePage()
        );

        btnOrder.setOnClickListener(
                v -> checkout()
        );

        showMenuPage();

        updateTotal();
    }

    // =========================================================
    // PROFILE HEADER
    // =========================================================

    private void loadProfile() {

        String name =
                preferences.getString(
                        "name",
                        "Kasir"
                );

        String store =
                preferences.getString(
                        "store",
                        "FoodOrder Store"
                );

        tvGreeting.setText(
                "Halo, " +
                        name +
                        " 👋"
        );

        tvStore.setText(
                store +
                        " • Kasir"
        );
    }

    // =========================================================
    // MENU PAGE
    // =========================================================

    private void showMenuPage() {

        contentContainer.removeAllViews();

        activateNavigation(
                navMenu
        );

        TextView title =
                createText(
                        "Mau pesan apa hari ini? 🍽️",
                        24,
                        TEXT_DARK,
                        true
                );

        contentContainer.addView(
                title
        );

        TextView subtitle =
                createText(
                        "Pilih menu favoritmu dan atur jumlah pesanan.",
                        14,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                subtitle,
                5
        );

        contentContainer.addView(
                subtitle
        );

        LinearLayout stats =
                new LinearLayout(
                        this
                );

        stats.setOrientation(
                LinearLayout.HORIZONTAL
        );

        LinearLayout.LayoutParams statsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(88)
                );

        statsParams.setMargins(
                0,
                dp(18),
                0,
                dp(8)
        );

        stats.setLayoutParams(
                statsParams
        );

        menuList =
                databaseHelper.getAllMenus();

        int transactionCount =
                databaseHelper.getTransactionCount();

        View menuStat =
                createStatCard(
                        "🍴",
                        String.valueOf(
                                menuList.size()
                        ),
                        "Menu tersedia",
                        PURPLE
                );

        View transactionStat =
                createStatCard(
                        "🧾",
                        String.valueOf(
                                transactionCount
                        ),
                        "Total pesanan",
                        PINK
                );

        stats.addView(
                menuStat,
                new LinearLayout.LayoutParams(
                        0,
                        dp(88),
                        1
                )
        );

        LinearLayout.LayoutParams secondStatParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(88),
                        1
                );

        secondStatParams.setMargins(
                dp(10),
                0,
                0,
                0
        );

        stats.addView(
                transactionStat,
                secondStatParams
        );

        contentContainer.addView(
                stats
        );

        TextView sectionTitle =
                createText(
                        "Daftar Menu",
                        19,
                        TEXT_DARK,
                        true
                );

        setTopMargin(
                sectionTitle,
                10
        );

        contentContainer.addView(
                sectionTitle
        );

        if (menuList.isEmpty()) {

            TextView empty =
                    createText(
                            "Belum ada menu yang tersedia.",
                            14,
                            TEXT_GRAY,
                            false
                    );

            setTopMargin(
                    empty,
                    12
            );

            contentContainer.addView(
                    empty
            );

            return;
        }

        for (Menu menu : menuList) {

            contentContainer.addView(
                    createMenuCard(
                            menu
                    )
            );
        }

        updateTotal();
    }

    private View createStatCard(
            String icon,
            String value,
            String label,
            int accent
    ) {

        LinearLayout card =
                new LinearLayout(
                        this
                );

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(13),
                dp(10),
                dp(13),
                dp(10)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                Color.WHITE
        );

        bg.setCornerRadius(
                dp(20)
        );

        card.setBackground(
                bg
        );

        card.setElevation(
                dp(2)
        );

        TextView iconView =
                createText(
                        icon,
                        20,
                        accent,
                        false
                );

        iconView.setGravity(
                Gravity.CENTER
        );

        GradientDrawable iconBg =
                new GradientDrawable();

        iconBg.setColor(
                blendColor(
                        accent,
                        Color.WHITE,
                        0.88f
                )
        );

        iconBg.setShape(
                GradientDrawable.OVAL
        );

        iconView.setBackground(
                iconBg
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        iconView.setLayoutParams(
                iconParams
        );

        card.addView(
                iconView
        );

        LinearLayout textContainer =
                new LinearLayout(
                        this
                );

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        textParams.setMargins(
                dp(10),
                0,
                0,
                0
        );

        textContainer.setLayoutParams(
                textParams
        );

        TextView number =
                createText(
                        value,
                        20,
                        TEXT_DARK,
                        true
                );

        TextView labelView =
                createText(
                        label,
                        11,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                labelView,
                1
        );

        textContainer.addView(
                number
        );

        textContainer.addView(
                labelView
        );

        card.addView(
                textContainer
        );

        return card;
    }

    private View createMenuCard(
            Menu menu
    ) {

        LinearLayout card =
                new LinearLayout(
                        this
                );

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(15),
                dp(15),
                dp(15),
                dp(15)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(22)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        239,
                        235,
                        247
                )
        );

        card.setBackground(
                background
        );

        card.setElevation(
                dp(2)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                dp(8),
                0,
                dp(8)
        );

        card.setLayoutParams(
                cardParams
        );

        LinearLayout topRow =
                new LinearLayout(
                        this
                );

        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView foodIcon =
                createText(
                        getFoodIcon(
                                menu.getName()
                        ),
                        29,
                        PURPLE,
                        false
                );

        foodIcon.setGravity(
                Gravity.CENTER
        );

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                getIconBackground(
                        menu.getCategory()
                )
        );

        iconBackground.setCornerRadius(
                dp(17)
        );

        foodIcon.setBackground(
                iconBackground
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                );

        foodIcon.setLayoutParams(
                iconParams
        );

        topRow.addView(
                foodIcon
        );

        LinearLayout detailContainer =
                new LinearLayout(
                        this
                );

        detailContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams detailParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        detailParams.setMargins(
                dp(12),
                0,
                dp(8),
                0
        );

        detailContainer.setLayoutParams(
                detailParams
        );

        TextView name =
                createText(
                        menu.getName(),
                        17,
                        TEXT_DARK,
                        true
                );

        TextView category =
                createText(
                        menu.getCategory(),
                        12,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                category,
                3
        );

        TextView price =
                createText(
                        formatRupiah(
                                menu.getPrice()
                        ),
                        14,
                        PURPLE,
                        true
                );

        setTopMargin(
                price,
                3
        );

        detailContainer.addView(
                name
        );

        detailContainer.addView(
                category
        );

        detailContainer.addView(
                price
        );

        topRow.addView(
                detailContainer
        );

        card.addView(
                topRow
        );

        LinearLayout controlRow =
                new LinearLayout(
                        this
                );

        controlRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams controlParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                );

        controlParams.setMargins(
                0,
                dp(13),
                0,
                0
        );

        controlRow.setLayoutParams(
                controlParams
        );

        TextView selectedText =
                createText(
                        "Jumlah",
                        13,
                        TEXT_GRAY,
                        false
                );

        LinearLayout.LayoutParams selectedParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        selectedText.setLayoutParams(
                selectedParams
        );

        controlRow.addView(
                selectedText
        );

        Button minus =
                createCircleButton(
                        "-"
                );

        TextView quantity =
                createText(
                        String.valueOf(
                                cart.getOrDefault(
                                        menu.getId(),
                                        0
                                )
                        ),
                        16,
                        TEXT_DARK,
                        true
                );

        quantity.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams quantityParams =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        quantityParams.setMargins(
                dp(7),
                0,
                dp(7),
                0
        );

        quantity.setLayoutParams(
                quantityParams
        );

        Button plus =
                createCircleButton(
                        "+"
                );

        controlRow.addView(
                minus
        );

        controlRow.addView(
                quantity
        );

        controlRow.addView(
                plus
        );

        card.addView(
                controlRow
        );

        minus.setOnClickListener(
                v -> {

                    int qty =
                            cart.getOrDefault(
                                    menu.getId(),
                                    0
                            );

                    if (qty > 0) {

                        qty--;

                        if (qty == 0) {

                            cart.remove(
                                    menu.getId()
                            );

                        } else {

                            cart.put(
                                    menu.getId(),
                                    qty
                            );
                        }

                        quantity.setText(
                                String.valueOf(
                                        qty
                                )
                        );

                        updateTotal();
                    }
                }
        );

        plus.setOnClickListener(
                v -> {

                    int qty =
                            cart.getOrDefault(
                                    menu.getId(),
                                    0
                            );

                    qty++;

                    cart.put(
                            menu.getId(),
                            qty
                    );

                    quantity.setText(
                            String.valueOf(
                                    qty
                            )
                    );

                    updateTotal();
                }
        );

        return card;
    }

    private Button createCircleButton(
            String text
    ) {

        Button button =
                new Button(
                        this
                );

        button.setText(
                text
        );

        button.setTextSize(
                18
        );

        button.setAllCaps(
                false
        );

        button.setTextColor(
                PURPLE
        );

        button.setPadding(
                0,
                0,
                0,
                0
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                PURPLE_LIGHT
        );

        background.setCornerRadius(
                dp(14)
        );

        button.setBackground(
                background
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        button.setLayoutParams(
                params
        );

        return button;
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    private void checkout() {

        if (cart.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pilih menu terlebih dahulu 🍔",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int total =
                calculateTotal();

        LinearLayout layout =
                new LinearLayout(
                        this
                );

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                dp(22),
                dp(4),
                dp(22),
                0
        );

        TextView totalView =
                createText(
                        "Total pembayaran\n" +
                                formatRupiah(total),
                        18,
                        TEXT_DARK,
                        true
                );

        layout.addView(
                totalView
        );

        EditText noteInput =
                new EditText(
                        this
                );

        noteInput.setHint(
                "Contoh: Tidak pedas, saus dipisah..."
        );

        noteInput.setMinLines(
                3
        );

        noteInput.setGravity(
                Gravity.TOP
        );

        LinearLayout.LayoutParams noteParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(100)
                );

        noteParams.setMargins(
                0,
                dp(15),
                0,
                0
        );

        noteInput.setLayoutParams(
                noteParams
        );

        layout.addView(
                noteInput
        );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        this
                )
                        .setTitle(
                                "Konfirmasi Pesanan 🧾"
                        )
                        .setView(
                                layout
                        )
                        .setNegativeButton(
                                "Batal",
                                null
                        )
                        .setPositiveButton(
                                "Simpan Pesanan",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    positive.setTextColor(
                            PURPLE
                    );

                    positive.setOnClickListener(
                            v -> {

                                String note =
                                        noteInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String date =
                                        new SimpleDateFormat(
                                                "dd MMM yyyy, HH:mm",
                                                new Locale(
                                                        "id",
                                                        "ID"
                                                )
                                        ).format(
                                                new Date()
                                        );

                                long result =
                                        databaseHelper
                                                .insertTransaction(
                                                        total,
                                                        note,
                                                        date,
                                                        cart
                                                );

                                if (result != -1) {

                                    cart.clear();

                                    updateTotal();

                                    Toast.makeText(
                                            this,
                                            "Pesanan berhasil disimpan 🎉",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialog.dismiss();

                                    showHistoryPage();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Gagal menyimpan pesanan.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
        );

        dialog.show();
    }

    // =========================================================
    // HISTORY
    // =========================================================

    private void showHistoryPage() {

        contentContainer.removeAllViews();

        activateNavigation(
                navHistory
        );

        TextView title =
                createText(
                        "Riwayat Pesanan 🧾",
                        24,
                        TEXT_DARK,
                        true
                );

        contentContainer.addView(
                title
        );

        TextView subtitle =
                createText(
                        "Semua pesanan yang sudah kamu simpan.",
                        14,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                subtitle,
                5
        );

        contentContainer.addView(
                subtitle
        );

        Cursor cursor =
                databaseHelper.getTransactions();

        if (cursor.getCount() == 0) {

            LinearLayout empty =
                    new LinearLayout(
                            this
                    );

            empty.setOrientation(
                    LinearLayout.VERTICAL
            );

            empty.setGravity(
                    Gravity.CENTER
            );

            empty.setPadding(
                    dp(20),
                    dp(50),
                    dp(20),
                    dp(20)
            );

            TextView icon =
                    createText(
                            "🧾",
                            50,
                            PURPLE,
                            false
                    );

            icon.setGravity(
                    Gravity.CENTER
            );

            TextView emptyTitle =
                    createText(
                            "Belum ada pesanan",
                            18,
                            TEXT_DARK,
                            true
                    );

            emptyTitle.setGravity(
                    Gravity.CENTER
            );

            setTopMargin(
                    emptyTitle,
                    15
            );

            TextView emptyText =
                    createText(
                            "Pesanan yang sudah disimpan\nakan muncul di sini.",
                            14,
                            TEXT_GRAY,
                            false
                    );

            emptyText.setGravity(
                    Gravity.CENTER
            );

            setTopMargin(
                    emptyText,
                    5
            );

            empty.addView(
                    icon
            );

            empty.addView(
                    emptyTitle
            );

            empty.addView(
                    emptyText
            );

            contentContainer.addView(
                    empty
            );

            cursor.close();

            updateTotal();

            return;
        }

        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                int total =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "total"
                                )
                        );

                String note =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "note"
                                )
                        );

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "date_time"
                                )
                        );

                contentContainer.addView(
                        createHistoryCard(
                                id,
                                total,
                                note,
                                date
                        )
                );

            } while (
                    cursor.moveToNext()
            );
        }

        cursor.close();

        updateTotal();
    }

    private View createHistoryCard(
            int id,
            int total,
            String note,
            String date
    ) {

        LinearLayout card =
                new LinearLayout(
                        this
                );

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                Color.WHITE
        );

        bg.setCornerRadius(
                dp(22)
        );

        bg.setStroke(
                dp(1),
                Color.rgb(
                        239,
                        235,
                        247
                )
        );

        card.setBackground(
                bg
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                dp(8),
                0,
                dp(8)
        );

        card.setLayoutParams(
                cardParams
        );

        LinearLayout header =
                new LinearLayout(
                        this
                );

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView orderNumber =
                createText(
                        "Pesanan #" + id,
                        17,
                        TEXT_DARK,
                        true
                );

        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        orderNumber.setLayoutParams(
                numberParams
        );

        TextView totalView =
                createText(
                        formatRupiah(total),
                        16,
                        PURPLE,
                        true
                );

        header.addView(
                orderNumber
        );

        header.addView(
                totalView
        );

        card.addView(
                header
        );

        TextView dateView =
                createText(
                        date,
                        12,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                dateView,
                5
        );

        card.addView(
                dateView
        );

        if (note != null &&
                !note.trim().isEmpty()) {

            TextView noteView =
                    createText(
                            "Catatan: " +
                                    note,
                            13,
                            TEXT_GRAY,
                            false
                    );

            setTopMargin(
                    noteView,
                    8
            );

            card.addView(
                    noteView
            );
        }

        Button detail =
                new Button(
                        this
                );

        detail.setText(
                "Lihat Detail"
        );

        detail.setAllCaps(
                false
        );

        detail.setTextColor(
                PURPLE
        );

        detail.setTextSize(
                13
        );

        detail.setBackground(
                roundedBackground(
                        PURPLE_LIGHT,
                        16
                )
        );

        LinearLayout.LayoutParams detailParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(46)
                );

        detailParams.setMargins(
                0,
                dp(10),
                0,
                0
        );

        detail.setLayoutParams(
                detailParams
        );

        card.addView(
                detail
        );

        detail.setOnClickListener(
                v -> showTransactionDetail(
                        id
                )
        );

        return card;
    }

    private void showTransactionDetail(
            int transactionId
    ) {

        Cursor cursor =
                databaseHelper
                        .getTransactionItems(
                                transactionId
                        );

        StringBuilder details =
                new StringBuilder();

        if (cursor.moveToFirst()) {

            do {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "menu_name"
                                )
                        );

                int quantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "quantity"
                                )
                        );

                int subtotal =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "subtotal"
                                )
                        );

                details.append(
                        "• "
                );

                details.append(
                        name
                );

                details.append(
                        " x"
                );

                details.append(
                        quantity
                );

                details.append(
                        "  "
                );

                details.append(
                        formatRupiah(
                                subtotal
                        )
                );

                details.append(
                        "\n"
                );

            } while (
                    cursor.moveToNext()
            );
        }

        cursor.close();

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Detail Pesanan #" +
                                transactionId
                )
                .setMessage(
                        details.toString()
                )
                .setPositiveButton(
                        "Tutup",
                        null
                )
                .show();
    }

    // =========================================================
    // PROFILE
    // =========================================================

    private void showProfilePage() {

        contentContainer.removeAllViews();

        activateNavigation(
                navProfile
        );

        TextView title =
                createText(
                        "Profil Kasir 👤",
                        24,
                        TEXT_DARK,
                        true
                );

        contentContainer.addView(
                title
        );

        TextView subtitle =
                createText(
                        "Kelola informasi toko dan catatan pribadimu.",
                        14,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                subtitle,
                5
        );

        contentContainer.addView(
                subtitle
        );

        TextView section =
                createText(
                        "Informasi Toko",
                        18,
                        TEXT_DARK,
                        true
                );

        setTopMargin(
                section,
                20
        );

        contentContainer.addView(
                section
        );

        EditText nameInput =
                createEditText(
                        "Nama kasir"
                );

        nameInput.setText(
                preferences.getString(
                        "name",
                        ""
                )
        );

        setTopMargin(
                nameInput,
                10
        );

        contentContainer.addView(
                nameInput
        );

        EditText storeInput =
                createEditText(
                        "Nama toko"
                );

        storeInput.setText(
                preferences.getString(
                        "store",
                        "FoodOrder Store"
                )
        );

        setTopMargin(
                storeInput,
                10
        );

        contentContainer.addView(
                storeInput
        );

        Button saveProfile =
                createPrimaryButton(
                        "Simpan Profil"
                );

        setTopMargin(
                saveProfile,
                14
        );

        contentContainer.addView(
                saveProfile
        );

        saveProfile.setOnClickListener(
                v -> {

                    String name =
                            nameInput
                                    .getText()
                                    .toString()
                                    .trim();

                    String store =
                            storeInput
                                    .getText()
                                    .toString()
                                    .trim();

                    if (name.isEmpty()) {

                        name = "Kasir";
                    }

                    if (store.isEmpty()) {

                        store =
                                "FoodOrder Store";
                    }

                    preferences.edit()
                            .putString(
                                    "name",
                                    name
                            )
                            .putString(
                                    "store",
                                    store
                            )
                            .apply();

                    loadProfile();

                    Toast.makeText(
                            this,
                            "Profil berhasil disimpan ✅",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        TextView storageTitle =
                createText(
                        "Data Storage",
                        18,
                        TEXT_DARK,
                        true
                );

        setTopMargin(
                storageTitle,
                25
        );

        contentContainer.addView(
                storageTitle
        );

        View storageInfo =
                createStorageInfoCard();

        setTopMargin(
                storageInfo,
                8
        );

        contentContainer.addView(
                storageInfo
        );

        Button noteButton =
                new Button(
                        this
                );

        noteButton.setText(
                "📝 Buka Catatan Toko"
        );

        noteButton.setAllCaps(
                false
        );

        noteButton.setTextColor(
                PURPLE
        );

        noteButton.setTextSize(
                14
        );

        noteButton.setBackground(
                roundedBackground(
                        PURPLE_LIGHT,
                        18
                )
        );

        LinearLayout.LayoutParams noteParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        noteParams.setMargins(
                0,
                dp(10),
                0,
                dp(20)
        );

        noteButton.setLayoutParams(
                noteParams
        );

        contentContainer.addView(
                noteButton
        );

        noteButton.setOnClickListener(
                v -> showInternalNoteDialog()
        );
    }

    private View createStorageInfoCard() {

        LinearLayout card =
                new LinearLayout(
                        this
                );

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        card.setBackground(
                roundedBackground(
                        Color.WHITE,
                        20
                )
        );

        TextView one =
                createText(
                        "🔐 SharedPreferences",
                        15,
                        PURPLE,
                        true
                );

        TextView oneDesc =
                createText(
                        "Menyimpan nama kasir dan nama toko.",
                        13,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                oneDesc,
                3
        );

        TextView two =
                createText(
                        "🗄️ SQLite",
                        15,
                        PURPLE,
                        true
                );

        setTopMargin(
                two,
                13
        );

        TextView twoDesc =
                createText(
                        "Menyimpan menu dan transaksi pesanan.",
                        13,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                twoDesc,
                3
        );

        TextView three =
                createText(
                        "📁 Internal Storage",
                        15,
                        PURPLE,
                        true
                );

        setTopMargin(
                three,
                13
        );

        TextView threeDesc =
                createText(
                        "Menyimpan file catatan toko.",
                        13,
                        TEXT_GRAY,
                        false
                );

        setTopMargin(
                threeDesc,
                3
        );

        card.addView(
                one
        );

        card.addView(
                oneDesc
        );

        card.addView(
                two
        );

        card.addView(
                twoDesc
        );

        card.addView(
                three
        );

        card.addView(
                threeDesc
        );

        return card;
    }

    // =========================================================
    // INTERNAL STORAGE
    // =========================================================

    private void showInternalNoteDialog() {

        EditText input =
                new EditText(
                        this
                );

        input.setHint(
                "Tulis catatan toko..."
        );

        input.setMinLines(
                6
        );

        input.setGravity(
                Gravity.TOP
        );

        input.setText(
                readInternalNote()
        );

        LinearLayout container =
                new LinearLayout(
                        this
                );

        container.setPadding(
                dp(22),
                0,
                dp(22),
                0
        );

        container.addView(
                input
        );

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Catatan Toko 📝"
                )
                .setView(
                        container
                )
                .setNegativeButton(
                        "Batal",
                        null
                )
                .setNeutralButton(
                        "Hapus",
                        (dialog, which) -> {

                            deleteInternalNote();

                            Toast.makeText(
                                    this,
                                    "Catatan dihapus.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .setPositiveButton(
                        "Simpan",
                        (dialog, which) -> {

                            saveInternalNote(
                                    input.getText()
                                            .toString()
                            );

                            Toast.makeText(
                                    this,
                                    "Catatan tersimpan ✅",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    private void saveInternalNote(
            String text
    ) {

        try {

            OutputStreamWriter writer =
                    new OutputStreamWriter(
                            openFileOutput(
                                    "catatan_toko.txt",
                                    MODE_PRIVATE
                            )
                    );

            writer.write(
                    text
            );

            writer.close();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Gagal menyimpan catatan.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private String readInternalNote() {

        StringBuilder result =
                new StringBuilder();

        try {

            FileInputStream stream =
                    openFileInput(
                            "catatan_toko.txt"
                    );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    stream
                            )
                    );

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                result.append(
                        line
                );

                result.append(
                        "\n"
                );
            }

            reader.close();

        } catch (Exception e) {

            return "";
        }

        return result.toString();
    }

    private void deleteInternalNote() {

        deleteFile(
                "catatan_toko.txt"
        );
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void activateNavigation(
            TextView active
    ) {

        setInactive(
                navMenu
        );

        setInactive(
                navHistory
        );

        setInactive(
                navProfile
        );

        active.setBackground(
                roundedBackground(
                        PURPLE,
                        18
                )
        );

        active.setTextColor(
                Color.WHITE
        );
    }

    private void setInactive(
            TextView view
    ) {

        view.setBackground(
                roundedBackground(
                        Color.rgb(
                                243,
                                240,
                                250
                        ),
                        18
                )
        );

        view.setTextColor(
                TEXT_GRAY
        );
    }

    // =========================================================
    // CART
    // =========================================================

    private int calculateTotal() {

        int total = 0;

        for (
                Integer menuId :
                cart.keySet()
        ) {

            int quantity =
                    cart.get(menuId);

            Menu menu =
                    databaseHelper
                            .getMenuById(
                                    menuId
                            );

            if (menu != null) {

                total +=
                        menu.getPrice()
                                * quantity;
            }
        }

        return total;
    }

    private void updateTotal() {

        int total =
                calculateTotal();

        tvTotal.setText(
                formatRupiah(
                        total
                )
        );
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private TextView createText(
            String text,
            float size,
            int color,
            boolean bold
    ) {

        TextView view =
                new TextView(
                        this
                );

        view.setText(
                text
        );

        view.setTextSize(
                size
        );

        view.setTextColor(
                color
        );

        if (bold) {

            view.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        return view;
    }

    private EditText createEditText(
            String hint
    ) {

        EditText editText =
                new EditText(
                        this
                );

        editText.setHint(
                hint
        );

        editText.setTextSize(
                14
        );

        editText.setSingleLine(
                true
        );

        editText.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        editText.setBackground(
                roundedStrokeBackground(
                        Color.WHITE,
                        Color.rgb(
                                231,
                                226,
                                241
                        ),
                        16
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        editText.setLayoutParams(
                params
        );

        return editText;
    }

    private Button createPrimaryButton(
            String text
    ) {

        Button button =
                new Button(
                        this
                );

        button.setText(
                text
        );

        button.setAllCaps(
                false
        );

        button.setTextColor(
                Color.WHITE
        );

        button.setTextSize(
                14
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        button.setBackground(
                roundedGradientBackground()
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        button.setLayoutParams(
                params
        );

        return button;
    }

    private GradientDrawable roundedGradientBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setGradientType(
                GradientDrawable.LINEAR_GRADIENT
        );

        drawable.setOrientation(
                GradientDrawable.Orientation.LEFT_RIGHT
        );

        drawable.setColors(
                new int[]{
                        Color.rgb(
                                91,
                                67,
                                230
                        ),
                        Color.rgb(
                                179,
                                76,
                                235
                        )
                }
        );

        drawable.setCornerRadius(
                dp(18)
        );

        return drawable;
    }

    private GradientDrawable roundedBackground(
            int color,
            float radius
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                color
        );

        drawable.setCornerRadius(
                dp(
                        Math.round(radius)
                )
        );

        return drawable;
    }

    private GradientDrawable roundedStrokeBackground(
            int backgroundColor,
            int strokeColor,
            float radius
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                backgroundColor
        );

        drawable.setCornerRadius(
                dp(
                        Math.round(radius)
                )
        );

        drawable.setStroke(
                dp(1),
                strokeColor
        );

        return drawable;
    }

    private void setTopMargin(
            View view,
            int margin
    ) {

        if (view.getLayoutParams()
                instanceof LinearLayout.LayoutParams) {

            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams)
                            view.getLayoutParams();

            params.setMargins(
                    params.leftMargin,
                    dp(margin),
                    params.rightMargin,
                    params.bottomMargin
            );

            view.setLayoutParams(
                    params
            );

        } else {

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(
                    0,
                    dp(margin),
                    0,
                    0
            );

            view.setLayoutParams(
                    params
            );
        }
    }

    private int dp(
            int value
    ) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                value * density
        );
    }

    private String formatRupiah(
            int number
    ) {

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale(
                                "id",
                                "ID"
                        )
                );

        return format
                .format(number)
                .replace(
                        ",00",
                        ""
                );
    }

    private String getFoodIcon(
            String name
    ) {

        String lower =
                name.toLowerCase(
                        Locale.ROOT
                );

        if (lower.contains("burger")) {
            return "🍔";
        }

        if (lower.contains("mie")) {
            return "🍜";
        }

        if (lower.contains("nasi")) {
            return "🍚";
        }

        if (lower.contains("kentang")) {
            return "🍟";
        }

        if (lower.contains("teh")) {
            return "🧋";
        }

        if (lower.contains("jeruk")) {
            return "🍊";
        }

        return "🍽️";
    }

    private int getIconBackground(
            String category
    ) {

        if (category.equalsIgnoreCase(
                "Minuman"
        )) {

            return Color.rgb(
                    237,
                    247,
                    255
            );
        }

        if (category.equalsIgnoreCase(
                "Snack"
        )) {

            return Color.rgb(
                    255,
                    242,
                    231
            );
        }

        return Color.rgb(
                243,
                239,
                255
        );
    }

    private int blendColor(
            int foreground,
            int background,
            float ratio
    ) {

        int red =
                Math.round(
                        Color.red(foreground)
                                * (1 - ratio)
                                +
                                Color.red(background)
                                        * ratio
                );

        int green =
                Math.round(
                        Color.green(foreground)
                                * (1 - ratio)
                                +
                                Color.green(background)
                                        * ratio
                );

        int blue =
                Math.round(
                        Color.blue(foreground)
                                * (1 - ratio)
                                +
                                Color.blue(background)
                                        * ratio
                );

        return Color.rgb(
                red,
                green,
                blue
        );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {

            databaseHelper.close();
        }
    }
}