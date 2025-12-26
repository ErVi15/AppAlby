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
import com.example.myapplication.data.Progression;
import com.example.myapplication.databinding.FragmentFirstBinding;
import com.example.myapplication.viewmodel.ProgressionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private RecyclerView recyclerView;
    private ProgressionViewModel viewModel;
    private MyAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        recyclerView = view.findViewById(R.id.myRecyclerView);

        //questa lista ogni votla che cambi pagina si perde andrebbe spostata in create view
        viewModel = new ViewModelProvider(requireActivity()).get(ProgressionViewModel.class);

        adapter = new MyAdapter(new MyAdapter.OnItemButtonClickListener() {
            @Override
            public void onDeleteClick(Progression progression) {
                viewModel.deleteProgression(progression); // delega a ViewModel -> Repository -> Room
            }

            @Override
            public void onChangeViewClick(Progression progression) {
                // Passando anche l'ID della Progression come argomento
                Bundle bundle = new Bundle();
                bundle.putLong("progressionId", progression.id);

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllProgression().observe(getViewLifecycleOwner(), list -> {
            adapter.setList(list); // aggiorna RecyclerView
        });







    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        FloatingActionButton fab = ((MainActivity) requireActivity()).getFab();
        if (fab != null) {
            fab.setOnClickListener(null); // rimuove listener quando esci dal fragment
        }
    }

    public void addNewItem() {
        EditText input = new EditText(requireContext()); //widget android che crea il box in cui inserire iput

        new AlertDialog.Builder(requireContext()) //pop up box in cui inserire l'input receiver preparato prima
                //richiedono entrambe il Context, ogni volta che si manipola la vista bisogna averlo
                .setTitle("Nome nuovo elemento")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String testo = input.getText().toString();
                    //this.viewModel.addValueList(testo);
                    this.viewModel.addProgression(testo);
                    //dovresti aggiornare la lista anche qui nel firstFragment e non lo fai
                    //adapter.addElement(testo); // Adapter aggiorna la lista e la RecyclerView
                })
                .setNegativeButton("Annulla", (dialog, which) -> {
                    dialog.cancel();
                })
                .show(); //senza questa non viene mostrato nulla

    }

    public void removeItem(Progression p) {
        this.viewModel.deleteProgression(p);
        //adapter.removeElement(position); // Adapter aggiorna la lista e la RecyclerView
    }

    public void onResume(){
        super.onResume();
        ((MainActivity) requireActivity()).setFabAction(this::addNewItem);
    }

}