package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.Progression; // Assumendo il tuo entity

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private OnItemButtonClickListener listener;
    private List<Progression> items = new ArrayList<>();

    public MyAdapter(OnItemButtonClickListener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageButton button1, button2;

        public MyViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.itemText);
            button1 = itemView.findViewById(R.id.itemButton); // Elimina
            button2 = itemView.findViewById(R.id.itemButton2); // Modifica
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
        Progression progression = items.get(position);
        holder.text.setText(progression.name);

        holder.button1.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(progression); // Passiamo l’oggetto al fragment
            }
        });

        holder.button2.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChangeViewClick(progression); // Passiamo l’oggetto al fragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setList(List<Progression> newList) {
        this.items.clear();
        if(newList != null) {
            this.items.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public interface OnItemButtonClickListener {
        void onDeleteClick(Progression progression); // Nuovo metodo per eliminare
        void onChangeViewClick(Progression progression); // Modifica
    }


}
