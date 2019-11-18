package lu.uni.project.eventmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.pojo.User;
import lu.uni.project.eventmanager.util.PreferenceKeys;
import lu.uni.project.eventmanager.util.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private static String TAG= " LoginActivity ";
    private int RC_SIGN_IN= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profilePhotoURI, account.getPhotoUrl());
                SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileFirstName, account.getGivenName());
                SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileLastName, account.getFamilyName());
                SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileDisplayName, account.getDisplayName());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference db= FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
                            db.child(user.getUid());
                            User userObj= new User();
                            userObj.setDisplayName(user.getDisplayName());
                            userObj.setUid(user.getUid());
//                            ArrayList<String> eId= new ArrayList();
//                            eId.add(user.getEmail());
//                            userObj.setEmailID(eId);
                            userObj.setEmailID(user.getEmail());
//                            ArrayList<String> pNo= new ArrayList();
//                            pNo.add(user.getPhoneNumber());
//                            userObj.setPhoneNumber(pNo);
                            userObj.setPhoneNumber(user.getPhoneNumber());
                            userObj.setProfileImgURL(user.getPhotoUrl().toString());
                            db.setValue(userObj);

                            startBottomoNavActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void startBottomoNavActivity(){
        startActivity(new Intent(this, BottomNavigationActivity.class));
        finish();
    }
}
