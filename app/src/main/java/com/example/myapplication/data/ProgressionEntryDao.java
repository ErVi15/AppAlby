package com.example.myapplication.data;

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
    List<ProgressionEntry> getEntriesForProgression(long progressionId);

    @Query("DELETE FROM progression_entries WHERE progressionId = :progressionId")
    void deleteForProgression(long progressionId);
}
