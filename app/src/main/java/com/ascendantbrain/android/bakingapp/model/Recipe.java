package com.ascendantbrain.android.bakingapp.model;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.COLUMN_RECIPE_ID;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.INDEX_ID;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.INDEX_NAME;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.INDEX_SERVINGS;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.INDEX_IMAGEURL;

import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.CONTENT_URI;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe.PROJECTION;


public class Recipe {
    public int id;
    public String name;
    public int servings;
    public String imageUrl;

    Recipe(int id, String name, int servings, String imageUrl) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }

    public static Recipe fromCursor(@NonNull Cursor c) {
        // ensure cursor is in a valid state
        if(c.isClosed()) return null;

        // retrieve data from cursor
        int recipe_id = c.getInt(INDEX_ID);
        int servings = c.getInt(INDEX_SERVINGS);
        String name = c.getString(INDEX_NAME);
        String imageUrl = c.getString(INDEX_IMAGEURL);

        return new Recipe(recipe_id,name, servings,imageUrl);
    }

    public static Uri getContentUri() { return CONTENT_URI; }

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static String getSortOrder() { return COLUMN_RECIPE_ID + " ASC";}
}
