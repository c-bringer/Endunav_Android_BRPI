package fr.corentinbringer.endunav.models;

import android.util.Patterns;

public class DeleteUser
{
    private String strEmailAddress;

    public DeleteUser(String emailAddress)
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
