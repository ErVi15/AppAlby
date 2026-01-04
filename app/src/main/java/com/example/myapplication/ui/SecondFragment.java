package com.example.myapplication.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSecondBinding;
import com.example.myapplication.domain.feedback.ProgressFeedback;
import com.example.myapplication.viewmodel.ProgressionViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
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

    private long currentId;

    private String umisura;


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
        viewModel = new ViewModelProvider(requireActivity()).get(ProgressionViewModel.class);


        //listener per cancellare l'entry
        // Qui "item" è l'EntryItem cliccato
        adapter = new MyAdapter2(new TreeMap<>(), this::removeItem);

        recyclerView.setAdapter(adapter);


//qui ho creato un osservatore annidato, che può creare memory leak sul lungo andare, tenere d'occhio (andrebbe usato mediator che impacchetta un Pair dei due oggetti e fai un osservatore solo)
        viewModel.getUiData().observe(getViewLifecycleOwner(), map -> {
            viewModel.getUMisura(currentId).observe(getViewLifecycleOwner(), uMisura -> {
                adapter.addElement(map, uMisura); // aggiorna RecyclerView
                viewModel.printUiIds(); //questo è un metodo log
            });
        });


        viewModel.debugLogEntryIds();

//questa parte recupera l'id indicato dal firstfragment quando ha richiesto di passare al secondfragment e
// lo usi per recueprare con una chiamata al repo i dati con quell id
        currentId = getArguments() != null ? getArguments().getLong("progressionId") : -1;

        //questo dice al viewmodel che progression è stata seleziona ora
        //questo carica le entries visivamente
        viewModel.loadIdOfSelectedProgression(currentId);
        if (currentId != -1) {
            viewModel.loadEntriesForProgression(currentId);
        }


        binding.buttonSecond.setOnClickListener(v -> {
            ProgressFeedback feedback = viewModel.getFinalFeedback();
            if (feedback == null) return;

            // Creo il BottomSheetDialog
            BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());

            // Inflato il layout
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheet_feedback, null);

            // Popolo le TextView
            TextView textState = bottomSheetView.findViewById(R.id.text_feedback_state);
            TextView textDescription = bottomSheetView.findViewById(R.id.text_feedback_description);
            TextView textSuggestion = bottomSheetView.findViewById(R.id.text_feedback_suggestion);
            // valori numerici
            TextView textStabilita = bottomSheetView.findViewById(R.id.text_stabilita);
            TextView textMediana = bottomSheetView.findViewById(R.id.text_mediana);
            TextView textWeekMax = bottomSheetView.findViewById(R.id.text_week_max);
            TextView textCostanza = bottomSheetView.findViewById(R.id.text_costanza);


            textState.setText(feedback.getState().name().replace("_", " "));
            switch(feedback.getState()) {
                case ALLENAMENTO_IRREGOLARE:
                case SOVRACCARICO_STASI:
                case POTENZIALE_NON_CONSOLIDATO:
                    textState.setTextColor(Color.YELLOW);
                    break;
                case PROGRESSO_SANO:
                    textState.setTextColor(Color.GREEN);
                    break;
                case REGRESSIONE:
                    textState.setTextColor(Color.RED);
                    break;
                case AGGIUNGI_ALTRI_GIORNI:
                case RIPRESA_DOPO_PAUSA:
                    textState.setTextColor(Color.CYAN);
                    break;
            }

            textDescription.setText(feedback.getState().getDescription());
            textSuggestion.setText(feedback.getState().getSugguestion());
            textStabilita.setText(String.valueOf((int) Math.round(feedback.getStabilità()*100))+"%");
            viewModel.getUMisura(currentId).observe(getViewLifecycleOwner(), uMisura -> {
                if (uMisura != null) {
                    // Aggiorna la UI con il valore
                    if(uMisura.length()>=4) {
                        uMisura = uMisura.substring(0, 3);
                    }
                    textMediana.setText(String.valueOf(feedback.getMediana())+" ("+uMisura+")");
                    textWeekMax.setText(String.valueOf(feedback.getWeekMax())+" ("+uMisura+")");
                } else {
                    // Valore null → puoi mostrare testo di default o vuoto
                    textMediana.setText(String.valueOf(feedback.getMediana()));
                    textWeekMax.setText(String.valueOf(feedback.getWeekMax()));
                }
            });

            textCostanza.setText(String.valueOf((int) Math.round(feedback.getCostanza()*100))+"%");


            LineChart lineChart = bottomSheetView.findViewById(R.id.lineChart);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setGranularity(1f);              // passo minimo = 1
            xAxis.setGranularityEnabled(true);     // forza l'uso della granularità
            xAxis.setAxisMinimum(1f); //fa partire l'asse orizzontale da 1
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //mette l'ascissa sotto
            YAxis leftAxis = lineChart.getAxisLeft();
            YAxis rightAxis =  lineChart.getAxisRight();


            rightAxis.setEnabled(false);   // disabilita asse destro

            leftAxis.setSpaceTop(20f); // percentuale di spazio
            leftAxis.setEnabled(true);     // esplicito, per chiarezza



            LineData lineData = new LineData(viewModel.getDataSetForChart()[0], viewModel.getDataSetForChart()[1]);
            lineChart.setData(lineData);
            lineChart.invalidate(); // serve per ridisegnare il grafico
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setTextSize(12f);


            // Imposto il contenuto e mostro
            bottomSheet.setContentView(bottomSheetView);
            bottomSheet.show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addNewItem() {

            viewModel.getOption(currentId).observe(getViewLifecycleOwner(), option -> {

                // Inflazione layout XML
                View dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.dialog_progression_entry, null);

                EditText input1 = dialogView.findViewById(R.id.input1);//widget android che crea il box in cui inserire iput
                EditText input2 = dialogView.findViewById(R.id.input2);
                EditText input3 = dialogView.findViewById(R.id.input3);

                // Mostro/nascondo campi in base all'opzione
                switch (option) {
                    case "Opzione 1":
                        input2.setVisibility(View.GONE);
                        input3.setVisibility(View.GONE);
                        break;
                    case "Opzione 2":
                        input2.setVisibility(View.VISIBLE);
                        input3.setVisibility(View.GONE);
                        break;
                    case "Opzione 3":
                        input2.setVisibility(View.VISIBLE);
                        input3.setVisibility(View.VISIBLE);
                        break;
                }

                List<EditText> fields = new ArrayList<>();
                fields.add(input1);
                if(input2.getVisibility() == View.VISIBLE) fields.add(input2);
                if(input3.getVisibility() == View.VISIBLE) fields.add(input3);
                //pop up box in cui inserire l'input receiver preparato prima
                //                            //richiedono entrambe il Context, ogni volta che si manipola la vista bisogna averlo
                new AlertDialog.Builder(requireContext())
                        .setTitle("Inserisci valori")
                        .setView(dialogView)
                        .setPositiveButton("OK", (dialog, which) -> {
                            try {
                                int result = 1;
                                for(EditText f : fields) {
                                    result *= Integer.parseInt(f.getText().toString());
                                }
                                viewModel.addEntry(currentId, result);//aggiorno la mappa in viewModel
                            } catch (NumberFormatException e) {
                                Toast.makeText(requireContext(),
                                        "Valore non valido (inserisci numeri interi)",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Annulla", (dialog, which) -> dialog.cancel())
                        .show();
            });

    }




    public void onResume(){
        super.onResume();
        ((MainActivity) requireActivity()).setFabAction(this::addNewItem);
    }

//    public void removeItem(MyAdapter2.EntryItem item) {
//        String day=item.getDay();
//        String value=item.getString();
//        viewModel.deleteEntry(day, Integer.parseInt(value));
//
//    }

//    public void removeItem(long id) {
//        viewModel.deleteEntry(id);
//    }

    public void removeItem(String day, int position) {
        viewModel.deleteEntry(day, position);
    }






}