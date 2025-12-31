package com.example.myapplication.domain.feedback;


public enum ProgressState {

    PROGRESSO_SANO("desc"),//DX>0, DM>0
    POTENZIALE_NON_CONSOLIDATO("desc2"),//DX>0, DM CIRCA 0
    SOVRACCARICO_STASI("desc3"),//DX CIRCA 0, DM CIRCA 0
    ALLENAMENTO_IRREGOLARE("desc4"),//DX>0, DM CIRCA 0
    REGRESSIONE("desc5"),//DX<0, DM<0
    NOT_SURE("desc6");

    private final String description;

    ProgressState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

//DELTA M = DM = VARIAZIONE MEDIANA
//DELTA X = DX = VARIAZIONE MASSIMO
