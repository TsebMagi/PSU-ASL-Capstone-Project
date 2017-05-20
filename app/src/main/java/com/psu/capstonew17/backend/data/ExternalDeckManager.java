//MIT License Copyright 2017 PSU ASL Capstone Team

package com.psu.capstonew17.backend.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import com.psu.capstonew17.backend.api.*;
import com.psu.capstonew17.backend.db.AslDbHelper;
import com.psu.capstonew17.backend.db.AslDbContract.*;

public class ExternalDeckManager implements DeckManager{
    public static ExternalDeckManager INSTANCE = new ExternalDeckManager();

    private AslDbHelper dbHelper;

    public static DeckManager getInstance(Context context){
        INSTANCE.dbHelper = AslDbHelper.getInstance(context);
        ExternalCardManager.getInstance(context);
        return INSTANCE;
    }

    public AslDbHelper getDbHelper(){
        return dbHelper;
    }

    private List<Card> getCardsForDeck(int deckId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = dbHelper.buildSelectQuery(
                RelationEntry.TABLE_NAME,
                Arrays.asList(RelationEntry.COLUMN_DECK + "=" + Integer.toString(deckId))
        );
        Cursor cursor = db.rawQuery(query, null);
        List<Card> cards = new ArrayList<Card>();
        while(cursor.moveToNext()){
            int cardId = cursor.getInt(cursor.getColumnIndex(RelationEntry.COLUMN_CARD));
            cards.add(ExternalCardManager.INSTANCE.getCard(cardId));
        }
        cursor.close();
        db.close();
        return cards;
    }

    public Deck getDeck(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = dbHelper.buildSelectQuery(
                DeckEntry.TABLE_NAME,
                Arrays.asList(DeckEntry.COLUMN_ID + "=" + id)
        );
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            String deckName = cursor.getString(cursor.getColumnIndex(DeckEntry.COLUMN_NAME));
            return new ExternalDeck(id, deckName, getCardsForDeck(id));
        }
        cursor.close();
        db.close();
        return null;
    }

    public Deck getDeck(String name)
    {
        Deck deck = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = dbHelper.buildSelectQuery(
                DeckEntry.TABLE_NAME,
                Arrays.asList(DeckEntry.COLUMN_NAME + "='" + name + "'")
        );
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            int deckId = cursor.getInt(cursor.getColumnIndex(DeckEntry.COLUMN_ID));
            deck = new ExternalDeck(deckId, name, getCardsForDeck(deckId));
        }
        cursor.close();
        db.close();
        return deck;
    }

    public boolean deckExists(String name){
        boolean exists = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = dbHelper.buildSelectQuery(
                DeckEntry.TABLE_NAME,
                Arrays.asList(DeckEntry.COLUMN_NAME + "='" + name + "'")
        );
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            exists = true;
        }
        cursor.close();
        db.close();
        return exists;
    }

    @Override
    public List<Deck> getDecks(String name) {
        List<Deck> decks = new ArrayList<Deck>();
        if(name == null){
            // get all decks
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = dbHelper.buildSelectQuery(DeckEntry.TABLE_NAME, null);
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                int deckId = cursor.getInt(cursor.getColumnIndex(DeckEntry.COLUMN_ID));
                decks.add(getDeck(deckId));
            }
            cursor.close();
            db.close();
        }
        Deck namedDeck = getDeck(name);
        if(namedDeck != null){
            decks.add(namedDeck);
        }
        return decks;
    }


    @Override
    public Deck buildDeck(String name, List<Card> cards) throws ObjectAlreadyExistsException {
        if(deckExists(name)){
            throw new ObjectAlreadyExistsException("The deck '" + name + "' already exists.");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DeckEntry.COLUMN_NAME, name);
        int deckId = (int) db.insert(DeckEntry.TABLE_NAME, null, values);
        for(Card card : cards){
            values = new ContentValues();
            values.put(RelationEntry.COLUMN_DECK, deckId);
            values.put(RelationEntry.COLUMN_CARD, ((ExternalCard) card).getId());
            db.insert(RelationEntry.TABLE_NAME, null, values);
        }
        db.close();
        return new ExternalDeck(deckId, name, cards);
    }

    @Override
    public Deck getDefaultDeck() {
        return new ExternalDefaultDeck();
    }
}
