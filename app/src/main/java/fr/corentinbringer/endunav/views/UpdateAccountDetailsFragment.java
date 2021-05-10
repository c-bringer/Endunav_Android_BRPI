package fr.corentinbringer.endunav.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.corentinbringer.endunav.MainActivity;
import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.ActivityMainBinding;
import fr.corentinbringer.endunav.databinding.FragmentUpdateAccountDetailsBinding;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;

public class UpdateAccountDetailsFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentUpdateAccountDetailsBinding b;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_update_account_details, container, false);

        //Affiche BottomNavigationView
        MainActivity mainActivity = (MainActivity) getActivity();
        ActivityMainBinding ab = mainActivity.getActivityBinding();
        ab.bottomNavigation.setVisibility(View.VISIBLE);

        //Configure BottomNavigationView et affiche ActionBar
        mainActivity.configureBottomNavigation();
        mainActivity.setActionBar(R.string.label_menu_account_details);

        Toolbar toolbar = ab.toolbar;
        mainActivity.setSupportActionBar(toolbar);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        sessionManager = new SessionManager(getContext());
    }


    @Override
    public void onResume()
    {
        super.onResume();
        //Check if user is signed
        try {
            if (sessionManager.checkLogin()) {
                navController.navigate(R.id.action_updateAccountDetailsFragment_to_loginFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}