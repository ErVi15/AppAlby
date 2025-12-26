package com.example.myapplication.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ProgressionRepository {

    private final ProgressionDao progressionDao;
    private final ProgressionEntryDao entryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProgressionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        progressionDao = db.progressionDao();
        entryDao = db.progressionEntryDao();
    }

    // ------------------ PROGRESSIONS ------------------

    public LiveData<List<Progression>> getAllProgressions() {
        return progressionDao.getAll(); //
    }

    public void insertProgression(Progression p) {
        executor.execute(() -> progressionDao.insert(p));
    }

    public void updateProgression(Progression p) {
        executor.execute(() -> progressionDao.update(p));
    }

    public void deleteProgression(Progression p) {
        executor.execute(() -> progressionDao.delete(p));
    }

    // ------------------ ENTRIES ------------------

    public LiveData<List<ProgressionEntry>> getAllEntries() {
        return entryDao.getAllEntries(); // senza WHERE
    }

    public LiveData<List<ProgressionEntry>> getEntries(long progressionId) {
        return entryDao.getEntriesForProgression(progressionId);
    }

    public void insertEntry(ProgressionEntry entry) {
        executor.execute(() -> entryDao.insert(entry));
    }

    public void deleteEntriesForProgression(long progressionId) {
        executor.execute(() -> entryDao.deleteForProgression(progressionId));
    }

    public void deleteEntryById(long id){
        executor.execute(() -> entryDao.deleteEntryById(id));

    }

    public List<Long> debugGetIds() {
        return entryDao.debugGetIds();
    }

}
