package com.example.myapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProgressionEntryDao {

    @Insert
    void insert(ProgressionEntry entry);

    @Insert
    void insertAll(List<ProgressionEntry> entries);

    @Query("SELECT * FROM progression_entries WHERE progressionId = :progressionId ORDER BY date ASC")
    LiveData<List<ProgressionEntry>> getEntriesForProgression(long progressionId);

    @Query("DELETE FROM progression_entries WHERE progressionId = :progressionId")
    void deleteForProgression(long progressionId);

    @Query("DELETE FROM progression_entries WHERE id =:id")
    void deleteEntryById(long id);

    @Query("SELECT * FROM progression_entries")
    LiveData<List<ProgressionEntry>> getAllEntries();

    @Query("SELECT id FROM progression_entries")
    List<Long> debugGetIds();

}
