package com.ascendantbrain.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Step;
import com.ascendantbrain.android.bakingapp.provider.DatabaseContract;

import java.lang.ref.WeakReference;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepListActivity}.
 */
public class StepDetailActivity extends AppCompatActivity implements NavigationListener {

    private static final String KEY_RECIPE_ID = "key_recipe_id";
    private static final String KEY_STEP_ID = "key_step_id";
    private static final int STEPS_LOADER_ID = 8;

    private FragmentProvider mFragmentProvider;
    private int mRecipeId = -1;

    public static Intent buildStartIntent(Context context, int recipeId, int stepId) {
        Intent intent = new Intent(context,StepDetailActivity.class);
        intent.putExtra(KEY_RECIPE_ID,recipeId);
        intent.putExtra(KEY_STEP_ID,stepId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null ) {
            mRecipeId = extras.getInt(KEY_RECIPE_ID,0);
            int stepId = extras.getInt(KEY_STEP_ID,0);
            mFragmentProvider = new FragmentProvider(stepId);
        } else {
            mFragmentProvider = new FragmentProvider(0);
        }

        // Prepare the loader.  Re-connect with an existing one or start a new one.
        getSupportLoaderManager().initLoader(STEPS_LOADER_ID, null, new LoaderCallbacks(this));
    }

    private static class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        private final WeakReference<StepDetailActivity> mActivityRef;

        LoaderCallbacks(StepDetailActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            StepDetailActivity activity = mActivityRef.get();
            if (null == activity) return null;

            switch (id) {
                case STEPS_LOADER_ID: {
                    return new CursorLoader(
                            activity,
                            DatabaseContract.Step.buildRecipeStepsUri(activity.mRecipeId),
                            Step.Companion.getProjection(),
                            null,
                            null,
                            Step.Companion.getSortOrder());
                }
                default:
                    throw new UnsupportedOperationException("Unknown load request: " + id);
            }
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            StepDetailActivity activity = mActivityRef.get();
            if (null == activity) return;

            int loaderId = loader.getId();
            switch (loaderId) {
                case STEPS_LOADER_ID: {
                    activity.mFragmentProvider.swap(cursor);
                    activity.onLoad();
                    break;
                }
                default: { }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader loader) {
            StepDetailActivity activity = mActivityRef.get();
            if(activity!=null) {
                activity.mFragmentProvider.swap(null);
            }
        }
    }

    @Override
    public void onNextClicked() {
        Fragment fragment = mFragmentProvider.getNext();
        if(fragment == null)  return;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    @Override
    public void onPreviousClicked() {
        Fragment fragment = mFragmentProvider.getPrevious();
        if(fragment == null)  return;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    private void onLoad() {
        Fragment fragment = mFragmentProvider.get();
        if(fragment == null)  return;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class FragmentProvider {
        private Cursor mCursor;
        private int mPosition = -1;

        FragmentProvider(int position) {
            mPosition = position;
        }

        StepDetailFragment get() {
            return get(mPosition);
        }

        StepDetailFragment get(int position) {
            if(mCursor!=null
                    && position >= 0
                    && position < mCursor.getCount()
                    && mCursor.moveToPosition(position)) {
                mPosition = position;
                Step step = Step.Companion.fromCursor(mCursor);
                return StepDetailFragment.getInstance(step);
            }
            return null;
        }

        StepDetailFragment getNext() {
            int requestedPosition = mPosition+1;
            return get(requestedPosition);
        }

        StepDetailFragment getPrevious() {
            int requestedPosition = mPosition-1;
            return get(requestedPosition);
        }

        Cursor swap(Cursor c) {
            Cursor old = mCursor;
            mCursor = c;
            return old;
        }
    }
}
