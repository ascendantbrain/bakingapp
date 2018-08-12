package com.ascendantbrain.android.bakingapp.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascendantbrain.android.bakingapp.R;
import com.ascendantbrain.android.bakingapp.model.Step;

import java.lang.ref.WeakReference;

public class StepAdapter extends CursorRecyclerViewAdapter<StepAdapter.StepViewHolder> {

    private ItemClickedListener<Step> mOnClickedListener;

    public StepAdapter(ItemClickedListener<Step> listener) {
        super();
        this.mOnClickedListener = listener;
    }

    @Override
    public void onBindViewHolder(StepViewHolder viewHolder, Cursor cursor) {
        Step step = Step.Companion.fromCursor(cursor);
        viewHolder.bind(step);
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view,mOnClickedListener);
    }

    static class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // data elements
        private TextView mTitle;
        private Step mStep;


        // OnClickListener ref
        private WeakReference<ItemClickedListener<Step>> mOnClickListenerRef;


        public StepViewHolder(View itemView, ItemClickedListener<Step> listener) {
            super(itemView);

            mOnClickListenerRef = new WeakReference<ItemClickedListener<Step>>(listener);

            // find views to hold
            mTitle = itemView.findViewById(R.id.step_title);

            itemView.setOnClickListener(this);
        }

        public void bind(Step step) {

            mStep = step;
            int id = step.getId();
            String indexHeader = (id == 0) ? "" : String.valueOf(id)+". ";
            mTitle.setText(String.format("%s%s", indexHeader, step.getShortDescription()));
        }

        @Override
        public void onClick(View v) {
            ItemClickedListener<Step> listener = mOnClickListenerRef.get();
            if(listener!=null) listener.onClick(mStep);
        }
    }
}
