package fr.corentinbringer.endunav.models;

import android.util.Patterns;

public class SignupUser
{
    private String strCompleteName;
    private String strEmailAddress;
    private String strPassword;

    public SignupUser(String completeName, String emailAddress, String password)
    {
        strCompleteName = completeName;
        strEmailAddress = emailAddress;
        strPassword = password;
    }

    public String getStrCompleteName()
    {
        return strCompleteName;
    }


    public String getStrEmailAddress()
    {
        return strEmailAddress;
    }


    public String getStrPassword()
    {
        return strPassword;
    }


    public boolean isEmailValid()
    {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }


    public boolean isPasswordLengthGreaterThan8()
    {
        return getStrEmailAddress().length() >= 8;
    }


    public boolean isCompleteNameLengthGreaterThan2()
    {
        return getStrCompleteName().length() >= 2;
    }
}
