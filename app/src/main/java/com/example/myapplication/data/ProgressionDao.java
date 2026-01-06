package com.example.myapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ProgressionDao {

    // Inserisce una singola Progression e ritorna l'ID generato
    @Insert
    long insert(Progression progression);

    // Inserisce più Progression in un batch
    @Insert
    List<Long> insertAll(List<Progression> progressions);

    // Aggiorna una Progression esistente
    @Update
    void update(Progression progression);

    @Query("UPDATE progressions SET costanza_desiderata = :value WHERE id = :progressionId")
    void updateCostanzaDesiderata(long progressionId, int value);

    @Query("UPDATE progressions SET valore_desiderato = :value WHERE id = :progressionId")
    void updateValoreDesiderato(long progressionId, int value);


    // Cancella una Progression specifica
    @Delete
    void delete(Progression progression);

    // Ottieni tutte le progressioni ordinate per nome
    @Query("SELECT * FROM progressions ORDER BY name ASC")
    LiveData<List<Progression>> getAll();

    @Query("SELECT * FROM progressions WHERE id = :id")
    Progression getByIdLive(long id);

    // Cancella una Progression tramite ID
    @Query("DELETE FROM progressions WHERE id = :id")
    void deleteById(long id);

}
