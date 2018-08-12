package com.ascendantbrain.android.bakingapp.model;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient.CONTENT_URI;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient.INDEX_INGREDIENT;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient.INDEX_MEASURE;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient.INDEX_QUANTITY;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient.PROJECTION;

public class Ingredient {

    public double quantity;
    public String measure;
    public String ingredient;

    public Ingredient(double quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public static Ingredient fromCursor(@NonNull Cursor c) {
        // ensure cursor is in a valid state
        if(c.isClosed()) return null;

        // retrieve data from cursor
        double quantity = c.getDouble(INDEX_QUANTITY);
        String measure = c.getString(INDEX_MEASURE);
        String ingredient = c.getString(INDEX_INGREDIENT);

        return new Ingredient(quantity, measure,ingredient);
    }

    public static Uri getContentUri() { return CONTENT_URI; }

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static String getSortOrder() { return BaseColumns._ID + " ASC";}

}
