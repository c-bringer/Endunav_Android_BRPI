package fr.corentinbringer.endunav.views;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import fr.corentinbringer.endunav.MainActivity;
import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.ActivityMainBinding;
import fr.corentinbringer.endunav.databinding.FragmentSettingsBinding;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;

import static androidx.core.content.ContextCompat.getSystemService;

public class SettingsFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentSettingsBinding b;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        //Affiche BottomNavigationView
        MainActivity mainActivity = (MainActivity) getActivity();
        ActivityMainBinding ab = mainActivity.getActivityBinding();
        ab.bottomNavigation.setVisibility(View.VISIBLE);

        //Configure BottomNavigationView et affiche ActionBar
        mainActivity.configureBottomNavigation();
        mainActivity.setActionBar(R.string.label_menu_settings);

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        sessionManager = new SessionManager(getContext());

        //Inflater la ListView
        String[] listItem = getResources().getStringArray(R.array.array_label_settings);
        b.settingsList.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_settings, listItem));

        b.settingsList.setOnItemClickListener(((parent, view1, position, id) -> {
            switch(position) {
                case 0:
                    //Redirection vers modifier les infos
                    break;
                case 1:
                    //Copier un lien d'invitation dans le presse papier
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(getContext(), ClipboardManager.class);
                    ClipData clipData = ClipData.newPlainText("invitation", getResources().getString(R.string.label_invitation));
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), R.string.label_inviation_copying, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    //Deconnecte l'utilisateur
                    sessionManager.logoutUser();
                    navController.navigate(R.id.action_settingsFragment_to_loginFragment);
                    Toast.makeText(getContext(), R.string.label_disconnected, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getContext(), R.string.label_error, Toast.LENGTH_LONG).show();
            }
        }));
    }


    @Override
    public void onResume()
    {
        super.onResume();
        //Check if user is signed
        try {
            if (!sessionManager.checkLogin()) {
                navController.navigate(R.id.action_settingsFragment_to_loginFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}