package com.example.myapplication.domain.feedback;


public enum ProgressState {

    PROGRESSO_SANO("Stai spingendo e assimilando, apprendimento corretto", "Suggerimento: continua così"),//DX>0, DM>0
    POTENZIALE_NON_CONSOLIDATO("Riesci a raggiungere nuovi massimi, ma non li rendi stabili","Suggerimento: lavora di più con valori di confort, devi rendere il tuo andamento stabile"),//DX>0, DM CIRCA 0
    SOVRACCARICO_STASI("Performance stabile","Suggerimento: complimenti, sei perfettamente a tuo agio! ma se intendi migliorare ancora devi aggiungere variazioni, dare nuovi stimoli"),//DX CIRCA 0, DM CIRCA 0
    ALLENAMENTO_IRREGOLARE("Miglioramenti saltuari","Suggerimento: cerca di lavorare tutti i giorni con lo stesso effort"),//DX>0, DM CIRCA 0
    REGRESSIONE("Fatica accumulata o carico eccessivo","Suggerimento: rilassati, abbassa la complessità dell'esercizio, e cura pulizia e rilassamento"),//DX<0, DM<0
    RIPRESA_DOPO_PAUSA("I dati recenti seguono un periodo di inattività.","Suggerimento: inserire dati almeno per due giorni per cambiare resoconto"),
    AGGIUNGI_ALTRI_GIORNI("Hai iniziato da poco la progressione i dati non sarebbero indicativi", "Suggerimento: fai passare almeno 7 giorni dal primo valore inserito"),

    AFFATICAMENTO_PASSIVO("C'è del potenziale evidente ma i carichi sono alti" ,"Suggerimento: rallenta leggermente, punta alla costanza"),

    RECUPERO_SENZA_STIMOLO("Stai lavorando benissimo sulla tua costanza, i record non sono l'obiettivo in questa fase","Suggerimento: puoi sperimentare nuovi stimoli o variare leggermente l’intensità"),

    PERDITA_DI_PICCO("Sei stabile ma i picchi non sono stabili","Suggerimento: potresti inserire stimoli"),

    ADATTMENTO_INCOERENTE("C'è molto potenziale, ma devi adattarti agli stimoli", "Suggerimento: insisti, e cerca regolarità");

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
