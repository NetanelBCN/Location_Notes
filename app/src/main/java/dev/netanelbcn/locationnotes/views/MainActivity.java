package dev.netanelbcn.locationnotes.views;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.databinding.ActivityMainBinding;
import dev.netanelbcn.locationnotes.utilities.DataManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialTextView Main_MTV_title;
    private MaterialButton Main_MB_signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_list, R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        findViews();
        setupClicks();
        this.Main_MTV_title.setText("Welcome " + DataManager.getInstance().getUserName() + " 👋");
    }

    private void setupClicks() {
        this.Main_MB_signout.setOnClickListener(v -> {
            signOut();
        });
    }

    private void signOut() {
        DataManager.getInstance().getFbManager().getmAuth().signOut();

        DataManager.getInstance().setUserId(null);
        DataManager.getInstance().setUserName(null);
        DataManager.getInstance().getNotes().clear();
        DataManager.getInstance().getAdapter().notifyDataSetChanged();
        // Navigate back to Login/Registration screen
        Intent intent = new Intent(MainActivity.this, dev.netanelbcn.locationnotes.views.Login_Reg_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
        startActivity(intent);
        finish();
    }

    private void findViews() {
        this.Main_MTV_title = findViewById(R.id.Main_MTV_title);
        this.Main_MB_signout = findViewById(R.id.Main_MB_signout);
    }


}