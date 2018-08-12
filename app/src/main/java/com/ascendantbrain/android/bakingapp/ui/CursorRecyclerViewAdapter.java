package com.ascendantbrain.android.bakingapp.ui;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Simple cursor adapter class for use with recycler view.
 * Observing dataset changes for the cursor is handled
 * external to adapter (e.g. using cursor loader).
 */
public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private boolean mDataValid = false;
    private Cursor mCursor = null;
    private int mRowIdIndex = -1;

    public CursorRecyclerViewAdapter() { }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdIndex);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        // handle invalid data
        if (!mDataValid) throw new IllegalStateException("Unable to bind invalid data");

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Cursor destination unreachable. p=" + position);
        }

        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Accepts a new cursor and returns the existing cursor.
     * Closing the old cursor is handled externally (automatically if using a cursor loader).
     */
    public Cursor swapCursor(@Nullable Cursor newCursor) {
        // return null if passed the existing cursor
        if (newCursor == mCursor) return null;

        // save old cursor for the return value
        final Cursor oldCursor = mCursor;

        // perform cursor swap then set data validity and row id index
        mCursor = newCursor;
        if (mCursor != null) {
            mDataValid = true;
            mRowIdIndex = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            mRowIdIndex = -1;
            notifyDataSetChanged();
        }
        return oldCursor;
    }
}

