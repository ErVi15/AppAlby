package com.example.myapplication.domain.stat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class StatsCalculator {


    //FUNCTIONS ON KEYS

    public Integer calculateMedianSevenLastDays( TreeMap<LocalDate, Integer> map){
        List<Integer> mapInteger=new ArrayList<>(map.values());
        return median(mapInteger);
    }

    public TreeMap<LocalDate, Integer> lastSevenMeansOfProgression(TreeMap<LocalDate, List<String> >map){

        //TreeMap<LocalDate, Integer> map_of_means=calculateDayMeans(map);
        TreeMap<LocalDate, Integer> sevenDaysValues=new TreeMap<>();


        // Ottieni una vista decrescente
        NavigableMap<LocalDate,List<String>> reversed = map.descendingMap();

        // Itera sui primi 7 elementi
        int count = 0;
        for (Map.Entry<LocalDate, List<String>> entry : reversed.entrySet()) {
            if (count >= 7) break;
            Integer day_mean=dayMean(entry.getValue());
            sevenDaysValues.put(entry.getKey(),day_mean);
            count++;
        }

        return sevenDaysValues;

    }





    //LocalDate day = LocalDate.parse(e.getKey(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));



    //FUNCTIONS ON VALUES

    public Integer dayMean(List<String> list){

        List<Integer> listNumbers=parserStrToInt(list);
        return mean(listNumbers);

    }

    public List<Integer> parserStrToInt(List<String> list){


        List<Integer> risult=new ArrayList<>();
        for(String e: list){
            Integer i=parseIntSafe(e);
            if(i!=null){
                risult.add(i);
            }
        }
        return risult;
    }


    public Integer parseIntSafe(String s) {
        if (s == null) return null;

        s = s.trim(); // rimuove spazi

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null; // o valore di fallback
        }
    }

    //FORMULE ARITMETICHE
    public Integer median(List<Integer> list){
        if (list == null || list.isEmpty()) return null;

        list.sort(null);
        int n = list.size();
        if(n % 2 == 0){
            return (list.get(n/2 - 1) + list.get(n/2)) / 2;
        } else {
            return list.get(n/2);
        }
    }


    public int mean(List<Integer> list) {
        if (list == null || list.isEmpty()) return 0;

        int sum = 0;
        for (int v : list) {
            sum += v;
        }
        return Math.round((float) sum / list.size()); // arrotonda al numero intero più vicino
    }




}
