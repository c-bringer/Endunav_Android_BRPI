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
import fr.corentinbringer.endunav.databinding.FragmentForgetPasswordBinding;
import fr.corentinbringer.endunav.models.ResetPasswordUser;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;
import fr.corentinbringer.endunav.viewmodels.ForgetPasswordViewModel;

public class ForgetPasswordFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentForgetPasswordBinding b;
    private NavController navController;
    private ForgetPasswordViewModel forgetPasswordViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password, container, false);

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
        forgetPasswordViewModel = ViewModelProviders.of(this).get(ForgetPasswordViewModel.class);
        forgetPasswordViewModel.setRequiredParameters(getContext());

        //Renvoie un nouveau mot de passe par mail
        b.buttonForgetPassword.setOnClickListener(v -> {
            final String emailAddress = b.editTextTextEmailAddress.getText().toString();
            ResetPasswordUser resetPasswordUser = new ResetPasswordUser(emailAddress);

            if(TextUtils.isEmpty(Objects.requireNonNull(resetPasswordUser).getStrEmailAddress())) {
                Toast.makeText(getContext(), R.string.label_error_email, Toast.LENGTH_LONG).show();
            } else if(!resetPasswordUser.isEmailValid()) {
                Toast.makeText(getContext(), R.string.label_error_email_valid, Toast.LENGTH_LONG).show();
            } else {
                forgetPasswordViewModel.sendResetPasswordRequest(emailAddress);
            }
        });

        //Redirection vers LoginFragment
        b.labelForgetPasswordHavePassword.setOnClickListener(v -> navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment));
    }


    @Override
    public void onResume()
    {
        super.onResume();
        //Check if user is signed
        try {
            if (!sessionManager.checkLogin()) {
                navController.navigate(R.id.action_forgetPasswordFragment_to_mapFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}