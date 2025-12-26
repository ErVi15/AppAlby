package com.example.myapplication.domain.feedback;

import com.example.myapplication.domain.stat.StatsCalculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

//legge i dati, e sulla base di algoritmo interno, sceglie cosa impacchettare e restituire in un oggetto ProgressFeedback
    public class FeedbackEvaluator {
        StatsCalculator calculator;


        FeedbackEvaluator(){
            calculator=new StatsCalculator();
        }

        public ProgressFeedback getFeedback(TreeMap<Date, List<String>> map){

            TreeMap<Date, Integer> map_int=calculator.calculateMeanOfDays(calculator.cutLast7Days(map));

            int median=calculator.calculateMedianOnMap(map_int);
            int active_days=calculator.calculateDays(map_int);
            int week_max=calculator.calculateMax(map_int);


            return new ProgressFeedback(
                    calculator.stabilità(median, week_max),
                    calculator.costanza(active_days),
                    median,
                    week_max
            );
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
}
