package fr.corentinbringer.endunav.views;

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

import fr.corentinbringer.endunav.MainActivity;
import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.ActivityMainBinding;
import fr.corentinbringer.endunav.databinding.FragmentForgetPasswordBinding;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;

public class ForgetPasswordFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentForgetPasswordBinding b;
    private NavController navController;

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

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

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