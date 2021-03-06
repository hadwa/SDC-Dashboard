package com.example.hadwa.myapplication;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.Collections;

/**
 * Created by hadwa on 4/22/2018.
 */

class ItemDragHelper extends ItemTouchHelper.Callback {
    private final RecyclerListAdapter itemHelper;
    public static final float ALPHA_FULL = 1.0f;

    public ItemDragHelper(RecyclerListAdapter touchHelper) {
        this.itemHelper = touchHelper;

    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, ItemTouchHelper.RIGHT); // Flag Left is not used
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction==ItemTouchHelper.RIGHT) {
            Collections.swap(itemHelper.Markers,viewHolder.getAdapterPosition(), viewHolder.getAdapterPosition()-1);
            Log.v("brownies", String.valueOf(itemHelper.Markers.size()));
        }

    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ListItemTouchHelper) {
                // Let the view holder know that this item is being moved or dragged
                ListItemTouchHelper itemViewHolder = (ListItemTouchHelper) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        if (viewHolder instanceof ListItemTouchHelper) {
            // Tell the view holder it's time to restore the idle state
            ListItemTouchHelper itemViewHolder = (ListItemTouchHelper) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}
