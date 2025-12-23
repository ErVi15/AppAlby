package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

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

//                binding.fab.setOnClickListener(v -> {
//                    Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
//
//                    if (navHostFragment instanceof NavHostFragment) {
//                        Fragment currentFragment =
//                                ((NavHostFragment) navHostFragment)
//                                        .getChildFragmentManager()
//                                        .getFragments()
//                                        .get(0);
//
//                        System.out.println("Fragment perceived: " + currentFragment.toString());
//                        if (currentFragment instanceof FirstFragment) {
//                            System.out.println("FirstFragment Instance is perceived");
//                            ((FirstFragment) currentFragment).addNewItem();
//                        } else if (currentFragment instanceof SecondFragment) {
//                            System.out.println("SecondFragment Instance is perceived");
//                            ((SecondFragment) currentFragment).addNewItem();
//                        }
//                    }
//                    Snackbar.make(v, "Azione del primo fragment", Snackbar.LENGTH_LONG).show();
//                });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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