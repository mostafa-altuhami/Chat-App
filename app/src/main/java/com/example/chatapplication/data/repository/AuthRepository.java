package com.example.chatapplication.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

public class AuthRepository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseUser getCurrentUser () {
        return auth.getCurrentUser();
    }

    public void signOut () {
        auth.signOut();
    }

    public void signInWithCredential (
            PhoneAuthCredential credential,
            OnCompleteListener<AuthResult> listener
    ) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(listener);

    }
}
