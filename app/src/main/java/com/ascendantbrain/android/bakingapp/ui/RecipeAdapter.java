package com.ascendantbrain.android.bakingapp.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.ImageHelper;
import com.ascendantbrain.android.bakingapp.model.Recipe;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class RecipeAdapter extends CursorRecyclerViewAdapter<RecipeAdapter.RecipeViewHolder> {

    private ItemClickedListener<Recipe> mOnClickedListener;

    public RecipeAdapter(ItemClickedListener<Recipe> listener) {
        super();
        this.mOnClickedListener = listener;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder viewHolder, Cursor cursor) {
        Recipe recipe = Recipe.fromCursor(cursor);
        viewHolder.bind(recipe);
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view,mOnClickedListener);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // data elements
        private ImageView mImage;
        private TextView mTitle;
        private TextView mSubtitle;
        private Recipe mRecipe;


        // OnClickListener ref
        private WeakReference<ItemClickedListener<Recipe>> mOnClickListenerRef;


        public RecipeViewHolder(View itemView, ItemClickedListener<Recipe> listener) {
            super(itemView);

            mOnClickListenerRef = new WeakReference<>(listener);

            // find views to hold
            mImage = itemView.findViewById(R.id.card_image);
            mTitle = itemView.findViewById(R.id.card_title);
            mSubtitle = itemView.findViewById(R.id.card_subtitle);

            itemView.setOnClickListener(this);
        }

        public void bind(Recipe recipe) {
            Context context = mImage.getContext();

            String subtitle = String.format(Locale.US,"Serves: %d",recipe.servings);

            mRecipe = recipe;
            mTitle.setText(recipe.name);
            mSubtitle.setText(subtitle);

            if(recipe.imageUrl.isEmpty()) {
                GlideApp.with(context)
                        .load(ImageHelper.getRecipeImage(recipe.id))
                        .centerCrop()
                        .into(mImage);
            } else {
                GlideApp.with(context)
                        .load(Uri.parse(recipe.imageUrl))
                        .centerCrop()
                        .into(mImage);
            }
        }

        @Override
        public void onClick(View v) {
            ItemClickedListener<Recipe> listener = mOnClickListenerRef.get();
            if(listener!=null) listener.onClick(mRecipe);
        }
    }
}
