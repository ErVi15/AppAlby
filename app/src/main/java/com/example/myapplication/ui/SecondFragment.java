package com.example.myapplication.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

    private boolean firstLoad = true;

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
        View indicator = binding.buttonSecond.findViewById(R.id.view_feedback_indicator);
        // Creo il BottomSheetDialog
        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());

        // Inflato il layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheet_feedback, null);


        ObjectAnimator blinkAnim = ObjectAnimator.ofFloat(indicator, "alpha", 1f, 0f);
        MaterialTextView emptyText= view.findViewById(R.id.emptyText2);

        //qui ho creato un osservatore annidato, che può creare memory leak sul lungo andare, tenere d'occhio (andrebbe usato mediator che impacchetta un Pair dei due oggetti e fai un osservatore solo)
        viewModel.getUiData().observe(getViewLifecycleOwner(), map -> {



                if(map.isEmpty()){
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }

            if(firstLoad){
                firstLoad=false;
                indicator.setBackgroundResource(R.drawable.bg_feedback_indicator);
                // animazione lampeggiante: alpha da 1 a 0 ripetutamente
                blinkAnim.setDuration(1000); // mezzo secondo
                blinkAnim.setRepeatMode(ValueAnimator.REVERSE);
                blinkAnim.setRepeatCount(ValueAnimator.INFINITE);
                blinkAnim.start();
                blinkAnim.pause();

            } else {
                indicator.setBackgroundResource(R.drawable.bg_feedback_indicator_positive);
                blinkAnim.resume();
            }

            String uMisura=viewModel.getUMisura(currentId);
            adapter.addElement(map, uMisura); // aggiorna RecyclerView
            viewModel.printUiIds(); //questo è un metodo log
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


        //bottone soglia
        ImageButton button_settings = view.findViewById(R.id.settingsButton);
        button_settings.setOnClickListener(btn-> {

            View alertView = getLayoutInflater().inflate(R.layout.preference_settings, null);

            EditText valore_desiderato = alertView.findViewById(R.id.input1);
            EditText costanza_desiderata = alertView.findViewById(R.id.input2);
            Button button_reset = alertView.findViewById(R.id.input3);

            button_reset.setOnClickListener(btn2 -> {

                TextView textMediana = bottomSheetView.findViewById(R.id.text_mediana);
                TextView textWeekMax = bottomSheetView.findViewById(R.id.text_week_max);
                TextView textCostanza = bottomSheetView.findViewById(R.id.text_costanza);

                TextView textMedianaSoglia = bottomSheetView.findViewById(R.id.text_mediana_soglia);
                TextView textWeekMaxSoglia = bottomSheetView.findViewById(R.id.soglia_text_week_max);
                TextView textCostanzaSoglia = bottomSheetView.findViewById(R.id.soglia_text_costanza);

                // 1️⃣ reset soglie nel viewModel
                viewModel.setValoreDesiderato(0);
                viewModel.setCostanzaDesiderata(0);

                // 2️⃣ aggiorna la UI come se non ci fossero soglie
                textWeekMaxSoglia.setText("");
                textMedianaSoglia.setText("");
                textCostanzaSoglia.setText("");

                // 3️⃣ aggiorna colori dei valori principali
                textMediana.setTextColor(Color.DKGRAY);
                textWeekMax.setTextColor(Color.DKGRAY);
                textCostanza.setTextColor(Color.DKGRAY);
                Toast.makeText(requireContext(), "Obiettivi resettati", Toast.LENGTH_SHORT).show();


            });


            new AlertDialog.Builder(requireContext())
                    .setTitle("Impostazioni")
                    .setView(alertView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String input1=valore_desiderato.getText().toString();
                        String input2=costanza_desiderata.getText().toString();


                        try{
                            if(input1.isEmpty()){
                                int obb_soglia = Integer.parseInt(input2);
                                this.viewModel.setCostanzaDesiderata(obb_soglia);
                                this.viewModel.updateProgression();
                                Toast.makeText(requireContext(),
                                        "Impostato obbiettivo: Costanza",
                                        Toast.LENGTH_SHORT).show();

                            } else if (input2.isEmpty()){
                                int obb_val_tipico = Integer.parseInt(input1);
                                this.viewModel.setValoreDesiderato(obb_val_tipico);
                                this.viewModel.updateProgression();
                                Toast.makeText(requireContext(),
                                        "Impostato obbiettivo: Valore tipico",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                int obb_val_tipico = Integer.parseInt(input1);
                                int obb_soglia = Integer.parseInt(input2);
                                // Passo tutto al viewModel
                                this.viewModel.setValoreDesiderato(obb_val_tipico);
                                this.viewModel.setCostanzaDesiderata(obb_soglia);

                                this.viewModel.updateProgression();
                                Toast.makeText(requireContext(),
                                        "Impostati obiettivi",
                                        Toast.LENGTH_SHORT).show();

                            }
                        } catch (NumberFormatException e) {
                                Toast.makeText(requireContext(),
                                        "Valori non validi (controlla siano numeri interi)",
                                        Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Annulla", (dialog, which) -> dialog.cancel())
                    .show();
        });



        binding.buttonSecond.setOnClickListener(v -> {
            ProgressFeedback feedback = viewModel.getFinalFeedback();
            if (feedback == null) {
                Toast.makeText(requireContext(),
                        "Inserisci almeno un valore",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            indicator.setBackgroundResource(R.drawable.bg_feedback_indicator);
            blinkAnim.pause();


            // Popolo le TextView
            TextView textState = bottomSheetView.findViewById(R.id.text_feedback_state);
            TextView textDescription = bottomSheetView.findViewById(R.id.text_feedback_description);
            TextView textSuggestion = bottomSheetView.findViewById(R.id.text_feedback_suggestion);
            // valori numerici
            TextView textStabilita = bottomSheetView.findViewById(R.id.text_stabilita);
            TextView textMediana = bottomSheetView.findViewById(R.id.text_mediana);
            TextView textWeekMax = bottomSheetView.findViewById(R.id.text_week_max);
            TextView textCostanza = bottomSheetView.findViewById(R.id.text_costanza);

            TextView textMedianaSoglia = bottomSheetView.findViewById(R.id.text_mediana_soglia);
            TextView textWeekMaxSoglia = bottomSheetView.findViewById(R.id.soglia_text_week_max);
            TextView textCostanzaSoglia = bottomSheetView.findViewById(R.id.soglia_text_costanza);




            textState.setText(feedback.getState().name().replace("_", " "));
            switch(feedback.getState()) {
                case ALTALENANTE:
                case CONFORT_ZONE:
                case AFFATICATO:
                    textState.setTextColor(Color.parseColor("#EAD895"));
                    break;
                case STONKS:
                case EMERGENTE:
                case CONSOLIDAMENTO:
                    textState.setTextColor(Color.parseColor("#006400"));
                    break;
                case IN_CALO:
                    textState.setTextColor(Color.RED);
                    break;
                case AGGIUNGI_ALTRI_GIORNI:
                case RIPRESA_DOPO_PAUSA:
                    textState.setTextColor(Color.parseColor("#ADD8E6"));
                    break;
            }

            //qui devi modificare la gestione visiva della soglia
            textDescription.setText(feedback.getState().getDescription());
            textSuggestion.setText(feedback.getState().getSugguestion());
            textStabilita.setText(Math.round(feedback.getStabilità() * 100) +"%");
            String uMisura=viewModel.getUMisura(currentId);
            long valore_desiderato= viewModel.getValoreDesiderato();
            long costanza_desiderata= viewModel.getCostanzaDesiderata();
            if (!Objects.equals(uMisura, "")) {
                // Aggiorna la UI con il valore
                if(uMisura.length()>=4) {
                    uMisura = uMisura.substring(0, 3);
                }
                textMediana.setText(feedback.getMediana() +" ("+uMisura+")");
                if(feedback.getMediana()>valore_desiderato && valore_desiderato!=0){
                    textMediana.setTextColor(Color.parseColor("#006400"));
                }else {
                    textMediana.setTextColor(Color.DKGRAY);
                }

                textWeekMax.setText(feedback.getWeekMax() +" ("+uMisura+")");
                if(feedback.getWeekMax()>valore_desiderato && valore_desiderato!=0){
                    textWeekMax.setTextColor(Color.parseColor("#006400"));
                } else {
                    textWeekMax.setTextColor(Color.DKGRAY);
                }
            } else {
                // Valore null → puoi mostrare testo di default o vuoto
                textMediana.setText(String.valueOf(feedback.getMediana()));
                textWeekMax.setText(String.valueOf(feedback.getWeekMax()));
            }


            textCostanza.setText(Math.round(feedback.getCostanza() * 100) +"%");
            if((feedback.getCostanza()*100)>costanza_desiderata && costanza_desiderata!=0){
                textCostanza.setTextColor(Color.parseColor("#006400"));
            } else {
                textCostanza.setTextColor(Color.DKGRAY);
            }
            if(valore_desiderato!=0){
                textWeekMaxSoglia.setText(String.valueOf(valore_desiderato));
                textMedianaSoglia.setText(String.valueOf(valore_desiderato));
            } else {
                textWeekMaxSoglia.setText("");
                textMedianaSoglia.setText("");
            }
            if(costanza_desiderata!=0){
                textCostanzaSoglia.setText(costanza_desiderata +"%");
            } else {
                textCostanzaSoglia.setText("");
            }




            LineChart lineChart = bottomSheetView.findViewById(R.id.lineChart);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setGranularity(1f);              // passo minimo = 1
            xAxis.setGranularityEnabled(true);     // forza l'uso della granularità
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //mette l'ascissa sotto
            xAxis.setAxisLineWidth(2f);
            YAxis leftAxis = lineChart.getAxisLeft();
            YAxis rightAxis =  lineChart.getAxisRight();


            rightAxis.setEnabled(true);   // disabilita asse destro
            rightAxis.setGranularity(1f);
            rightAxis.setGranularityEnabled(true);
            rightAxis.setAxisLineWidth(2f);
             // percentuale di spazio
            leftAxis.setEnabled(false);     // esplicito, per chiarezza


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

        String option=viewModel.getOption(currentId);

        // Inflazione layout XML
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_progression_entry, null);

        EditText input1 = dialogView.findViewById(R.id.input1);//widget android che crea il box in cui inserire iput
        EditText input2 = dialogView.findViewById(R.id.input2);
        EditText input3 = dialogView.findViewById(R.id.input3);
        ImageButton custom_selector= dialogView.findViewById(R.id.custom_selector);
        TextView customDateText =dialogView.findViewById(R.id.custom_date_text);

        // Stato della data selezionata
        final boolean[] isCustomDateSelected = {false};

        // Mostro/nascondo campi in base all'opzione
        switch (option) {
            case "Singolo":
                input2.setVisibility(View.GONE);
                input3.setVisibility(View.GONE);
                break;
            case "Coppia":
                input2.setVisibility(View.VISIBLE);
                input3.setVisibility(View.GONE);
                break;
            case "Tripla":
                input2.setVisibility(View.VISIBLE);
                input3.setVisibility(View.VISIBLE);
                break;
        }

        List<EditText> fields = new ArrayList<>();
        fields.add(input1);
        if(input2.getVisibility() == View.VISIBLE) fields.add(input2);
        if(input3.getVisibility() == View.VISIBLE) fields.add(input3);

        custom_selector.setOnClickListener(v -> {
            if(!isAdded())return;
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // selectedMonth è 0-based (0 = gennaio)
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                        selectedDate.set(Calendar.MINUTE, 0);
                        selectedDate.set(Calendar.SECOND, 0);
                        selectedDate.set(Calendar.MILLISECOND, 0);

                        viewModel.setCustom_date(selectedDate.getTime());
                        // qui usi la data (salvataggio, aggiornamento UI, ecc.)
                        isCustomDateSelected[0]=true;

                        // Aggiorna TextView (opzionale)
                        if (customDateText != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            customDateText.setText(sdf.format(selectedDate.getTime()));
                        }
                    },
                    year,
                    month,
                    day
            );

// opzionale ma consigliato: blocca date future
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

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
                        if(isCustomDateSelected[0]){
                            viewModel.addEntry(currentId, result, viewModel.getCustom_date());//aggiorno la mappa in viewModel
                        } else {
                            viewModel.addEntry(currentId, result);//aggiorno la mappa in viewModel
                        }
                        viewModel.resetCustom_date();

                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(),
                                "Valore non valido (inserisci numeri interi)",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", (dialog, which) -> dialog.cancel())
                .show();


    }


    public void onResume(){
        super.onResume();
        ((MainActivity) requireActivity()).setFabAction(this::addNewItem);
    }


    public void removeItem(String day, int position) {
        viewModel.deleteEntry(day, position);
    }


}