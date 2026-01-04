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

    public String u_misura;

    public String option;

    public Progression(@NonNull String name, String u_misura, String option) {
        this.name = name;
        this.u_misura= u_misura;
        this.option=option;
    }
}

