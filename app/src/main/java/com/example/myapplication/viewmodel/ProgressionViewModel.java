package com.example.myapplication.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Progression;
import com.example.myapplication.data.ProgressionEntry;
import com.example.myapplication.data.ProgressionRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ProgressionViewModel extends AndroidViewModel {

    //private final ArrayList<String> list=new ArrayList<>();
    //private final Map<Date, List<String>> diz = new TreeMap<>();
    private MutableLiveData<TreeMap<String, List<String>>> uiData = new MutableLiveData<>(new TreeMap<>());
    private final Map<String, List<Long>> uiIds = new HashMap<>();

    private ProgressionRepository repository;
    private LiveData<List<Progression>> progression;
    private LiveData<List<ProgressionEntry>> progression_entry;
    private final MutableLiveData<Long> selectedProgressionId =
            new MutableLiveData<>();


    public ProgressionViewModel(@NonNull Application application) {
        super(application);
        repository = new ProgressionRepository(application);
        progression = repository.getAllProgressions();
        progression_entry=repository.getAllEntries();


        //transformation.map attiva un lstener  che ogni volta che il primo argomento indicato viene invocato, lui fa una trasformazione prima di passarlo
//        this.uiData = (MutableLiveData<TreeMap<String, List<String>>>) Transformations.map(progression_entry, progression -> {
//            TreeMap<Date, List<String>> valueMap = new TreeMap<>();
//            Map<Date, List<Long>> idMap = new TreeMap<>();
//
//            for (ProgressionEntry p : progression) {
//                valueMap.computeIfAbsent(p.date, d -> new ArrayList<>())
//                        .add(String.valueOf(p.value));
//
//                idMap.computeIfAbsent(p.date, d -> new ArrayList<>())
//                        .add(p.id);
//            }
//
//            // conversione date → string
//            TreeMap<String, List<String>> uiMap =
//                    convertToAdapterFormat(valueMap);
//
//            Map<String, List<Long>> tempIds =
//                    convertIdsToAdapterFormat(idMap);
//
//            // aggiorna la mappa parallela
//            uiIds.clear();
//            uiIds.putAll(tempIds);
//
//
//            return uiMap;
//        });

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
        //progression_entry =repository.getEntries();



    //METODI SULLA LISTA PROGRESSION

    // Aggiungi una progression
    public void addProgression(String name) {
        Progression p=new Progression(name);
        new Thread(() -> repository.insertProgression(p)).start();
    }


    // Cancella una progression
    public void deleteProgression(Progression p) {
        new Thread(() -> repository.deleteProgression(p)).start();
    }

    public LiveData<List<Progression>> getAllProgression(){
        return progression;
    }



    public LiveData<TreeMap<String, List<String>>> getUiData() {
        return uiData;
    }

    public void loadIdOfSelectedProgression(long progressionId) {
        selectedProgressionId.setValue(progressionId);
    }



//METODI CHE LAVORANO SULLA MAP ENTRIES

    public void addEntry(long id, int value) {
        ProgressionEntry e =
                new ProgressionEntry(id, getTime(), value);
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


}


