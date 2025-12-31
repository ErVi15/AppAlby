package com.example.myapplication.domain.feedback;


//classe che impacchetta il risultato e sarà il return di FeedbackEvaluator
public class ProgressFeedback {
    private float stabilità;
    private int mediana;
    private int week_max;
    private float costanza;

    private ProgressState state;
    private String desc;

    public ProgressFeedback(float stabilità, float costanza, int mediana, int week_max) {
        this.stabilità = stabilità;
        this.costanza=costanza;
        this.mediana=mediana;
        this.week_max=week_max;
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








}
