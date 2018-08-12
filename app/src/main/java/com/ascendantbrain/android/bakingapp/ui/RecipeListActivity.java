package com.ascendantbrain.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Recipe;
import com.ascendantbrain.android.bakingapp.webapi.DataService;

import java.lang.ref.WeakReference;

/**
 * Entry point activity containing the recipe cards.
 *
 * Details are available when a specific recipe is selected.
 */
public class RecipeListActivity extends AppCompatActivity {
    private static final String TAG = "RecipeListActivity";

    private static final int RECIPE_LOADER_ID = 100;
    private static boolean sFreshDataReqeusted = false;

    private RecipeAdapter mAdapter;
    private boolean mTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle().toString().toUpperCase());
        setSupportActionBar(toolbar);

        if (!sFreshDataReqeusted) {
            DataService.asyncFetch(this);
            sFreshDataReqeusted = true;
        }

        // is device a tablet
        mTablet = getResources().getBoolean(R.bool.isTablet);

        // setup adapter before recycler view and loader init
        mAdapter = new RecipeAdapter(new LocalClickedListener());

        // setup recycler view
        RecyclerView recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);

        // Prepare the loader.  Re-connect with an existing one or start a new one.
        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, new LoaderCallbacks(this));


    }

    private static class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        private final WeakReference<RecipeListActivity> mActivityRef;

        LoaderCallbacks(RecipeListActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Context context = mActivityRef.get();
            if (null == context) return null;

            switch (id) {
                case RECIPE_LOADER_ID: {
                    return new CursorLoader(
                            context,
                            Recipe.getContentUri(),
                            Recipe.getProjection(),
                            null,
                            null,
                            Recipe.getSortOrder());
                }
                default:
                    throw new UnsupportedOperationException("Unknown load request: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            RecipeListActivity activity = mActivityRef.get();
            if (null == activity) return;

            int loaderId = loader.getId();
            switch (loaderId) {
                case RECIPE_LOADER_ID: {
                    if (cursor == null || !cursor.moveToFirst()) {
                        activity.fetchData(loaderId);
                        return;
                    }
                    activity.onLoad(loaderId, cursor);
                    break;
                }
                default: {
                }
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
            RecipeListActivity activity = mActivityRef.get();
            if (null == activity) return;
            int loaderId = loader.getId();
            activity.onLoad(loaderId, null);
        }

    }

    private void onLoad(int loaderId, @Nullable Cursor data) {
        switch (loaderId) {
            case RECIPE_LOADER_ID: {
                mAdapter.swapCursor(data);
                break;
            }
            default: {
            }
        }
    }

    private void fetchData(int loaderId) {
        switch (loaderId) {
            case RECIPE_LOADER_ID: {
                DataService.asyncFetch(RecipeListActivity.this);
                break;
            }
            default: {
            }
        }
    }


    private class LocalClickedListener implements ItemClickedListener<Recipe> {
        LocalClickedListener() {
        }

        @Override
        public void onClick(Recipe recipe) {
            onRecipeClicked(recipe);
        }
    }

    private void onRecipeClicked(Recipe recipe) {
        Intent intent = StepListActivity.getLauncherIntent(this,recipe.id);
        startActivity(intent);
    }
}