package com.example.myapplication.domain.stat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class StatsCalculator {
//FUNCTION ON TreeMap<Date, List<String>>

    // ottieni gli ultimi 7 giorni di valori
    public TreeMap<Date, List<String>> cutLast7Days(TreeMap<Date, List<String>> map, boolean is_second_last){
        //Date data =map.lastKey(); //data piu recente
        Date today = getTime(); //data odierna
        Date to;
        Date from;
        //data sette giorni prima, il massimo oltre cui non prendere piu entries
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);

        if (!is_second_last) {
            // ultima settimana: [oggi-7 → oggi]
            to = today;
            cal.add(Calendar.DAY_OF_MONTH, -7);
            from = cal.getTime();
            System.out.println("from date "+ from + "to date "+to);
        } else {
            // penultima settimana: [oggi-14 → oggi-7]
            to = today;
            cal.add(Calendar.DAY_OF_MONTH, -7);
            to = cal.getTime();

            cal.add(Calendar.DAY_OF_MONTH, -7);
            from = cal.getTime();
        }
        //Lista delle entries negli ultimi sette giorni
        return new TreeMap<>(map.subMap(from, false, to, true));
    }

    public ArrayList<TreeMap<Date, List<String>>> splitMapBy7Days(TreeMap<Date, List<String>> map) {
        ArrayList<TreeMap<Date, List<String>>> result = new ArrayList<>();
        if (map.isEmpty()) return result; //se mappa è vuota return lista vuota

        // lavoriamo a partire dall'ultima data inserita
        NavigableMap<Date, List<String>> descendingMap = map.descendingMap();
        Iterator<Map.Entry<Date, List<String>>> iter = descendingMap.entrySet().iterator(); //preparo un iterator che contiene i dati della lista ribaltata (cosi gli intervalli partono a ritroso)

        while (iter.hasNext()) { //finche l'iterator ha element
            TreeMap<Date, List<String>> intervalMap = new TreeMap<>(); //mappa d'appoggio che conterrà l'intervallo ritagliato
            Map.Entry<Date, List<String>> lastEntry = iter.next(); //prendo l'ultimo elemento
            Date intervalEnd = lastEntry.getKey(); //ricavo la chiave/data
            intervalMap.put(intervalEnd, lastEntry.getValue()); //la inserisco come primo elemento della mappa d'appoggio

            Calendar cal = Calendar.getInstance();
            cal.setTime(intervalEnd);
            cal.add(Calendar.DAY_OF_MONTH, -6); // intervallo di 7 giorni
            Date intervalStart = cal.getTime();

            // raccogli le date dentro l'intervallo
            while (iter.hasNext()) {
                Map.Entry<Date, List<String>> entry = iter.next();
                Date date = entry.getKey();

                if (!date.before(intervalStart)) {
                    intervalMap.put(date, entry.getValue());
                } else {
                    if(intervalMap.isEmpty()){
                        intervalMap.put(intervalEnd, new ArrayList<>(List.of("0")));
                    }
                    break; // esci, questa data sarà per il prossimo intervallo
                }
            }

            result.add(intervalMap);
        }

        return result;
    }

    public ArrayList<TreeMap<Date, List<String>>> splitMapBy7Days2(TreeMap<Date, List<String>> map) {
        ArrayList<TreeMap<Date, List<String>>> result = new ArrayList<>();
        if (map.isEmpty()) return result; //se mappa è vuota return lista vuota

        // lavoriamo a partire dall'ultima data inserita
        NavigableMap<Date, List<String>> descendingMap = map.descendingMap();
        Iterator<Map.Entry<Date, List<String>>> iter = descendingMap.entrySet().iterator(); //preparo un iterator che contiene i dati della lista ribaltata (cosi gli intervalli partono a ritroso)

        TreeMap<Date, List<String>> intervalMap = new TreeMap<>();
        Date intervalEnd = null;
        Date intervalStart = null;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        while (iter.hasNext()) { //finche l'iterator ha element
            //mappa d'appoggio che conterrà l'intervallo ritagliato
            Map.Entry<Date, List<String>> lastEntry = iter.next(); //prendo l'ultimo elemento
            if (intervalEnd == null) {
                intervalEnd = lastEntry.getKey();

                cal.setTime(intervalEnd);
                cal.add(Calendar.DAY_OF_MONTH, -6); // intervallo di 7 giorni
                //dovresti azzerare secondi
                intervalStart = cal.getTime();
                intervalMap.put(lastEntry.getKey(), lastEntry.getValue());

            } else if (!lastEntry.getKey().before(intervalStart)) {
                intervalMap.put(lastEntry.getKey(), lastEntry.getValue());
            } else {
                while (lastEntry.getKey().before(intervalStart)) {
                    result.add(intervalMap);
                    intervalMap=new TreeMap<>();



                    //aggiorno gli intervalli con -1 e -6
                    cal.setTime(intervalStart);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    intervalEnd = intervalStart;

                    cal.setTime(intervalEnd);
                    cal.add(Calendar.DAY_OF_MONTH, -6); // intervallo di 7 giorni
                    //dovresti azzerare secondi
                    intervalStart = cal.getTime();

                    intervalMap.put(intervalEnd, List.of("0"));


                }

                //ho trovato un intervallo incui sta il next(), tolgo l'entry template vuoto
                intervalMap.clear();
                //e valorizzo al suo posto col valore next()
                intervalMap.put(lastEntry.getKey(), lastEntry.getValue());
            }
        }
        result.add(intervalMap);
        return result;
    }


    public TreeMap<Date, Integer> calculateMeanOfDays(
            TreeMap<Date, List<String>> map) {


        TreeMap<Date, Integer> newmap=new TreeMap<>();

        for (Map.Entry<Date, List<String>> entry : map.entrySet()) {
            newmap.put(entry.getKey(), dayMean(entry.getValue()));
        }

        return newmap;
    }


