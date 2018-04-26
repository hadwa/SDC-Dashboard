package com.example.hadwa.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

    public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.RecyclerViewHolder> {

        private final Context mCtx;
       // LinkedHashSet<String> Markers = new LinkedHashSet<String>();
       // List<String> uniqueStrings = new ArrayList<String>(Markers);
        List<String> Markers;

        public RecyclerListAdapter(Context mCtx,  List<String>  Markers) {
            this.mCtx = mCtx;
            this.Markers =  Markers;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.recycler_layout, null);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            String Marker=Markers.get(position);
            holder.CardText.setText(Marker.toString());
        }

        @Override
        public int getItemCount() {
            return Markers.size();
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView CardText;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                CardText = itemView.findViewById(R.id.CardText);
            }

        }


        public void onMove(int fromPos, int toPos) {
            if (fromPos < toPos) {
                for (int i = fromPos; i < toPos; i++) {
                Collections.swap(Markers, i, i + 1);
                }
            } else {
                for (int i = fromPos; i > toPos; i--) {
                    Collections.swap(Markers, i, i - 1);

                }
            }
            notifyItemMoved(fromPos, toPos);
        }
    }

