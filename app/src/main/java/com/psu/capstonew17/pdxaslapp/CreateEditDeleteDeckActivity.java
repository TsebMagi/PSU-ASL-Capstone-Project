//MIT License Copyright 2017 PSU ASL Capstone Team

package com.psu.capstonew17.pdxaslapp;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.psu.capstonew17.backend.api.Deck;
import com.psu.capstonew17.backend.api.DeckManager;
import com.psu.capstonew17.backend.data.ExternalDeckManager;

public class CreateEditDeleteDeckActivity extends BaseActivity{
    static final int        MIN_CARDS       = 2;
    static final int        MAX_STRG_LNGTH  = 50;
    private final String    CHECKED_DECK   = "checkedDeck";

    private RadioGroup  deckRG;
    private List<Deck>  decks;
    private DeckManager deckManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_delete_deck);

        deckManager = ExternalDeckManager.getInstance(this);
        deckRG      = (RadioGroup) findViewById(R.id.deckRButtons);
        populateRadioGroup();
    }

    public void populateRadioGroup(){
        deckRG.clearCheck();
        deckRG.removeAllViews();
        decks = deckManager.getDecks(null);

        for (int i = 0; i < decks.size(); i++) {
            RadioButton currRad = new RadioButton(this);
            currRad.setId(i);
            currRad.setText(decks.get(i).getName());
            deckRG.addView(currRad);
        }
    }

    public void onCreateClicked(View view) {
        Intent intent;
        intent = new Intent(this, CreateDeckActivity.class);
        startActivity(intent);
    }

    public void onDeleteClicked(View view) {
        int index = deckRG.getCheckedRadioButtonId();
        if(index == -1) {
            Toast.makeText(this, R.string.deck_not_selected, Toast.LENGTH_SHORT).show();

        } else {
            Deck selectedDeck = decks.get(index);
            selectedDeck.delete();
            selectedDeck.commit();
            populateRadioGroup();
        }
    }

    public void onEditClicked(View view) {
        int index = deckRG.getCheckedRadioButtonId();
        if(index == -1) {
            Toast.makeText(this, R.string.deck_not_selected, Toast.LENGTH_SHORT).show();

        } else {
            Intent intent;
            intent = new Intent(this, EditDeckActivity.class);
            intent.putExtra(CHECKED_DECK, decks.get(index).getName());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateRadioGroup();
    }
}
