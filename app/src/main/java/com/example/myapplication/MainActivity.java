package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    //variabile per richiamare le proprietà impostate per un oggetto di tipo appbar
    private ActivityMainBinding binding;
    //variabile per richiamare tutte le proprietà impostate prendendole da un file activity_main.xml
    //evita l'uso di findViewById()


    private Runnable fabAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //costruttore dell'oggetto ActivityMainBinding a partire dal file activity_main.xml
        //inflate=gonfia ossia metti a schermo

        setContentView(binding.getRoot());
        //Senza questa riga, l’Activity non mostrerebbe nulla. dice cosa mostrare,
        // get root è l'elemento piu esterno del documento xml associato all'activitymainbinding

        setSupportActionBar(binding.toolbar);
        //Se nel tuo layout esiste un elemento <androidx.appcompat.widget.Toolbar> con id toolbar,
        // questa riga: imposta quella Toolbar come ActionBar dell’Activity


        //I fragment sono mini-activity che definiscono un comportamento del layout, sono riutilizzabili,
        //possono vivere dentro la stessa Activity (MainActivity) usando un NavHostFragment
        //E con i NavController gestisce quale fragment è attivo dentro il NavHostFragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //collega la toolbar alla logica di navigazione pt1
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //collega la toolbar alla logica di navigazione pt2

        //gestore tramite nav dei fragment listeners
        navController.addOnDestinationChangedListener((controller, destination, args) -> {

            if (destination.getId() == R.id.FirstFragment || destination.getId() == R.id.SecondFragment) {//la destination viene impostata sulla
                // base del fragment, quando usi NavHostFragment.findNavController(FirstFragment.this).navigate()
                // Mostra FAB
                binding.fab.show();

            } else {
                // Nascondi FAB negli altri fragment
                binding.fab.hide();
                binding.fab.setOnClickListener(null);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String testo = getString(R.string.lorem_ipsum);
            String testo2 = getString(R.string.lorem_ipsum2);

            // TextView
            TextView tv = new TextView(this);
            tv.setText(testo  + testo2);
            tv.setPadding(48, 32, 48, 32);
            tv.setTextSize(14);
            tv.setLineSpacing(0f, 1.2f);

            // ScrollView wrapper
            ScrollView scrollView = new ScrollView(this);
            scrollView.addView(tv);

            // Dialog
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setView(scrollView)
                    .setPositiveButton("OK", null)
                    .show();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public FloatingActionButton getFab() {
        if (binding != null) {
            return binding.fab;
        } else {
            System.out.println("MainActivity "+ "Binding non inizializzato!");
            return null;
        }
    }


    public void setFabAction(Runnable action) {
        this.fabAction = action;
        binding.fab.setOnClickListener(v -> fabAction.run());
    }


}