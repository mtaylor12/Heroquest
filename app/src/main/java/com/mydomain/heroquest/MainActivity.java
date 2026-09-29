package com.mydomain.heroquest;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_TABS   = 6;
    private static final int SCROLL_AMT = 80; // pixels per arrow tap

    // ── UI refs ──────────────────────────────────────────────
    private LinearLayout tabContainer;
    private Button       addCharacterButton;
    private Button       resetButton, deleteButton;
    private Button       plusButton, minusButton;
    private Button       lvlPlusButton, lvlMinusButton;
    private Button       addSpellButton;
    private Button       weaponsScrollUp, weaponsScrollDown;
    private Button       itemsScrollUp,   itemsScrollDown;
    private Button       spellsScrollUp,  spellsScrollDown;
    private ScrollView   spellScrollView;
    private Switch       keepScreenOnSwitch;

    private EditText nameInput, speciesInput, moneyInput;
    private EditText attInput, defInput, bodyInput, mindInput;
    private EditText bodyPointsInput, lvlInput;
    private EditText weaponsInput, itemsInput;
    private EditText spellEntryInput;
    private LinearLayout spellListContainer;

    // ── Per-character data ───────────────────────────────────
    private ArrayList<String> tabNames       = new ArrayList<>();
    private ArrayList<String> speciesData    = new ArrayList<>();
    private ArrayList<String> moneyData      = new ArrayList<>();
    private ArrayList<String> attData        = new ArrayList<>();
    private ArrayList<String> defData        = new ArrayList<>();
    private ArrayList<String> bodyData       = new ArrayList<>();
    private ArrayList<String> mindData       = new ArrayList<>();
    private ArrayList<String> bodyPointsData = new ArrayList<>();
    private ArrayList<String> lvlData        = new ArrayList<>();
    private ArrayList<String> weaponsData    = new ArrayList<>();
    private ArrayList<String> itemsData      = new ArrayList<>();
    private ArrayList<String> spellsData     = new ArrayList<>();

    private int activeTab = 0;
    private SharedPreferences prefs;

    // ── Lifecycle ────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        tabContainer       = findViewById(R.id.tabContainer);
        addCharacterButton = findViewById(R.id.addCharacterButton);
        resetButton        = findViewById(R.id.resetButton);
        deleteButton       = findViewById(R.id.deleteButton);
        plusButton         = findViewById(R.id.plusButton);
        minusButton        = findViewById(R.id.minusButton);
        lvlPlusButton      = findViewById(R.id.lvlPlusButton);
        lvlMinusButton     = findViewById(R.id.lvlMinusButton);
        addSpellButton     = findViewById(R.id.addSpellButton);
        weaponsScrollUp    = findViewById(R.id.weaponsScrollUp);
        weaponsScrollDown  = findViewById(R.id.weaponsScrollDown);
        itemsScrollUp      = findViewById(R.id.itemsScrollUp);
        itemsScrollDown    = findViewById(R.id.itemsScrollDown);
        spellsScrollUp     = findViewById(R.id.spellsScrollUp);
        spellsScrollDown   = findViewById(R.id.spellsScrollDown);
        spellScrollView    = findViewById(R.id.spellScrollView);
        keepScreenOnSwitch = findViewById(R.id.keepScreenOnSwitch);

        nameInput       = findViewById(R.id.nameInput);
        speciesInput    = findViewById(R.id.speciesInput);
        moneyInput      = findViewById(R.id.moneyInput);
        attInput        = findViewById(R.id.attInput);
        defInput        = findViewById(R.id.defInput);
        bodyInput       = findViewById(R.id.bodyInput);
        mindInput       = findViewById(R.id.mindInput);
        bodyPointsInput = findViewById(R.id.bodyPointsInput);
        lvlInput        = findViewById(R.id.lvlInput);
        weaponsInput    = findViewById(R.id.weaponsInput);
        itemsInput      = findViewById(R.id.itemsInput);
        spellEntryInput = findViewById(R.id.spellEntryInput);
        spellListContainer = findViewById(R.id.spellListContainer);

        prefs = getSharedPreferences("CharacterSheet", MODE_PRIVATE);

        // ── Button listeners ─────────────────────────────────

        addCharacterButton.setOnClickListener(v -> {
            if (tabNames.size() < MAX_TABS) {
                saveTab(activeTab);
                createTab("");
            } else {
                Toast.makeText(this, "Maximum 6 characters", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> showResetConfirmDialog());
        deleteButton.setOnClickListener(v -> showDeleteConfirmDialog());

        // Body points
        plusButton.setOnClickListener(v -> {
            int val = parseIntField(bodyPointsInput);
            bodyPointsInput.setText(String.valueOf(val + 1));
            saveTab(activeTab);
        });
        minusButton.setOnClickListener(v -> {
            int val = parseIntField(bodyPointsInput);
            bodyPointsInput.setText(String.valueOf(Math.max(0, val - 1)));
            saveTab(activeTab);
        });

        // Level
        lvlPlusButton.setOnClickListener(v -> {
            int val = parseIntField(lvlInput);
            lvlInput.setText(String.valueOf(val + 1));
            saveTab(activeTab);
        });
        lvlMinusButton.setOnClickListener(v -> {
            int val = parseIntField(lvlInput);
            lvlInput.setText(String.valueOf(Math.max(1, val - 1)));
            saveTab(activeTab);
        });

        // Spell add
        addSpellButton.setOnClickListener(v -> {
            String spell = spellEntryInput.getText().toString().trim();
            if (!spell.isEmpty()) {
                addSpellRow(spell, false);
                spellEntryInput.setText("");
                saveTab(activeTab);
            }
        });

        // Scroll arrows — weapons
        weaponsScrollUp.setOnClickListener(v ->
                weaponsInput.scrollTo(0, Math.max(0, weaponsInput.getScrollY() - SCROLL_AMT)));

        itemsScrollUp.setOnClickListener(v ->
                itemsInput.scrollTo(0, Math.max(0, itemsInput.getScrollY() - SCROLL_AMT)));

        spellsScrollUp.setOnClickListener(v ->
                spellScrollView.scrollTo(0, Math.max(0, spellScrollView.getScrollY() - SCROLL_AMT)));
        weaponsScrollDown.setOnClickListener(v ->
                weaponsInput.scrollBy(0, SCROLL_AMT));

        // Scroll arrows — items
        weaponsScrollUp.setOnClickListener(v ->
                weaponsInput.scrollTo(0, Math.max(0, weaponsInput.getScrollY() - SCROLL_AMT)));

        itemsScrollUp.setOnClickListener(v ->
                itemsInput.scrollTo(0, Math.max(0, itemsInput.getScrollY() - SCROLL_AMT)));

        spellsScrollUp.setOnClickListener(v ->
                spellScrollView.scrollTo(0, Math.max(0, spellScrollView.getScrollY() - SCROLL_AMT)));
        itemsScrollDown.setOnClickListener(v ->
                itemsInput.scrollBy(0, SCROLL_AMT));

        // Scroll arrows — spells ScrollView
        weaponsScrollUp.setOnClickListener(v ->
                weaponsInput.scrollTo(0, Math.max(0, weaponsInput.getScrollY() - SCROLL_AMT)));

        itemsScrollUp.setOnClickListener(v ->
                itemsInput.scrollTo(0, Math.max(0, itemsInput.getScrollY() - SCROLL_AMT)));

        spellsScrollUp.setOnClickListener(v ->
                spellScrollView.scrollTo(0, Math.max(0, spellScrollView.getScrollY() - SCROLL_AMT)));
        spellsScrollDown.setOnClickListener(v ->
                spellScrollView.scrollBy(0, SCROLL_AMT));

        // Keep screen on
        keepScreenOnSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        // Load or create initial data
        loadAllData();
        if (tabNames.isEmpty()) {
            createTab("");
        } else {
            rebuildTabButtons();
            switchToTab(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTab(activeTab);
    }

    // ── Tab management ───────────────────────────────────────

    private void createTab(String name) {
        tabNames.add(name);
        speciesData.add("");
        moneyData.add("");
        attData.add("");
        defData.add("");
        bodyData.add("");
        mindData.add("");
        bodyPointsData.add("0");
        lvlData.add("1");
        weaponsData.add("");
        itemsData.add("");
        spellsData.add("");

        addTabButton(tabNames.size() - 1, name);
        switchToTab(tabNames.size() - 1);
        addCharacterButton.setVisibility(tabNames.size() >= MAX_TABS ? View.GONE : View.VISIBLE);
        saveAllData();
    }

    private void addTabButton(int index, String label) {
        Button tabBtn = new Button(this);
        tabBtn.setText(label.isEmpty() ? "Character " + (index + 1) : label);
        tabBtn.setTag(index);
        tabBtn.setAllCaps(false);
        tabBtn.setTextSize(12f);
        tabBtn.setPadding(24, 0, 24, 0);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        p.setMargins(2, 4, 2, 4);
        tabBtn.setLayoutParams(p);

        styleTab(tabBtn, false);
        tabBtn.setOnClickListener(v -> {
            saveTab(activeTab);
            switchToTab((int) v.getTag());
        });
        tabContainer.addView(tabBtn);
    }

    private void rebuildTabButtons() {
        tabContainer.removeAllViews();
        for (int i = 0; i < tabNames.size(); i++) {
            String label = tabNames.get(i);
            addTabButton(i, label.isEmpty() ? "Character " + (i + 1) : label);
        }
        addCharacterButton.setVisibility(tabNames.size() >= MAX_TABS ? View.GONE : View.VISIBLE);
    }

    private void switchToTab(int index) {
        activeTab = index;
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            View child = tabContainer.getChildAt(i);
            if (child instanceof Button) {
                styleTab((Button) child, (int) child.getTag() == index);
            }
        }
        loadTab(index);
    }

    private void styleTab(Button btn, boolean active) {
        btn.setBackgroundColor(active ? Color.parseColor("#5C85D6") : Color.parseColor("#444444"));
        btn.setTextColor(Color.WHITE);
        btn.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    // ── Load / Save ──────────────────────────────────────────

    private void loadTab(int index) {
        nameInput.setText(tabNames.get(index));
        speciesInput.setText(speciesData.get(index));
        moneyInput.setText(moneyData.get(index));
        attInput.setText(attData.get(index));
        defInput.setText(defData.get(index));
        bodyInput.setText(bodyData.get(index));
        mindInput.setText(mindData.get(index));
        bodyPointsInput.setText(bodyPointsData.get(index));
        lvlInput.setText(lvlData.get(index));
        weaponsInput.setText(weaponsData.get(index));
        itemsInput.setText(itemsData.get(index));

        spellListContainer.removeAllViews();
        String spellRaw = spellsData.get(index);
        if (!spellRaw.isEmpty()) {
            for (String entry : spellRaw.split("\\|")) {
                String[] parts = entry.split(":", 2);
                if (parts.length == 2) {
                    addSpellRow(parts[0], parts[1].equals("1"));
                }
            }
        }
    }

    private void saveTab(int index) {
        String name = nameInput.getText().toString().trim();
        tabNames.set(index, name);
        speciesData.set(index, speciesInput.getText().toString());
        moneyData.set(index, moneyInput.getText().toString());
        attData.set(index, attInput.getText().toString());
        defData.set(index, defInput.getText().toString());
        bodyData.set(index, bodyInput.getText().toString());
        mindData.set(index, mindInput.getText().toString());
        bodyPointsData.set(index, bodyPointsInput.getText().toString());
        lvlData.set(index, lvlInput.getText().toString().isEmpty() ? "1" : lvlInput.getText().toString());
        weaponsData.set(index, weaponsInput.getText().toString());
        itemsData.set(index, itemsInput.getText().toString());

        // Serialize spells
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spellListContainer.getChildCount(); i++) {
            View child = spellListContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (sb.length() > 0) sb.append("|");
                sb.append(cb.getTag().toString())
                        .append(":")
                        .append(cb.isChecked() ? "1" : "0");
            }
        }
        spellsData.set(index, sb.toString());

        // Update tab label
        if (index < tabContainer.getChildCount()) {
            View child = tabContainer.getChildAt(index);
            if (child instanceof Button) {
                ((Button) child).setText(name.isEmpty() ? "Character " + (index + 1) : name);
            }
        }
        saveAllData();
    }

    private void saveAllData() {
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt("tabCount", tabNames.size());
        for (int i = 0; i < tabNames.size(); i++) {
            ed.putString("tabName_" + i,    tabNames.get(i));
            ed.putString("species_" + i,    speciesData.get(i));
            ed.putString("money_" + i,      moneyData.get(i));
            ed.putString("att_" + i,        attData.get(i));
            ed.putString("def_" + i,        defData.get(i));
            ed.putString("body_" + i,       bodyData.get(i));
            ed.putString("mind_" + i,       mindData.get(i));
            ed.putString("bodyPoints_" + i, bodyPointsData.get(i));
            ed.putString("lvl_" + i,        lvlData.get(i));
            ed.putString("weapons_" + i,    weaponsData.get(i));
            ed.putString("items_" + i,      itemsData.get(i));
            ed.putString("spells_" + i,     spellsData.get(i));
        }
        ed.apply();
    }

    private void loadAllData() {
        int count = prefs.getInt("tabCount", 0);
        for (int i = 0; i < count; i++) {
            tabNames.add(       prefs.getString("tabName_" + i,    ""));
            speciesData.add(    prefs.getString("species_" + i,    ""));
            moneyData.add(      prefs.getString("money_" + i,      ""));
            attData.add(        prefs.getString("att_" + i,        ""));
            defData.add(        prefs.getString("def_" + i,        ""));
            bodyData.add(       prefs.getString("body_" + i,       ""));
            mindData.add(       prefs.getString("mind_" + i,       ""));
            bodyPointsData.add( prefs.getString("bodyPoints_" + i, "0"));
            lvlData.add(        prefs.getString("lvl_" + i,        "1"));
            weaponsData.add(    prefs.getString("weapons_" + i,    ""));
            itemsData.add(      prefs.getString("items_" + i,      ""));
            spellsData.add(     prefs.getString("spells_" + i,     ""));
        }
    }

    // ── Spell checklist ──────────────────────────────────────

    private void addSpellRow(String spellName, boolean isChecked) {
        CheckBox cb = new CheckBox(this);
        cb.setText(spellName);
        cb.setTag(spellName);
        cb.setChecked(isChecked);
        cb.setTextSize(12f);
        cb.setTextColor(Color.BLACK);
        applyStrikethrough(cb, isChecked);
        cb.setOnCheckedChangeListener((btn, checked) -> {
            applyStrikethrough((CheckBox) btn, checked);
            saveTab(activeTab);
        });
        spellListContainer.addView(cb);
    }

    private void applyStrikethrough(CheckBox cb, boolean strike) {
        if (strike) {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cb.setTextColor(Color.GRAY);
        } else {
            cb.setPaintFlags(cb.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            cb.setTextColor(Color.BLACK);
        }
    }

    // ── Reset / Delete ───────────────────────────────────────

    private void showResetConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Character")
                .setMessage("Clear all fields for this character?")
                .setPositiveButton("Yes", (d, w) -> resetCurrentTab())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Character")
                .setMessage("Delete this character permanently?")
                .setPositiveButton("Yes", (d, w) -> deleteCurrentTab())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetCurrentTab() {
        nameInput.setText("");
        speciesInput.setText("");
        moneyInput.setText("");
        attInput.setText("");
        defInput.setText("");
        bodyInput.setText("");
        mindInput.setText("");
        bodyPointsInput.setText("0");
        lvlInput.setText("1");
        weaponsInput.setText("");
        itemsInput.setText("");
        spellListContainer.removeAllViews();
        saveTab(activeTab);
        Toast.makeText(this, "Character reset", Toast.LENGTH_SHORT).show();
    }

    private void deleteCurrentTab() {
        if (tabNames.size() <= 1) {
            Toast.makeText(this, "Cannot delete the last character", Toast.LENGTH_SHORT).show();
            return;
        }
        tabNames.remove(activeTab);
        speciesData.remove(activeTab);
        moneyData.remove(activeTab);
        attData.remove(activeTab);
        defData.remove(activeTab);
        bodyData.remove(activeTab);
        mindData.remove(activeTab);
        bodyPointsData.remove(activeTab);
        lvlData.remove(activeTab);
        weaponsData.remove(activeTab);
        itemsData.remove(activeTab);
        spellsData.remove(activeTab);

        if (activeTab >= tabNames.size()) activeTab = tabNames.size() - 1;
        rebuildTabButtons();
        switchToTab(activeTab);
        saveAllData();
        Toast.makeText(this, "Character deleted", Toast.LENGTH_SHORT).show();
    }

    // ── Helpers ──────────────────────────────────────────────

    private int parseIntField(EditText et) {
        try {
            String val = et.getText().toString().trim();
            return val.isEmpty() ? 0 : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
