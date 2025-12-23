package com.example.myapplication.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSecondBinding;
import com.example.myapplication.viewmodel.ProgressionViewModel;

import java.util.List;
import java.util.TreeMap;

/*
        | Cosa fa ora nel Fragment/Adapter | Dove dovrebbe stare |
        | --------------------------------- | ------------------- |
        | Creare Adapter, RecyclerView      | Fragment            |
        | Manipolare mappa `diz`            | ViewModel           |
        | Convertire map per Adapter        | ViewModel/Domain    |
        | Aggiungere/rimuovere item         | ViewModel           |
        | Mostrare AlertDialog              | Fragment            |
        | Navigazione tra Fragment          | Fragment            |
*/

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private RecyclerView recyclerView;

    private MyAdapter2 adapter;

    private ProgressionViewModel viewModel;




    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //lista item

        recyclerView = view.findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(ProgressionViewModel.class);


        //listener per cancellare l'entry
        // Qui "item" è l'EntryItem cliccato
        adapter = new MyAdapter2(new TreeMap<>(), this::removeItem);

        recyclerView.setAdapter(adapter);

        viewModel.getUiData().observe(getViewLifecycleOwner(), map -> {
            adapter.addElement(map); // aggiorna RecyclerView
        });

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addNewItem() {
        EditText input = new EditText(requireContext()); //widget android che crea il box in cui inserire iput

        new AlertDialog.Builder(requireContext()) //pop up box in cui inserire l'input receiver preparato prima
                //richiedono entrambe il Context, ogni volta che si manipola la vista bisogna averlo
                .setTitle("Nome nuovo elemento")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {

                    String testo = input.getText().toString();
                    this.viewModel.addValueToMap(testo); //aggiorno la mappa in viewModel

                })
                .setNegativeButton("Annulla", (dialog, which) -> {
                    dialog.cancel();
                })
                .show(); //senza questa non viene mostrato nulla

    }




    public void onResume(){
        super.onResume();
        ((MainActivity) requireActivity()).setFabAction(this::addNewItem);
    }

    public void removeItem(MyAdapter2.EntryItem item) {
        String day=item.getDay();
        String value=item.getString();
        viewModel.removeValue(day, value);

    }





}