package fr.corentinbringer.endunav.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import fr.corentinbringer.endunav.MainActivity;
import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.ActivityMainBinding;
import fr.corentinbringer.endunav.databinding.FragmentUpdateAccountDetailsBinding;
import fr.corentinbringer.endunav.models.DeleteUser;
import fr.corentinbringer.endunav.models.UpdateUser;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import fr.corentinbringer.endunav.viewmodels.UpdateAccountDetailsViewModel;

public class UpdateAccountDetailsFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentUpdateAccountDetailsBinding b;
    private NavController navController;
    private UpdateAccountDetailsViewModel updateAccountDetailsViewModel;

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

        //MVVM
        updateAccountDetailsViewModel = ViewModelProviders.of(this).get(UpdateAccountDetailsViewModel.class);
        updateAccountDetailsViewModel.setRequiredParameters(getContext(), sessionManager, navController);
        updateAccountDetailsViewModel.emailAddress.set("");

        //Remplis le champ email
        final String email = sessionManager.getKeyEmail();
        b.editTextTextEmailAddress.setText(email);

        //AlertDialog
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    updateAccountDetailsViewModel.sendDeleteAccountRequest(email);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        };

        //Mettre à jours les informations du compte
        b.buttonUpdate.setOnClickListener(v -> {
            final String emailAddress = b.editTextTextEmailAddress.getText().toString();
            String password = b.editTextTextPassword.getText().toString();
            UpdateUser updateUser = new UpdateUser(email, emailAddress, password);

            if(TextUtils.isEmpty(Objects.requireNonNull(updateUser).getStrEmailAddress())) {
                Toast.makeText(getContext(), R.string.label_error_email, Toast.LENGTH_LONG).show();
            } else if(!updateUser.isEmailValid()) {
                Toast.makeText(getContext(), R.string.label_error_email_valid, Toast.LENGTH_LONG).show();
            } else {
                updateAccountDetailsViewModel.sendUpdateAccountDetailsRequest(email, emailAddress, password);
            }
        });

        //Supprimer mon compte
        b.buttonDeleteAccount.setOnClickListener(v -> {
            DeleteUser deleteUser = new DeleteUser(email);

            if(TextUtils.isEmpty(Objects.requireNonNull(deleteUser).getStrEmailAddress())) {
                Toast.makeText(getContext(), R.string.label_error_email, Toast.LENGTH_LONG).show();
            } else if(!deleteUser.isEmailValid()) {
                Toast.makeText(getContext(), R.string.label_error_email_valid, Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.label_do_you_want_to_delete_your_account).setPositiveButton("Oui", dialogClickListener)
                       .setNegativeButton("Non", dialogClickListener).show();
            }
        });
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