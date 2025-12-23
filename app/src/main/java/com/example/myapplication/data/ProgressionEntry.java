package com.example.myapplication.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "progression_entries",
        foreignKeys = @ForeignKey(
                entity = Progression.class,
                parentColumns = "id",
                childColumns = "progressionId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("progressionId")
)
public class ProgressionEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long progressionId;

    @NonNull
    public Date date;

    public int value; // ripetizioni, bpm, peso, ecc.
}
