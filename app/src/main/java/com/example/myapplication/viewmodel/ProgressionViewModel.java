package com.example.myapplication.viewmodel;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Progression;
import com.example.myapplication.data.ProgressionEntry;
import com.example.myapplication.data.ProgressionRepository;
import com.example.myapplication.domain.feedback.FeedbackEvaluator;
import com.example.myapplication.domain.feedback.ProgressFeedback;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ProgressionViewModel extends AndroidViewModel {

    private MutableLiveData<TreeMap<String, List<String>>> uiData = new MutableLiveData<>(new TreeMap<>());
    private final Map<String, List<Long>> uiIds = new HashMap<>();
    private ProgressionRepository repository;
    private LiveData<List<Progression>> progression;
    private LiveData<List<ProgressionEntry>> progression_entry;
    private final MutableLiveData<Long> selectedProgressionId =
            new MutableLiveData<>();

    private final MutableLiveData<Progression> selectedProgression = new MutableLiveData<>();

    private FeedbackEvaluator feedback;
    private Date custom_date;


    public ProgressionViewModel(@NonNull Application application) {
        super(application);
        repository = new ProgressionRepository(application);
        progression = repository.getAllProgressions();
        progression_entry=repository.getAllEntries();
        feedback=new FeedbackEvaluator();

        uiData = (MutableLiveData<TreeMap<String, List<String>>>)
                Transformations.switchMap(selectedProgressionId, pid ->
                        Transformations.map(progression_entry, entries -> {

                            TreeMap<Date, List<String>> valueMap = new TreeMap<>();
                            Map<Date, List<Long>> idMap = new TreeMap<>();

                            for (ProgressionEntry p : entries) {
                                if (p.progressionId != pid) continue;

                                valueMap.computeIfAbsent(p.date, d -> new ArrayList<>())
                                        .add(String.valueOf(p.value));

                                idMap.computeIfAbsent(p.date, d -> new ArrayList<>())
                                        .add(p.id);
                            }

                            uiIds.clear();
                            uiIds.putAll(convertIdsToAdapterFormat(idMap));

                            return convertToAdapterFormat(valueMap);
                        }));


    }


    //METODI SULLA LISTA PROGRESSION

    public void addProgression(String name, String u_misura, String option) {
        Progression p=new Progression(name, u_misura, option);
        new Thread(() -> repository.insertProgression(p)).start();
    }


    // Cancella una progression
    public void deleteProgression(Progression p) {
        new Thread(() -> repository.deleteProgression(p)).start();
    }

    public void updateProgression(){
        new Thread(() -> repository.updateProgression(selectedProgression.getValue())).start();
    }

    public LiveData<List<Progression>> getAllProgression(){
        return progression;
    }



    public LiveData<TreeMap<String, List<String>>> getUiData() {
        return uiData;
    }

    public void loadIdOfSelectedProgression(long progressionId) {
        selectedProgressionId.setValue(progressionId);

        Progression p = findProgressionById(progressionId);
        selectedProgression.setValue(p);
    }

    private Progression findProgressionById(long id) {
        if (progression.getValue() == null) return null;

        for (Progression p : progression.getValue()) {
            if (p.id == id) return p;
        }
        return null;
    }

    public String getUMisura(long progressionId){
        return selectedProgression.getValue() != null
                ? selectedProgression.getValue().u_misura
                : "";
    }


    public String getOption(long progressionId){
        return selectedProgression.getValue() !=null
                ? selectedProgression.getValue().option
                : "";
    }

    public void setCostanzaDesiderata(int valore){
        Objects.requireNonNull(selectedProgression.getValue()).costanza_desiderata=valore;
    }

    public void setValoreDesiderato(int valore){
        Objects.requireNonNull(selectedProgression.getValue()).valore_desiderato=valore;
    }

    public int getCostanzaDesiderata() {
        return Objects.requireNonNull(selectedProgression.getValue()).costanza_desiderata!=0
                ? selectedProgression.getValue().costanza_desiderata
                : 0;

    }

    public int getValoreDesiderato() {
        return Objects.requireNonNull(selectedProgression.getValue()).valore_desiderato!=0
                ? selectedProgression.getValue().valore_desiderato
                : 0;
    }





//METODI CHE LAVORANO SULLA MAP ENTRIES

    public void addEntry(long id, int value) {
        ProgressionEntry e =
                new ProgressionEntry(id, getTime(), value);
        repository.insertEntry(e);
        Log.d("ENTRY", "id=" + e.id + " progressionId=" + e.progressionId);
    }
    public void addEntry(long id, int value, Date custom_date) {
        ProgressionEntry e =
                new ProgressionEntry(id, custom_date, value);
        repository.insertEntry(e);
        Log.d("ENTRY", "id=" + e.id + " progressionId=" + e.progressionId);
    }


//qui c'è l'uso della mapp uiIds, che associa all'elemento x dell uiData, all'elemento y alla stessa posizione in uiId,
// cosi da ottenere l'id corretto
    public void deleteEntry(String day, int position) {

            List<Long> ids = uiIds.get(day);
            if (ids == null) {
                Log.w("DELETE", "uiIds null for day " + day);
                return;
            }
            if (position < 0 || position >= ids.size()) {
                Log.w("DELETE", "Invalid position " + position + " for day " + day + ", ids=" + ids);
                return; // <-- evita crash
            }

            long id = ids.get(position);
            Log.d("DELETE", "Deleting id=" + id + " at position=" + position + " for day=" + day);
            deleteEntry(id);


    }


    public void deleteEntry(long id) {
        repository.deleteEntryById(id);
    }

    public void loadEntriesForProgression(long progressionId){
        repository.getEntries(progressionId);
    }


    //METODI DI MANIPOLAZIONE DEI DATI

    public LineDataSet[] getDataSetForChart(){
        TreeMap<String, List<String>> map = uiData.getValue();
        ArrayList<Integer[]> median_max_of_weeks= feedback.prepareDataForChart(convertFromAdapterFormat(map)); //tutti i giorni separati per settimane, e convertiti a date integer
        Collections.reverse(median_max_of_weeks);

        //Data set per mediane
        List<Entry> medianaEntries = new ArrayList<>();
        int i=0;
        for(Integer[] e: median_max_of_weeks){
            medianaEntries.add(new Entry(i, e[0]));
            i++;
        }

// ...

        LineDataSet medianaDataSet = new LineDataSet(medianaEntries, "Valore tipico");
        medianaDataSet.setColor(Color.GREEN); // colore linea
        medianaDataSet.setCircleColor(Color.GREEN); // colore punti
        medianaDataSet.setLineWidth(2f);
        medianaDataSet.setCircleRadius(3f);
        medianaDataSet.setDrawValues(false); //elimina la targhette con i valori sopra ogni punto


        //DAta set per massimi

        List<Entry> maxEntries2 = new ArrayList<>();
        int j=0;
        for(Integer[] e: median_max_of_weeks){
            maxEntries2.add(new Entry(j, e[1]));
            j++;
        }
// ...

        LineDataSet maxDataSet2 = new LineDataSet(maxEntries2, "Record");
        maxDataSet2.setColor(Color.CYAN); // colore linea
        maxDataSet2.setCircleColor(Color.CYAN); // colore punti
        maxDataSet2.setLineWidth(2f);
        maxDataSet2.setCircleRadius(4f);
        maxDataSet2.setDrawValues(false); //elimina la targhette con i valori sopra ogni punto

        return new LineDataSet[]{medianaDataSet, maxDataSet2};
    }

    private TreeMap<Date, List<String>> buildValueMap(
            List<ProgressionEntry> entries,
            long progressionId
    ) {
        TreeMap<Date, List<String>> valueMap = new TreeMap<>();

        for (ProgressionEntry p : entries) {
            if (p.progressionId != progressionId) continue;

            valueMap
                    .computeIfAbsent(p.date, d -> new ArrayList<>())
                    .add(String.valueOf(p.value));
        }

        return valueMap;
    }


    public TreeMap<String, List<String>> convertToAdapterFormat(TreeMap<Date, List<String>> target) {

        TreeMap<String, List<String>> newMap = new TreeMap<>();

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Map.Entry<Date, List<String>> entry : target.entrySet()) {

            Date dateKey = entry.getKey();
            List<String> value = entry.getValue();

            String dateString = sdf.format(dateKey);

            //newMap.put(dateString, value); equivalente a
            newMap.computeIfAbsent(dateString, k -> new ArrayList<>())
                    .addAll(value);

        }

        return newMap;
    }

    public TreeMap<Date, List<String>> convertFromAdapterFormat(
            TreeMap<String, List<String>> target
    ) {
        TreeMap<Date, List<String>> newMap = new TreeMap<>();

        for (Map.Entry<String, List<String>> entry : target.entrySet()) {

            String dateString = entry.getKey();
            List<String> values = entry.getValue();

            Date dateKey = parseDateWithoutTime(dateString);

            newMap.computeIfAbsent(dateKey, d -> new ArrayList<>())
                    .addAll(values);
        }

        return newMap;
    }


    private Map<String, List<Long>> convertIdsToAdapterFormat(
            Map<Date, List<Long>> target) {

        Map<String, List<Long>> newMap = new HashMap<>();
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Map.Entry<Date, List<Long>> entry : target.entrySet()) {

            String dateString = sdf.format(entry.getKey());

            newMap.computeIfAbsent(dateString, k -> new ArrayList<>())
                    .addAll(entry.getValue());
        }

        return newMap;
    }


    public Date parseDateWithoutTime(String dateString)  {

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date parsedDate = sdf.parse(dateString);

            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            return cal.getTime();
        } catch(ParseException e){
            throw new IllegalArgumentException(e);
        }
    }


    public Date getTime(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date getCustom_date() {
        return custom_date;
    }

    public void resetCustom_date(){
        this.custom_date=getTime();
    }
    public void setCustom_date(Date custom_date) {
        this.custom_date = custom_date;
    }

    public void debugLogEntryIds() {
        new Thread(() -> {
            List<Long> ids = repository.debugGetIds();
            Log.d("DB_IDS", "IDs in DB = " + ids);
        }).start();
    }

    public void printUiIds() {
        for (Map.Entry<String, List<Long>> e : uiIds.entrySet()) {
            Log.d("UI_IDS", e.getKey() + " -> " + e.getValue());
        }
    }

    private ProgressFeedback getFeedback(){
        TreeMap<String, List<String>> map = uiData.getValue();
        if (map == null || map.isEmpty()) {
            return null;
        }
        return feedback.getFeedback(convertFromAdapterFormat(map), false);
    }

    private ProgressFeedback getFeedbackLastWeek(){
        TreeMap<String, List<String>> map = uiData.getValue();
        if (map == null || map.isEmpty()) {
            return null;
        }
        return feedback.getFeedback(convertFromAdapterFormat(map), true);
    }

    public ProgressFeedback getFinalFeedback(){
        ProgressFeedback this_week_fb=getFeedback();
        ProgressFeedback second_last_week_fb=getFeedbackLastWeek();
        if(this_week_fb==null && second_last_week_fb==null){
            return null;
        } else {
            feedback.setProgressState(this_week_fb, second_last_week_fb); //imposta stato e descrizione nell-ogetto ProgressFeedback
            return this_week_fb;
        }

    }



}


