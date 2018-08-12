package com.ascendantbrain.android.bakingapp.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the recipe database.
 */
public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.ascendantbrain.easyrecipe";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_INGREDIENT = "ingredient";
    public static final String PATH_STEP = "step";

    public static final class Recipe implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        // Table name
        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_RECIPE_ID = "recipeid";
        public static final String COLUMN_NAME = "recipename";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGEURL = "imageurl";

        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getRecipeIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(1);
            return Integer.valueOf(id);
        }

        public static final String SORT_ORDER = COLUMN_NAME + " ASC";

        public static String[] PROJECTION = {
                BaseColumns._ID,
                COLUMN_RECIPE_ID,
                COLUMN_NAME,
                COLUMN_SERVINGS,
                COLUMN_IMAGEURL
        };

        // Column indices - must match order within projection
        public static final int INDEX_ID = 0;
        public static final int INDEX_RECIPE_ID = 1;
        public static final int INDEX_NAME = 2;
        public static final int INDEX_SERVINGS = 3;
        public static final int INDEX_IMAGEURL = 4;

        public static ContentValues buildContentValues(int recipe_id, String name, int servings, String imageUrl) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_ID,recipe_id);
            values.put(COLUMN_NAME,name);
            values.put(COLUMN_SERVINGS,servings);
            values.put(COLUMN_IMAGEURL,imageUrl);
            return values;
        }
    }

    public static final class Ingredient implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENT;

        public static final String TABLE_NAME = "ingredient";

        // Table columns
        public static final String COLUMN_RECIPE_ID = "ingredient_recipe_id";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";

        // ingredient/id
        public static Uri buildIngredientUri(long ingredientId) {
            Uri.Builder builder = CONTENT_URI.buildUpon().appendPath(PATH_RECIPE);
            return ContentUris.appendId(builder,ingredientId).build();
        }

        // ingredient/id
        public static int getIngredientIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(1);
            return Integer.valueOf(id);
        }

        // ingredient/recipe/id
        public static Uri buildRecipeIngredientsUri(long recipeId) {
            Uri.Builder builder = CONTENT_URI.buildUpon().appendPath(PATH_RECIPE);
            return ContentUris.appendId(builder,recipeId).build();
        }

        // ingredient/recipe/id
        public static int getRecipeIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(2);
            return Integer.valueOf(id);
        }

        public static String[] PROJECTION = {
                BaseColumns._ID,
                COLUMN_RECIPE_ID,
                COLUMN_INGREDIENT,
                COLUMN_QUANTITY,
                COLUMN_MEASURE
        };

        // Column indices - must match order within projection
        public static final int INDEX_ID = 0;
        public static final int INDEX_RECIPE_ID = 1;
        public static final int INDEX_INGREDIENT = 2;
        public static final int INDEX_QUANTITY = 3;
        public static final int INDEX_MEASURE = 4;

        public static ContentValues buildContentValues(int recipe_id, String ingredient, double quantity, String measure) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_ID,recipe_id);
            values.put(COLUMN_INGREDIENT,ingredient);
            values.put(COLUMN_QUANTITY,quantity);
            values.put(COLUMN_MEASURE,measure);
            return values;
        }
    }

    public static final class Step implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEP;

        public static final String TABLE_NAME = "step";

        // Table columns
        public static final String COLUMN_RECIPE_ID = "step_recipe_id";
        public static final String COLUMN_STEP_ID = "step_id";
        public static final String COLUMN_DESCRIPTION_SHORT = "short_description";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "video_url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

        public static String[] PROJECTION = {
                BaseColumns._ID,
                COLUMN_RECIPE_ID,
                COLUMN_STEP_ID,
                COLUMN_DESCRIPTION_SHORT,
                COLUMN_DESCRIPTION,
                COLUMN_VIDEO_URL,
                COLUMN_THUMBNAIL_URL
        };

        // Column indices - must match order within projection
        public static final int INDEX_ID = 0;
        public static final int INDEX_RECIPE_ID = 1;
        public static final int INDEX_STEP_ID = 2;
        public static final int INDEX_DESCRIPTION_SHORT = 3;
        public static final int INDEX_DESCRIPTION = 4;
        public static final int INDEX_VIDEO_URL = 5;
        public static final int INDEX_THUMBNAIL_URL = 6;


        public static int getRecipeIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(2);
            return Integer.valueOf(id);
        }

        public static int getStepIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(1);
            return Integer.valueOf(id);
        }

        public static Uri buildRecipeStepsUri(long id) {
            Uri.Builder builder = CONTENT_URI.buildUpon().appendPath(PATH_RECIPE);
            return ContentUris.appendId(builder,id).build();
        }

        public static Uri buildStepUri(long recipeId, long stepId) {
            Uri.Builder builder = CONTENT_URI.buildUpon().appendPath(PATH_RECIPE);
            builder =  ContentUris.appendId(builder,recipeId);
            return ContentUris.appendId(builder,stepId).build();
        }


        public static ContentValues buildContentValues(int recipe_id, int step_id, String short_desc, String desc, String videoURL,String thumbnailURL) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_ID,recipe_id);
            values.put(COLUMN_STEP_ID,step_id);
            values.put(COLUMN_DESCRIPTION_SHORT,short_desc);
            values.put(COLUMN_DESCRIPTION,desc);
            values.put(COLUMN_VIDEO_URL,videoURL);
            values.put(COLUMN_THUMBNAIL_URL,thumbnailURL);
            return values;
        }
    }
}