//FUNCTIONS ON Map<Date, Integer>

    // fa la mediana su una mappa di date, ritornano interi
    public Integer calculateMedianOnMap(TreeMap<Date, Integer> map) {
        if(map.isEmpty() || map==null){
            return 0;
        }
        else{
            List<Integer> mapInteger = new ArrayList<>(map.values());
            return median(mapInteger);
        }

    }

    public Integer calculateDays(TreeMap<Date, Integer> map) {

        return map.size();

    }

    public Integer calculateMax(TreeMap<Date, Integer> map){
        return max_value_of_list(new ArrayList<>(map.values()));
    }











//FUNCTIONS ON VALUES

    private Integer dayMean(List<String> list){

        List<Integer> listNumbers=parserStrToInt(list);
        return mean(listNumbers);

    }

    private List<Integer> parserStrToInt(List<String> list){

        List<Integer> risult=new ArrayList<>();
        for(String e: list){
            Integer i=parseIntSafe(e);
            if(i!=null){
                risult.add(i);
            }
        }
        return risult;
    }

    private Integer parseIntSafe(String s) {
        if (s == null) return null;

        s = s.trim(); // rimuove spazi

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null; // o valore di fallback
        }
    }

    //FORMULE ARITMETICHE
    private Integer median(List<Integer> list){
        if (list == null || list.isEmpty()) return null;

        list.sort(null);
        int n = list.size();
        if(n % 2 == 0){
            return (list.get(n/2 - 1) + list.get(n/2)) / 2;
        } else {
            return list.get(n/2);
        }
    }

    private int mean(List<Integer> list) {
        if (list == null || list.isEmpty()) return 0;

        int sum = 0;
        for (int v : list) {
            sum += v;
        }
        return Math.round((float) sum / list.size()); // arrotonda al numero intero più vicino
    }

    private int max_value_of_list(List<Integer> list){
        int max=0;
        for(Integer i: list){
            if (max<i){
                max=i;
            }
        }
        return max;
    }

    //CALCOLO INDICI
    public float costanza(int giorni_attivi){
        return (float) giorni_attivi /7;
    }

    public float stabilità(int mediana_settimanale, int week_max){
        if(mediana_settimanale==0 || week_max ==0){
            return 0;
        }
        return (float) mediana_settimanale/ (float) week_max;
    }

    public float delta_mediana(int mediana_settimanale, int mediana_precedente){
        return (float) mediana_settimanale-(float) mediana_precedente;
    }

    public float delta_max(int max, int max_prec){

        return (float) max-(float) max_prec;
    }


    //UTILITY
    private Date getTime(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
