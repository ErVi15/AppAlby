package com.example.myapplication.domain.feedback;


public enum ProgressState {

    STONKS("Stai spingendo e assimilando il tuo livello abituale, stai ampliando il tuo potenziale", "Suggerimento: continua così"),//DX>0, DM>0
    EMERGENTE("Spesso mostri margine di miglioramento, ma resti stabile","Suggerimento: è il momento per fare definitivamente tuoi questi record, concentrati sulla stabilità del tuo andamento"),//DX>0, DM CIRCA 0
    CONFORT_ZONE("Performance stabile","Suggerimento: ottimo, sei perfettamente a tuo agio! Se vuoi evolvere, prova delle variazioni, nuovi stimoli."),//DX CIRCA 0, DM CIRCA 0
    ALTALENANTE("Stai mostrando progressi alterni","Suggerimento: cerca di lavorare tutti i giorni con lo stesso effort"),//DX>0, DM CIRCA 0
    IN_CALO("Stai mostrando un calo temporaneo, ma il tuo potenziale rimane","Suggerimento: Rallenta un po’ e cura la costanza. Ricorda che un singolo intervallo negativo non cambia il trend"),//DX<0, DM<0
    RIPRESA_DOPO_PAUSA("I dati recenti seguono un periodo di inattività.","Suggerimento: inserire dati almeno per un giorno per cambiare resoconto"),
    AGGIUNGI_ALTRI_GIORNI("Hai iniziato da poco, i dati iniziali non definiscono ancora un trend", "Suggerimento: fai passare almeno 7 giorni dal primo valore inserito"),

    AFFATICATO("C'è del potenziale evidente ma il tuo livello abiutale ne risente" ,"Suggerimento: rallenta leggermente, punta alla costanza, anche se un intervallo sembra negativo l'importante sono i progressi sul lungo termine"),

    CONSOLIDAMENTO("Stai lavorando benissimo sulla tua costanza, i record non sono l'obiettivo in questa fase","Suggerimento: se lo desideri puoi sperimentare nuovi stimoli o aumentare leggermente l’intensità"),

    ABITUALE("Sei stabile ma il margine di miglioramento si riduce","Suggerimento: trova variazioni, nuovi stimoli, RISVEGLIA IL LEONE"),

    FLUTTUANTE("Stai adattandoti agli stimoli, ma ricorda il tuo potenziale è lì", "Suggerimento: insisti, e cerca regolarità, RICORDATI CHI SEI");

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
