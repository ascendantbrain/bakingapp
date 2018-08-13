package com.ascendantbrain.android.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ascendantbrain.android.bakingapp.R;

import com.ascendantbrain.android.bakingapp.model.Ingredient;
import com.ascendantbrain.android.bakingapp.model.Recipe;
import com.ascendantbrain.android.bakingapp.model.Step;
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract;

import java.lang.ref.WeakReference;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends AppCompatActivity {

    private static final String KEY_RECIPE_ID = "key_recipe_id";
    private int mRecipeId = -1;
    private static final String TAG_STEP_DETAIL_FRAGMENT = "tag_step_detail_fragment";
    private Step mSelectedStep = null;

    private static final int INGREDIENT_LOADER_ID = 200;
    private static final int STEP_LOADER_ID = 300;

    private StepAdapter mStepAdapter;
    private IngredientAdapter mIngredientAdapter;

    public static Intent getLauncherIntent(Context context, int recipeId) {
        Intent intent = new Intent(context,StepListActivity.class);
        intent.putExtra(KEY_RECIPE_ID,recipeId);
        return intent;
    }

    /** indicates if activity in two-pane mode, i.e. running on a tablet device. */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(!extras.isEmpty() && extras.containsKey(KEY_RECIPE_ID)) {
            mRecipeId = extras.getInt(KEY_RECIPE_ID);
            updateAppWidgetContents(mRecipeId);
        } else finish();

        setContentView(R.layout.activity_step_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = getRecipeName(this,mRecipeId).toUpperCase();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) actionBar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }

        // setup adapter before recycler view and loader init
        mIngredientAdapter = new IngredientAdapter();
        mStepAdapter = new StepAdapter(new StepClickedListener());

        // setup ingredient recycler view
        RecyclerView ingredientRecyclerView = findViewById(R.id.ingredient_list);
        assert ingredientRecyclerView != null;
        ingredientRecyclerView.setAdapter(mIngredientAdapter);
        ingredientRecyclerView.setHasFixedSize(true);

        // setup step recycler view
        RecyclerView stepRecyclerView = findViewById(R.id.step_list);
        assert stepRecyclerView != null;
        stepRecyclerView.setAdapter(mStepAdapter);
        stepRecyclerView.setHasFixedSize(true);
        //Drawable divider = getDrawable(R.drawable.divider);
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        //if(divider!=null) dividerDecoration.setDrawable(divider);
        stepRecyclerView.addItemDecoration(dividerDecoration);

        // Prepare the loaders.  Re-connect with an existing one or start a new one.
        Bundle args = new Bundle();
        args.putInt(KEY_RECIPE_ID,mRecipeId);
        getSupportLoaderManager().initLoader(STEP_LOADER_ID, args, new StepListActivity.LoaderCallbacks(this));
        getSupportLoaderManager().initLoader(INGREDIENT_LOADER_ID, args, new StepListActivity.LoaderCallbacks(this));

        if(mTwoPane && savedInstanceState != null) {
            // Find existing fragment using its tag
            final FragmentManager fragmentManager = getSupportFragmentManager();
            StepDetailFragment fragment = (StepDetailFragment) fragmentManager
                    .findFragmentByTag(TAG_STEP_DETAIL_FRAGMENT);
            // Add fragment to activity using a fragment transaction
            if(fragment==null) return;  // fragment hasn't been created
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, fragment, TAG_STEP_DETAIL_FRAGMENT)
                    .commit();
        }
    }

    private void updateAppWidgetContents(int recipeId) {
        Context context = StepListActivity.this;
        SharedPrefUtil.setCurrentRecipeId(context,recipeId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,IngredientWidget.class));

        IngredientWidget.updateAppWidgets(context,appWidgetManager,appWidgetIds);
    }

    private static class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        private final WeakReference<StepListActivity> mActivityRef;

        LoaderCallbacks(StepListActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Context context = mActivityRef.get();
            if (null == context) return null;
            int recipeId = args.getInt(KEY_RECIPE_ID);

            switch (id) {
                case STEP_LOADER_ID: {
                    Uri uri = DatabaseContract.Step.buildRecipeStepsUri(recipeId);
                    return new CursorLoader(
                            context,
                            uri,
                            Step.Companion.getProjection(),
                            null,
                            null,
                            Step.Companion.getSortOrder());
                }
                case INGREDIENT_LOADER_ID: {
                    Uri uri = DatabaseContract.Ingredient.buildRecipeIngredientsUri(recipeId);
                    return new CursorLoader(
                            context,
                            uri,
                            Ingredient.getProjection(),
                            null,
                            null,
                            Ingredient.getSortOrder());
                }
                default:
                    throw new UnsupportedOperationException("Unknown load request: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            StepListActivity activity = mActivityRef.get();
            if (null == activity) return;

            int loaderId = loader.getId();
            switch (loaderId) {
                case INGREDIENT_LOADER_ID: {
                    if (cursor == null || !cursor.moveToFirst()) return;
                    activity.onIngredientsLoad(cursor);
                    break;
                }
                case STEP_LOADER_ID: {
                    if (cursor == null || !cursor.moveToFirst()) return;
                    activity.onStepsLoad(cursor);
                    break;
                }
                default: {
                }
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
            StepListActivity activity = mActivityRef.get();
            if (null == activity) return;
            int loaderId = loader.getId();
            switch (loaderId) {
                case INGREDIENT_LOADER_ID: {
                    activity.onIngredientsLoad(null);
                    break;
                }
                case STEP_LOADER_ID: {
                    activity.onStepsLoad(null);
                    break;
                }
                default: {
                }
            }
        }

    }

    private void onIngredientsLoad(@Nullable Cursor data) {
        mIngredientAdapter.swapCursor(data);
    }

    private void onStepsLoad(@Nullable Cursor data) {
        mStepAdapter.swapCursor(data);
    }

    private class StepClickedListener implements ItemClickedListener<Step> {
        StepClickedListener() {
        }

        @Override
        public void onClick(Step step) {
            onStepClicked(step);
        }
    }

    private void onStepClicked(Step step) {
        if(mTwoPane) {
            StepDetailFragment fragment = StepDetailFragment.getInstance(step);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.step_detail_container,fragment,TAG_STEP_DETAIL_FRAGMENT)
                    .commit();
        } else {
            Intent intent = StepDetailActivity.buildStartIntent(this,mRecipeId,step.getId());
            startActivity(intent);
        }

    }

    private String getRecipeName(Context context,int id) {
        Uri uri = DatabaseContract.Recipe.buildRecipeUri(id);
        Cursor c = context.getContentResolver().query(uri,Recipe.getProjection(),null,null,null);
        if(c != null && c.moveToFirst()) {
            return c.getString(DatabaseContract.Recipe.INDEX_NAME);
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
