package dev.netanelbcn.locationnotes.utilities;

import com.google.firebase.auth.FirebaseAuth;

public class FBManager {
    private FirebaseAuth mAuth;
    private static FBManager instance;


    private FBManager() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static FBManager getInstance() {
        if (instance == null) {
            instance = new FBManager();
        }
        return instance;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

}
