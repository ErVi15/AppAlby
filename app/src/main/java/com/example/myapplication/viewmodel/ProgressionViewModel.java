package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ProgressionViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<String>> uiList = new MutableLiveData<>(); // lista che efettivamente contiene i dati, Adapter ha una copia
    private final ArrayList<String> list=new ArrayList<>();
    private final Map<Date, List<String>> diz = new TreeMap<>();
    private final MutableLiveData<TreeMap<String, List<String>>> uiData = new MutableLiveData<>(new TreeMap<>());

    public LiveData<TreeMap<String, List<String>>> getUiData() {
        return uiData;
    }

    public LiveData<ArrayList<String>> getListData(){
        return uiList;
    }

    public void addValueList(String string){
        this.list.add(string);
        updateListData();
    }

    public void removeValueList(int position){
        this.list.remove(position);
        updateListData();
    }

    public void updateListData(){
        uiList.setValue(list);
    }



    // Metodo pubblico per aggiungere valori
    public void addValueToMap(String value) {
        Date today = getTime(); // tuo metodo per prendere la data odierna
        diz.computeIfAbsent(today, k -> new ArrayList<>()).add(value);
        updateUiData(); // aggiorna LiveData
    }

    // Metodo interno che converte la mappa per l'Adapter
    private void updateUiData() {
        uiData.setValue(convertToAdapterFormat());
    }





    public void removeValue(String day, String value) {

        Date date = parseDateWithoutTime(day);
        List<String> internalList = diz.get(date);
        if (internalList != null) {
            internalList.remove(value);
            if (internalList.isEmpty()) diz.remove(date);
        }

        // 2. Aggiorna uiData
        updateUiData(); // converte diz in Map<String, List<String>> e fa uiData.setValue(...)

    }


    public TreeMap<String, List<String>> convertToAdapterFormat() {

        TreeMap<String, List<String>> newMap = new TreeMap<>();

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Map.Entry<Date, List<String>> entry : this.diz.entrySet()) {

            Date dateKey = entry.getKey();
            List<String> value = entry.getValue();

            String dateString = sdf.format(dateKey);

            //newMap.put(dateString, value); equivalente a
            newMap.computeIfAbsent(dateString, k -> new ArrayList<>())
                    .addAll(value);

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
}


