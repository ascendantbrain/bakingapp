package com.ascendantbrain.android.bakingapp.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Ingredient;
import com.ascendantbrain.android.bakingapp.utils.FormatHelper;

import java.util.Locale;

public class IngredientAdapter extends CursorRecyclerViewAdapter<IngredientAdapter.IngredientViewHolder> {

    public IngredientAdapter() {
        super();
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder viewHolder, Cursor cursor) {
        Ingredient ingredient = Ingredient.fromCursor(cursor);
        viewHolder.bind(ingredient);
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        // data elements
        private TextView mText;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            // find views to hold
            mText = itemView.findViewById(R.id.ingredient_text);
        }

        public void bind(Ingredient ingredient) {

            String ingredientText = String.format(Locale.US,"\u25CF  %s %s %s",
                    FormatHelper.formatDouble(Locale.US,ingredient.quantity),
                    ingredient.measure,
                    ingredient.ingredient);

            mText.setText(ingredientText);
        }
    }
}
