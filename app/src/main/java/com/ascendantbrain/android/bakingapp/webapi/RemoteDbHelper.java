package com.ascendantbrain.android.bakingapp.webapi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ascendantbrain.android.bakingapp.provider.DatabaseContract;

import java.util.ArrayList;
import java.util.List;

public class RemoteDbHelper {

    public static void addToDatabase(@NonNull Context context, @NonNull List<RemoteContract.Recipe> recipes) {
        final Context appContext = context.getApplicationContext();
        if(recipes.isEmpty()) return;

        // remove old entries
        deleteRecipes(appContext);

        List<ContentValues> recipeDataList = new ArrayList<>();
        List<ContentValues> ingredientDataList = new ArrayList<>();
        List<ContentValues> stepDataList = new ArrayList<>();

        // add recipe's data to the recipe data list
        for( RemoteContract.Recipe recipe: recipes) {
            int recipeId = recipe.id;
            ContentValues recipeData = recipe.getValues();
            recipeDataList.add(recipeData);

            // add recipe's ingredient data to the ingredient data list
            for(RemoteContract.Ingredient ingredient: recipe.ingredients) {
                ContentValues ingredientData = ingredient.getValues(recipeId);
                ingredientDataList.add(ingredientData);
            }

            // add recipe's step data to the step data list
            for(RemoteContract.Step step: recipe.steps) {
                ContentValues stepData = step.getValues(recipeId);
                stepDataList.add(stepData);
            }
        }
        ContentResolver cr = appContext.getContentResolver();

        // bulk insert recipe data into recipe table
        ContentValues[] recipeValues = new ContentValues[recipeDataList.size()];
        recipeDataList.toArray(recipeValues);
        cr.bulkInsert(DatabaseContract.Recipe.CONTENT_URI, recipeValues);

        // bulk insert ingredient data into ingredient table
        ContentValues[] ingredientValues = new ContentValues[ingredientDataList.size()];
        ingredientDataList.toArray(ingredientValues);
        cr.bulkInsert(DatabaseContract.Ingredient.CONTENT_URI, ingredientValues);

        // bulk insert step data into step table
        ContentValues[] stepValues = new ContentValues[stepDataList.size()];
        stepDataList.toArray(stepValues);
        cr.bulkInsert(DatabaseContract.Step.CONTENT_URI, stepValues);
    }

    public static void deleteRecipes(Context context) {
        final Context appContext = context.getApplicationContext();
        ContentResolver cr = appContext.getContentResolver();

        // delete all recipe data tables (recipe,ingredients,steps)
        cr.delete(DatabaseContract.Recipe.CONTENT_URI,null,null);
        cr.delete(DatabaseContract.Ingredient.CONTENT_URI,null,null);
        cr.delete(DatabaseContract.Step.CONTENT_URI,null,null);
    }
}
