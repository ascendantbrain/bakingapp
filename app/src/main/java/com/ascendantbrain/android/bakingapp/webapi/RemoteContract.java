package com.ascendantbrain.android.bakingapp.webapi;

import android.content.ContentValues;

import com.ascendantbrain.android.bakingapp.provider.DatabaseContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RemoteContract {

    class Recipe {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("ingredients")
        @Expose
        public List<Ingredient> ingredients = null;
        @SerializedName("steps")
        @Expose
        public List<Step> steps = null;
        @SerializedName("servings")
        @Expose
        public int servings;
        @SerializedName("image")
        @Expose
        public String imageUrl;

        public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
            this.id = id;
            this.name = name;
            this.ingredients = ingredients;
            this.steps = steps;
            this.servings = servings;
            this.imageUrl = image;
        }

        public ContentValues getValues() {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Recipe.COLUMN_RECIPE_ID, id);
            values.put(DatabaseContract.Recipe.COLUMN_NAME, name);
            values.put(DatabaseContract.Recipe.COLUMN_SERVINGS, servings);
            values.put(DatabaseContract.Recipe.COLUMN_IMAGEURL, imageUrl);
            return values;
        }
    }

    public class Ingredient {
        @SerializedName("quantity")
        @Expose
        public double quantity;
        @SerializedName("measure")
        @Expose
        public String measure;
        @SerializedName("ingredient")
        @Expose
        public String ingredient;

        public Ingredient(double quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
        }

        public ContentValues getValues(int recipeId) {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Ingredient.COLUMN_RECIPE_ID, recipeId);
            values.put(DatabaseContract.Ingredient.COLUMN_QUANTITY, quantity);
            values.put(DatabaseContract.Ingredient.COLUMN_MEASURE, measure);
            values.put(DatabaseContract.Ingredient.COLUMN_INGREDIENT, ingredient);
            return values;
        }

    }

    public class Step {
        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("shortDescription")
        @Expose
        public String shortDescription;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("videoURL")
        @Expose
        public String videoURL;
        @SerializedName("thumbnailURL")
        @Expose
        public String thumbnailURL;

        public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
            this.id = id;
            this.shortDescription = shortDescription;
            this.description = description;
            this.videoURL = videoURL;
            this.thumbnailURL = thumbnailURL;
        }

        public ContentValues getValues(int recipeId) {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Step.COLUMN_RECIPE_ID, recipeId);
            values.put(DatabaseContract.Step.COLUMN_STEP_ID, id);
            values.put(DatabaseContract.Step.COLUMN_DESCRIPTION_SHORT, shortDescription);
            values.put(DatabaseContract.Step.COLUMN_DESCRIPTION, description);
            values.put(DatabaseContract.Step.COLUMN_VIDEO_URL, videoURL);
            values.put(DatabaseContract.Step.COLUMN_THUMBNAIL_URL, thumbnailURL);
            return values;
        }
    }

}
