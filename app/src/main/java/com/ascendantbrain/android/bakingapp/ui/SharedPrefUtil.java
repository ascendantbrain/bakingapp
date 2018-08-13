package com.ascendantbrain.android.bakingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtil {

    private static final String KEY_CURRENT_RECIPE_ID = "key_current_recipe_id";

    public static boolean setCurrentRecipeId(Context context, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_CURRENT_RECIPE_ID, value);
            return editor.commit();
        }
        return false;
    }

    public static int getCurrentRecipeId(Context context) {
        int value = 1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(KEY_CURRENT_RECIPE_ID, value);
        }
        return value;
    }

}
