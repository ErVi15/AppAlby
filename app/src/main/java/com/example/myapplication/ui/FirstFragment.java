package com.example.myapplication.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.google.android.material.textview.MaterialTextView;

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

        MaterialTextView emptyText= view.findViewById(R.id.emptyText);
        viewModel.getAllProgression().observe(getViewLifecycleOwner(), list -> {
            adapter.setList(list); // aggiorna RecyclerView

            if(list.isEmpty()){
                emptyText.setVisibility(View.VISIBLE);
            } else {
                emptyText.setVisibility(View.GONE);
            }

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
//        EditText input = new EditText(requireContext()); 
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_progression, null);

        EditText inputName = dialogView.findViewById(R.id.inputName); //widget android che crea il box in cui inserire iput
        EditText inputUMisura = dialogView.findViewById(R.id.inputUMisura);
        RadioGroup toggleOptions = dialogView.findViewById(R.id.toggleOptions);
        TextView toggleDesc= dialogView.findViewById(R.id.option_desc);
        new AlertDialog.Builder(requireContext())
                .setTitle("Nuova progressione")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String name = inputName.getText().toString();
                    String u_misura = inputUMisura.getText().toString();

                    // Recupero l'opzione selezionata
                    int selectedId = toggleOptions.getCheckedRadioButtonId();
                    String option = "";
                    if (selectedId != -1) {
                        RadioButton selected = dialogView.findViewById(selectedId);

                        option = selected.getText().toString();
                    }

                    // Passo tutto al viewModel
                    this.viewModel.addProgression(name, u_misura, option);

                    // Se vuoi aggiornare la RecyclerView subito
                    // adapter.addElement(name); 
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.cancel())
                .show();

        toggleOptions.check(R.id.oneinput);
        toggleDesc.setText("Valore singolo");
        toggleOptions.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = dialogView.findViewById(checkedId);
            if (selected != null) {
                switch(selected.getText().toString()) {
                    case "Singolo":
                        toggleDesc.setText("Valore singolo");
                        inputUMisura.setVisibility(VISIBLE);
                        break;
                    case "Coppia":
                        toggleDesc.setText("Serie x Ripetizioni");
                        inputUMisura.getText().clear();
                        //inputUMisura.setVisibility(GONE);
                        break;
                    case "Tripla":
                        toggleDesc.setText("Serie x Ripetizioni x Peso");
                        inputUMisura.getText().clear();
                        //inputUMisura.setVisibility(GONE);
                        break;
                }
            }
        });
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