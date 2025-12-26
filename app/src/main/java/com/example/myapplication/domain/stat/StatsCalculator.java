package com.example.myapplication.domain.stat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class StatsCalculator {
//FUNCTION ON TreeMap<Date, List<String>>

    // ottieni gli ultimi 7 giorni di valori
    public TreeMap<Date, List<String>> cutLast7Days(TreeMap<Date, List<String>> map){
        //Date data =map.lastKey(); //data piu recente
        Date data = getTime(); //data odierna

        //data sette giorni prima, il massimo oltre cui non prendere piu entries
        Calendar cal=Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date data_sette_giorni_prima = cal.getTime();

        //Lista delle entries negli ultimi sette giorni
        return (TreeMap<Date,List<String>>) map.tailMap(data_sette_giorni_prima);
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
        List<Integer> mapInteger = new ArrayList<>(map.values());
        return median(mapInteger);
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
