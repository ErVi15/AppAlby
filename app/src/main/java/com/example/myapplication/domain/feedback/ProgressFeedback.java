package com.example.myapplication.domain.feedback;


import java.util.Date;

//classe che impacchetta il risultato e sarà il return di FeedbackEvaluator
public class ProgressFeedback {
    private float stabilità; //solidità: quanto ti avvicini di media al tuo max
    private int mediana; //i tuoi valori tipici
    private int week_max; //il tuo record settimanale
    private float costanza; //quanti giorni sei attivo in una settimana
    private int active_days;
    private Date starting_day;

    private ProgressState state;
    private String desc;

    public ProgressFeedback(float stabilità, float costanza, int mediana, int week_max, int active_days, Date starting_day) {
        this.stabilità = stabilità;
        this.costanza=costanza;
        this.mediana=mediana;
        this.week_max=week_max;
        this.active_days=active_days;
        this.starting_day=starting_day;
        this.desc="empty description";
    }

    public String getDesc() {
        return desc;
    }

    public ProgressState getState() {
        return state;
    }

    public void setState(ProgressState state) {
        this.state = state;
        this.desc=state.getDescription();
    }

    public int getMediana(){
        return this.mediana;
    }

    public int getWeekMax(){
        return this.week_max;
    }

    public float getStabilità() {
        return stabilità;
    }

    public float getCostanza(){
        return this.costanza;
    }

    public int getActive_days(){
        return this.active_days;
    }

    public Date getStarting_day(){
        return this.starting_day;
    }
}
