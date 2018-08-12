package com.ascendantbrain.android.bakingapp.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.PATH_INGREDIENT;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.PATH_STEP;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step;

import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.PATH_RECIPE;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.COLUMN_RECIPE_ID;
import static com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step.COLUMN_STEP_ID;

public class DatabaseProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RecipeDbHelper mDbHelper;

    // Supported URIs
    // recipe               - return all recipes
    // ingredient/recipe/#  - return all ingredients for a recipe
    // step/#               - return specific step
    // step/recipe/#        - list steps for a given recipe

    static final int RECIPE = 100;                      // recipe
    static final int RECIPE_ID = 101;                   // recipe/#
    static final int INGREDIENT = 200;                  // ingredient
    static final int INGREDIENT_ID = 201;               // ingredient/#
    static final int INGREDIENT_WITH_RECIPE_ID = 202;   // ingredient/recipe/#
    static final int STEP = 300;                     // step
    static final int STEP_ID = 301;                     // step/#
    static final int STEP_WITH_RECIPE_ID = 302;         // step/recipe/#

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_RECIPE, RECIPE);
        matcher.addURI(authority, PATH_RECIPE + "/#",RECIPE_ID);
        matcher.addURI(authority, PATH_INGREDIENT, INGREDIENT);
        matcher.addURI(authority, PATH_INGREDIENT + "/" + PATH_RECIPE + "/#", INGREDIENT_WITH_RECIPE_ID);
        matcher.addURI(authority, PATH_STEP, STEP);
        matcher.addURI(authority, PATH_STEP + "/#", STEP_ID);
        matcher.addURI(authority, PATH_STEP + "/" + PATH_RECIPE + "/#", STEP_WITH_RECIPE_ID);

        return matcher;
    }

    // table.column = ?
    private static String buildSingleColumnSelection(String table, String column) {
        return table + "." + column + " = ? ";
    }

    private static String[] buildRecipeSelectionArgs(int recipeId) {
        return new String[]{Integer.toString(recipeId)};
    }

    private static String[] buildStepSelectionArgs(int stepId) {
        return new String[]{Integer.toString(stepId)};
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    /**
     * @param uri content uri
     * @return MIME type for requested content uri
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RECIPE:
                return Recipe.CONTENT_TYPE;
            case RECIPE_ID:
                return Recipe.CONTENT_ITEM_TYPE;
            case INGREDIENT_WITH_RECIPE_ID:
                return Ingredient.CONTENT_TYPE;
            case STEP_ID:
                return Step.CONTENT_ITEM_TYPE;
            case STEP_WITH_RECIPE_ID:
                return Step.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "recipe" --> all recipes
            case RECIPE:
            {
                retCursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.Recipe.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECIPE_ID:
            {
                retCursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.Recipe.TABLE_NAME,
                        projection,
                        buildSingleColumnSelection(Recipe.TABLE_NAME,Recipe.COLUMN_RECIPE_ID),
                        buildRecipeSelectionArgs(Recipe.getRecipeIdFromUri(uri)),
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "ingredient/recipe/#"  --> all ingredients for a given recipe
            case INGREDIENT_WITH_RECIPE_ID: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.Ingredient.TABLE_NAME,
                        projection,
                        buildSingleColumnSelection(Ingredient.TABLE_NAME,Ingredient.COLUMN_RECIPE_ID),
                        buildRecipeSelectionArgs(Ingredient.getRecipeIdFromUri(uri)),
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "step/#"  --> info for a specific step id
            case STEP_ID: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.Step.TABLE_NAME,
                        projection,
                        buildSingleColumnSelection(Step.TABLE_NAME, COLUMN_STEP_ID),
                        buildStepSelectionArgs(Step.getStepIdFromUri(uri)),
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "step/recipe/#"  --> all steps for a given recipe
            case STEP_WITH_RECIPE_ID: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        DatabaseContract.Step.TABLE_NAME,
                        projection,
                        buildSingleColumnSelection(Step.TABLE_NAME, COLUMN_RECIPE_ID),
                        buildStepSelectionArgs(Step.getRecipeIdFromUri(uri)),
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if(context!=null) retCursor.setNotificationUri(context.getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RECIPE: {
                long _id = db.insert(Recipe.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Recipe.buildRecipeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case INGREDIENT: {
                long _id = db.insert(Ingredient.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Ingredient.buildIngredientUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STEP: {
                long _id = db.insert(Step.TABLE_NAME, null, values);
                if ( _id > 0 ){
                    int recipeId = values.getAsInteger(COLUMN_RECIPE_ID);
                    int stepId = values.getAsInteger(COLUMN_STEP_ID);
                    returnUri = Step.buildStepUri(recipeId,stepId);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if(context!=null) context.getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // if selection is null then delete all rows and return the deletion count
        if ( null == selection ) selection = "1";

        switch (match) {
            case RECIPE:
                rowsDeleted = db.delete(
                        Recipe.TABLE_NAME, selection, selectionArgs);
                break;
            case INGREDIENT:
                rowsDeleted = db.delete(
                        Ingredient.TABLE_NAME, selection, selectionArgs);
                break;
            case STEP:
                rowsDeleted = db.delete(
                        Step.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            Context context = getContext();
            if(context!=null) context.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case RECIPE:
                rowsUpdated = db.update(Recipe.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INGREDIENT:
                rowsUpdated = db.update(Ingredient.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STEP:
                rowsUpdated = db.update(Step.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            Context context = getContext();
            if(context!=null) context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Context context = getContext();

        String table = null;
        switch (match) {
            case RECIPE:
                table = Recipe.TABLE_NAME;  // fall through
            case INGREDIENT:
                if(null == table) table = Ingredient.TABLE_NAME;  // fall through
            case STEP:
                if(null == table) table = Step.TABLE_NAME;

                // perform database operations
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(table, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if(context!=null) context.getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // Method specifically to assist the testing framework in running smoothly. Read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}