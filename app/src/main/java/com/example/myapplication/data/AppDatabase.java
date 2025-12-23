package com.example.myapplication.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(
        entities = {Progression.class, ProgressionEntry.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // DAO per le progressioni
    public abstract ProgressionDao progressionDao();

    // DAO per le entry
    public abstract ProgressionEntryDao progressionEntryDao();
}

//AppDatabase db = Room.databaseBuilder(
//        context.getApplicationContext(),
//        AppDatabase.class,
//        "my_database"
//).build();
//
//ProgressionDao progressionDao = db.progressionDao();
//ProgressionEntryDao entryDao = db.progressionEntryDao();
