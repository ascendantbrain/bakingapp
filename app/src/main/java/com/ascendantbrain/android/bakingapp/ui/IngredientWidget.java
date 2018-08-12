package com.ascendantbrain.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Ingredient;
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract;
import com.ascendantbrain.android.bakingapp.utils.FormatHelper;

import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        String ingredients = getIngredients(context);
        views.setTextViewText(R.id.ingredient_list, ingredients);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static String getIngredients(Context context) {

        Context appContext = context.getApplicationContext();
        Cursor cursor = appContext.getContentResolver().query(
                DatabaseContract.Ingredient.buildRecipeIngredientsUri(1),
                Ingredient.getProjection(),null,null,
                Ingredient.getSortOrder());
        if(cursor == null) return "";
        String[] ingredients = new String[cursor.getCount()];

        try{
            int row = 0;
            while(cursor.moveToNext()) {
                Ingredient ingredient = Ingredient.fromCursor(cursor);
                if(ingredient!=null) ingredients[row] = String.format(Locale.US,"\u25CF  %s %s %s",
                        FormatHelper.formatDouble(Locale.US,ingredient.quantity),
                        ingredient.measure,
                        ingredient.ingredient);
                row++;
            }
        } catch (Exception e) { Log.e("IngredientWidget",Log.getStackTraceString(e)); }
        finally { cursor.close(); }

        return TextUtils.join("\n",ingredients);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

