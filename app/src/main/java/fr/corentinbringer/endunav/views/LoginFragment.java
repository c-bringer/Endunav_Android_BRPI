package fr.corentinbringer.endunav.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import fr.corentinbringer.endunav.databinding.FragmentLoginBinding;
import fr.corentinbringer.endunav.models.LoginUser;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import fr.corentinbringer.endunav.viewmodels.LoginViewModel;

public class LoginFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentLoginBinding b;
    private NavController navController;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

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

        //MVVM
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.setRequiredParameters(getContext(), sessionManager, navController);

        //Redirection vers SignupFragment
        b.labelLoginDontHaveAccount.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_signupFragment));

        //Redirection vers ForgetPasswordFragment
        b.labelLoginForgetPassword.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_forgetPasswordFragment));

        //Clique sur le bouton de connexion
        b.buttonLogin.setOnClickListener(v -> {
            final String emailAddress = b.editTextTextEmailAddress.getText().toString();
            final String password = b.editTextTextPassword.getText().toString();
            LoginUser loginUser = new LoginUser(emailAddress, password);

            if(TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrEmailAddress())) {
                Toast.makeText(getContext(), R.string.label_error_email, Toast.LENGTH_LONG).show();
            } else if(!loginUser.isEmailValid()) {
                Toast.makeText(getContext(), R.string.label_error_email_valid, Toast.LENGTH_LONG).show();
            } else if(TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrPassword())) {
                Toast.makeText(getContext(), R.string.label_error_password, Toast.LENGTH_LONG).show();
            } else if(!loginUser.isPasswordLengthGreaterThan8()) {
                Toast.makeText(getContext(), R.string.label_error_password_length, Toast.LENGTH_LONG).show();
            } else {
                loginViewModel.sendLoginRequest(emailAddress, password);
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
                navController.navigate(R.id.action_loginFragment_to_mapFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}