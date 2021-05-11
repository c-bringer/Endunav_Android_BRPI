package fr.corentinbringer.endunav.models;

import android.util.Patterns;

public class ResetPasswordUser
{
    private String strEmailAddress;

    public ResetPasswordUser(String emailAddress)
    {
        this.strEmailAddress = emailAddress;
    }


    public String getStrEmailAddress()
    {
        return strEmailAddress;
    }


    public boolean isEmailValid()
    {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }
}
