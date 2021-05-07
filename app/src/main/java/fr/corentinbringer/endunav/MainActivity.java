package fr.corentinbringer.endunav;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.View;

import fr.corentinbringer.endunav.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding b;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }


    public ActivityMainBinding getActivityBinding()
    {
        return b;
    }


    public void configureBottomNavigation()
    {
        b.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            boolean result = false;

            switch(item.getItemId()) {
                case R.id.map_tab:
                    navController.navigate(R.id.mapFragment);
                    result = true;
                    break;
                case R.id.settings_tab:
                    navController.navigate(R.id.settingsFragment);
                    result = true;
                    break;
            }

            return result;
        });
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    public void setActionBar(Integer heading)
    {
        b.toolbar.setVisibility(View.VISIBLE);
        b.toolbar.setTitle(heading);
    }
}