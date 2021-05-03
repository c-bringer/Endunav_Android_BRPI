package fr.corentinbringer.endunav.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.corentinbringer.endunav.R;
import fr.corentinbringer.endunav.databinding.FragmentLoginBinding;
import fr.corentinbringer.endunav.sharedprefs.SessionManager;

public class LoginFragment extends Fragment
{
    SessionManager sessionManager;
    private FragmentLoginBinding b;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        return b.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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