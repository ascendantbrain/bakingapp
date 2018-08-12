package com.ascendantbrain.android.bakingapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Recipe;
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Ingredient;
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract.Step;


public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "easyrecipe.db";

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + Recipe.TABLE_NAME + " (" +
                Recipe._ID + " INTEGER PRIMARY KEY," +
                Recipe.COLUMN_RECIPE_ID + " TEXT UNIQUE NOT NULL," +
                Recipe.COLUMN_NAME + " TEXT NOT NULL," +
                Recipe.COLUMN_SERVINGS + " INTEGER NOT NULL," +
                Recipe.COLUMN_IMAGEURL + " TEXT" +
                " );";


        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + Ingredient.TABLE_NAME + " (" +
                Ingredient._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Ingredient.COLUMN_RECIPE_ID + " TEXT NOT NULL, " +
                Ingredient.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                Ingredient.COLUMN_MEASURE + " TEXT NOT NULL, " +
                Ingredient.COLUMN_QUANTITY + " REAL NOT NULL," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + Ingredient.COLUMN_RECIPE_ID + ") REFERENCES " +
                Recipe.TABLE_NAME + " (" + Recipe._ID + ")," +

                " UNIQUE (" + Ingredient.COLUMN_RECIPE_ID + ", " +
                Ingredient.COLUMN_INGREDIENT + ") ON CONFLICT REPLACE" +
                " );";


        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + Step.TABLE_NAME + " (" +
                Step._ID + " INTEGER PRIMARY KEY," +
                Step.COLUMN_RECIPE_ID + " TEXT NOT NULL, " +
                Step.COLUMN_STEP_ID + " TEXT NOT NULL, " +
                Step.COLUMN_DESCRIPTION_SHORT + " TEXT NOT NULL, " +
                Step.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                Step.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                Step.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + Step.COLUMN_RECIPE_ID + ") REFERENCES " +
                Recipe.TABLE_NAME + " (" + Recipe._ID + "), " +

                " UNIQUE (" + Step.COLUMN_RECIPE_ID + ", " +
                Step.COLUMN_STEP_ID + ") ON CONFLICT REPLACE" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data.
        // Upgrade policy is to discard the data and start over.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Recipe.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Ingredient.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Step.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}