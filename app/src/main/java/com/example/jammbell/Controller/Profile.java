package com.example.jammbell.Controller;

import com.example.jammbell.View.IProfileView;

public class Profile implements IProfile
{
    IProfileView ProfileView;

    public Profile(IProfileView ProfileView) {
        this.ProfileView = ProfileView;
    }


}
