package fr.corentinbringer.endunav.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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
import fr.corentinbringer.endunav.databinding.FragmentSignupBinding;
import fr.corentinbringer.endunav.models.SignupUser;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import fr.corentinbringer.endunav.viewmodels.SignupViewModel;

public class SignupFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentSignupBinding b;
    private NavController navController;
    private SignupViewModel signupViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);

        //MVVM
        signupViewModel = new SignupViewModel(getContext());
        b.setSignupViewModel(signupViewModel);

        //Cacher la toolbar
        MainActivity mainActivity = (MainActivity) getActivity();
        ActivityMainBinding ab = mainActivity.getActivityBinding();
        ab.toolbar.setVisibility(View.GONE);
        ab.bottomNavigation.setVisibility(View.GONE);

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        sessionManager = new SessionManager(getContext());

        //Redirection vers LoginFragment
        b.labelSignupHaveAccount.setOnClickListener(v -> navController.navigate(R.id.action_signupFragment_to_loginFragment));

        //Clique sur bouton d'inscription
        b.buttonSignup.setOnClickListener(v -> {
            final String completeName = b.editTextTextPersonName.getText().toString();
            final String emailAddress = b.editTextTextEmailAddress.getText().toString();
            final String password = b.editTextTextPassword.getText().toString();
            SignupUser signupUser = new SignupUser(completeName, emailAddress, password);

            if(TextUtils.isEmpty(Objects.requireNonNull(signupUser).getStrCompleteName())) {
                Toast.makeText(getContext(), R.string.label_signup_error_name, Toast.LENGTH_LONG).show();
            } else if(!signupUser.isCompleteNameLengthGreaterThan2()) {
                Toast.makeText(getContext(), R.string.label_signup_error_name_length, Toast.LENGTH_LONG).show();
            } else if(TextUtils.isEmpty(Objects.requireNonNull(signupUser).getStrEmailAddress())) {
                Toast.makeText(getContext(), R.string.label_signup_error_email, Toast.LENGTH_LONG).show();
            } else if(!signupUser.isEmailValid()) {
                Toast.makeText(getContext(), R.string.label_signup_error_email_valid, Toast.LENGTH_LONG).show();
            } else if(TextUtils.isEmpty(Objects.requireNonNull(signupUser).getStrPassword())) {
                Toast.makeText(getContext(), R.string.label_signup_error_password, Toast.LENGTH_LONG).show();
            } else if(!signupUser.isPasswordLengthGreaterThan8()) {
                Toast.makeText(getContext(), R.string.label_signup_error_password_length, Toast.LENGTH_LONG).show();
            } else {
                signupViewModel.sendSignupRequest(completeName, emailAddress, password);

                if(signupViewModel.userRegistrationCompleted) {
                    navController.navigate(R.id.action_signupFragment_to_loginFragment);
                }
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();
        //Check if user is signed
        try {
            if (!sessionManager.checkLogin()) {
                navController.navigate(R.id.action_signupFragment_to_mapFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}