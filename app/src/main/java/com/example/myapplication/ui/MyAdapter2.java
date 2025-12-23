package com.example.myapplication.ui;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int TYPE_DAY = 0;
    static final int TYPE_NUMBER = 1;
    //private MyAdapter2.OnItemButtonClickListener listener; //listener dinamico per il bottone Modifica che avrà ogni oggetto della lista
    private List<ListItem> items;

    private OnItemDeleteListener listener;


    public MyAdapter2(Map<String, List<String>> map, OnItemDeleteListener listener) {

        this.items = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            this.items.add(new DayItem(entry.getKey()));// aggiungi la chiave
            for (String e : entry.getValue()) {
                items.add(new EntryItem(e, entry.getKey()));
            }
        }
        this.listener=listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType==TYPE_DAY){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day, parent, false);
            return new DayViewHolder(view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_entry, parent, false);
            return new NumberViewHolder(view, listener, items);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DayViewHolder) {
            ((DayViewHolder) holder).tvDay.setText(items.get(position).getString());
        } else if (holder instanceof NumberViewHolder) {
            ((NumberViewHolder) holder).tvNumber.setText(items.get(position).getString());
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }



    public void addElement(Map<String, List<String> > map){
        items.clear();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {

            this.items.add(new DayItem(entry.getKey()));// aggiungi la chiave
            for (String e : entry.getValue()) {
                items.add(new EntryItem(e, entry.getKey()));         // aggiungi tutti i valori
            }
        }
        notifyDataSetChanged();
    }



    public interface OnItemDeleteListener {
        void onDeleteItem(EntryItem item);
    }


    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;

        DayViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);

            // Listener del bottone



        }
    }

    static class NumberViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        Button btnDelete;
        NumberViewHolder(View itemView, OnItemDeleteListener listener, List<ListItem> items) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                int posiz=getAdapterPosition();
                if (posiz != RecyclerView.NO_POSITION) {
                    listener.onDeleteItem((EntryItem) items.get(posiz));
                }
            });
        }
    }

    abstract class ListItem {
        static final int TYPE_DAY = 0;
        static final int TYPE_ENTRY = 1;
        String mystring;

        ListItem(String string){
            this.mystring=string;
        }
        String getString(){
            return this.mystring;
        }
        abstract int getType();
    }

    class DayItem extends ListItem {

        DayItem(String string) {
            super(string);
        }

        @Override
        int getType() {
            return TYPE_DAY;
        }
    }

    class EntryItem extends ListItem{

        String day;
        EntryItem(String string, String day) {
            super(string);
            this.day=day;
        }

        @Override
        int getType() {
            return TYPE_ENTRY;
        }
        String getDay(){
            return this.day;
        }

    }


}