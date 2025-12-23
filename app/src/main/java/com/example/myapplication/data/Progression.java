package com.example.myapplication.data;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progressions")
public class Progression {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

}

