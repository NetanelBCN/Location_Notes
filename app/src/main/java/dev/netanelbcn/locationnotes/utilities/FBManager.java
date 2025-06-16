package dev.netanelbcn.locationnotes.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FBManager {
    private FirebaseAuth mAuth;
    private static FBManager instance;
    private FirebaseDatabase FBDb;

    public FirebaseDatabase getFBDb() {
        return FBDb;
    }

    private FBManager() {
        this.mAuth = FirebaseAuth.getInstance();
        FBDb = FirebaseDatabase.getInstance();

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
