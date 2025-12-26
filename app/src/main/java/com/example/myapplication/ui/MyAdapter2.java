package com.example.myapplication.ui;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.ProgressionEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            for ( String e: entry.getValue() ) {
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

    private static int getRealPosition(List<ListItem> items, int adapterPosition) {
        int realPos = 0;
        for (int i = 0; i < adapterPosition; i++) {
            if (items.get(i).getType() == ListItem.TYPE_ENTRY) {
                realPos++;
            }
        }
        return realPos;
    }



    public void addElement(Map<String, List<String> > map){
        items.clear();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {

            this.items.add(new DayItem(entry.getKey()));// aggiungi la chiave
            for (String e : entry.getValue()) {
                items.add(new EntryItem(e, entry.getKey()));         // aggiungi tutti i valori
            }
        }
        Log.d("ADAPTER", "Adapter items: " + items.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        notifyDataSetChanged();

    }



    public interface OnItemDeleteListener {
        void onDeleteItem(String day, int position);
    }

//questo non va usato nel modello architetturale mvvn: l'adapter non deve conoscere dati del repo. L'id lo conoscerà il viewmodel
//    public interface OnItemDeleteListener {
//        void onDeleteItem(long id);
//    }


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
                    EntryItem item = (EntryItem) items.get(posiz);
                    int real_posiz=getRealPosition(items, posiz);
                    listener.onDeleteItem(item.getDay(), real_posiz);
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

    class EntryItem extends ListItem {
        long id;
        String day;

        EntryItem( String value, String day) {
            super(value);
            this.day = day;
        }

        long getId() { return id; }
        String getDay() { return day; }

        @Override
        int getType() {
            return TYPE_ENTRY;
        }
    }



}