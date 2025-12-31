package com.example.myapplication.domain.feedback;

import com.example.myapplication.domain.stat.StatsCalculator;

import java.util.ArrayList;
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
            if (dx > 0 && dm > 0) {
                week.setState(ProgressState.PROGRESSO_SANO);
            } else if (dx > 0 && Math.abs(dm) < 1) {
                week.setState(ProgressState.POTENZIALE_NON_CONSOLIDATO);
            } else if (Math.abs(dx) < 1 && Math.abs(dm) < 1) {
                week.setState(ProgressState.SOVRACCARICO_STASI);
            } else if (dx > 0 && dm < 0) {
                week.setState(ProgressState.ALLENAMENTO_IRREGOLARE);
            } else if (dx < 0 && dm < 0) {
                week.setState(ProgressState.REGRESSIONE);
            }

        }

}
