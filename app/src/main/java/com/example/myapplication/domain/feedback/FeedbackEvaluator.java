package com.example.myapplication.domain.feedback;

import com.example.myapplication.domain.stat.StatsCalculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

//legge i dati, e sulla base di algoritmo interno, sceglie cosa impacchettare e restituire in un oggetto ProgressFeedback
    public class FeedbackEvaluator {
        StatsCalculator calculator;


        public FeedbackEvaluator(){
            calculator=new StatsCalculator();
        }

        public ProgressFeedback getFeedback(TreeMap<Date, List<String>> map, boolean is_second_last){

            TreeMap<Date, Integer> map_int=calculator.calculateMeanOfDays(calculator.cutLast7Days(map, is_second_last));
//            TreeMap<Date, Integer> map_int=new TreeMap<>();
//            map_int.put(map.firstKey(), 3);
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(map.firstKey());
//            cal.add(Calendar.DAY_OF_MONTH, -1);
//            Date originalDate = cal.getTime(); // esempio
//
//            cal.add(Calendar.DAY_OF_MONTH, -2);
//            Date originalDate2 = map.firstKey(); // esempio
//
//            map_int.put(originalDate, 5);
//            map_int.put(originalDate2, 6);


            int median=calculator.calculateMedianOnMap(map_int);
            int active_days=calculator.calculateDays(map_int);
            int week_max=calculator.calculateMax(map_int);

            Date starting_day=null;
            if(map.size() !=0){
                starting_day=map.firstKey();
            }


            return new ProgressFeedback(
                    calculator.stabilità(median, week_max),
                    calculator.costanza(active_days),
                    median,
                    week_max,
                    active_days,
                    starting_day
            );
        }

        public ArrayList<Integer[]> prepareDataForChart(TreeMap<Date, List<String>> map){

            ArrayList<TreeMap<Date, Integer>> treeMapArrayList=new ArrayList<>();

            for(TreeMap<Date, List<String>> e: calculator.splitMapBy7Days(map)){
                treeMapArrayList.add(calculator.calculateMeanOfDays(e));
            }

            ArrayList<Integer[]> median_for_each_week=new ArrayList<>();
            for( TreeMap<Date, Integer> f: treeMapArrayList){
                median_for_each_week.add(new Integer[]{calculator.calculateMedianOnMap(f), calculator.calculateMax(f)});

            }
            return median_for_each_week;
        }

        float getDeltaMedian(ProgressFeedback week, ProgressFeedback last_week){
            return calculator.delta_mediana(
                    week.getMediana(),
                    last_week.getMediana());
        }

        float getDeltaMax(ProgressFeedback week, ProgressFeedback last_week){
            return calculator.delta_max(
                    week.getWeekMax(),
                    last_week.getWeekMax());
        }

        /*
        CASO MANCANTI
        DX	DM	Significato possibile
        0	+	Recupero senza stimolo
        −	0	Perdita senza carico
        0	−	Affaticamento passivo
        −	+	Adattamento anomalo / misurazione incoerente
        */
        public void setProgressState(ProgressFeedback week, ProgressFeedback last_week){

            float dm=0;
            float dx=0;
            dm=getDeltaMedian(week, last_week);
            dx=getDeltaMax(week, last_week);

            if(last_week.getActive_days()>0.25){

                if(week.getActive_days()>0.25) {

                    if (Math.abs(dx) < 1 && Math.abs(dm) < 1) { //dati variano di pochissimo
                        week.setState(ProgressState.SOVRACCARICO_STASI);
                    } else if (dx > 0 && Math.abs(dm) < 1) { //dati mostrano potenzialità ma complessivamente variano di pocchissimo
                        week.setState(ProgressState.POTENZIALE_NON_CONSOLIDATO);
                    } else {
                        if (dx > 0 && dm > 0) {
                            week.setState(ProgressState.PROGRESSO_SANO);
                        }  else if (dx > 0 && dm < 0) {
                            week.setState(ProgressState.ALLENAMENTO_IRREGOLARE);
                        } else if (dx < 0 && dm < 0) {
                            week.setState(ProgressState.REGRESSIONE);
                        }
                    }
                } else {
                    week.setState(ProgressState.RIPRESA_DOPO_PAUSA);
                }
            } else {
                week.setState(ProgressState.AGGIUNGI_ALTRI_GIORNI);
            }

        }


}
