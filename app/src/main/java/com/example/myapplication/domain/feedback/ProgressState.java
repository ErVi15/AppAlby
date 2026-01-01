package com.example.myapplication.domain.feedback;


public enum ProgressState {

    PROGRESSO_SANO("Stai spingendo e assimilando, apprendimento corretto", "Suggerimento: continua così"),//DX>0, DM>0
    POTENZIALE_NON_CONSOLIDATO("Riesci a raggiungere nuovi massimi, ma non li rendi stabili","Suggerimento: lavora di più con valori di confort, devi rendere il tuo andamento stabile"),//DX>0, DM CIRCA 0
    SOVRACCARICO_STASI("Performance stabile","Suggerimento: complimenti, sei perfettamente a tuo agio! ma se intendi migliorare ancora devi aggiungere variazioni, dare nuovi stimoli"),//DX CIRCA 0, DM CIRCA 0
    ALLENAMENTO_IRREGOLARE("Miglioramenti saltuari","Suggerimento: cerca di lavorare tutti i giorni con lo stesso effort"),//DX>0, DM CIRCA 0
    REGRESSIONE("Fatica accumulata o carico eccessivo","Suggerimento: rilassati, abbassa la complessità dell'esercizio, e cura pulizia e rilassamento"),//DX<0, DM<0
    RIPRESA_DOPO_PAUSA("i dati recenti seguono un periodo di inattività.","Suggerimento: inserire almeno per due giorni per cambiare resoconto"),
    AGGIUNGI_ALTRI_GIORNI("hai iniziato da poco la progressione i dati non sarebbero indicativi", "Suggerimento: fai passare almeno 7 giorni dal primo valore inserito");

    private final String description;
    private final String sugguestion;

    ProgressState(String description, String sugguestion) {
        this.description = description;
        this.sugguestion = sugguestion;
    }

    public String getDescription() {
        return description;
    }
    public String getSugguestion(){
        return sugguestion;
    }
}

//DELTA M = DM = VARIAZIONE MEDIANA
//DELTA X = DX = VARIAZIONE MASSIMO
