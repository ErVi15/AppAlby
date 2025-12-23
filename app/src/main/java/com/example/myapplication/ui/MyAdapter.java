package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private OnItemButtonClickListener listener; //listener dinamico per il bottone Modifica che avrà ogni oggetto della lista
    private List<String> items;

    public MyAdapter(List<String> items,OnItemButtonClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        Button button1, button2;

        public MyViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.itemText);
            button1=itemView.findViewById(R.id.itemButton); //button Elimina
            button2=itemView.findViewById(R.id.itemButton2); //button Modifica
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.text.setText(items.get(position));

        holder.button1.setOnClickListener(v -> {
            removeElement(position);
        });

        holder.button2.setOnClickListener(v -> {
            if(listener != null) {
                listener.onChangeViewClick(position); // Notifica il Fragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addElement(String stringa){
        items.add(stringa);
        notifyItemInserted(items.size() - 1);
    }


    public void removeElement(int position){
        items.remove(position);
        notifyItemRemoved(position);

    }

    public void setList(List<String> newList) {
        this.items.clear();
        this.items.addAll(newList);
        notifyDataSetChanged();
    }

    public void showElements(){
        System.out.println("La lista contiene attualmente i seguenti: ");
        for(String e: items ){
            System.out.println(e);
        }
    }


    public interface OnItemButtonClickListener {
        void onChangeViewClick(int position); // o String item se vuoi
    }
}

