package com.ascendantbrain.android.bakingapp.model;

import com.ascendantbrain.android.bakingapp.R;

/** ONLY FOR INTERNAL TESTING and subject to copyright considerations */
public class ImageHelper {

    public static int getRecipeImage(int recipeId) {
        switch(recipeId) {
            case 1: return R.drawable.nutella_pie;
            case 2: return R.drawable.brownies;
            case 3: return R.drawable.yellow_cake;
            case 4: return R.drawable.cheesecake;
            default: return R.drawable.dessert_collection;
        }
    }

}
