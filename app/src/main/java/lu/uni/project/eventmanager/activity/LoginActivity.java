package lu.uni.project.eventmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.adapter.EventsAdapter;
import lu.uni.project.eventmanager.pojo.User;
import lu.uni.project.eventmanager.util.PreferenceKeys;
import lu.uni.project.eventmanager.util.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private static String TAG= " LoginActivity ";
    private int RC_SIGN_IN= 1;
    private Button loginButton;
    private EditText emailID;
    private EditText pwd;
    private boolean pwdState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        loginButton = findViewById(R.id.login_button);
        emailID = findViewById(R.id.emailID);
        pwd = findViewById(R.id.password);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        changeStatusBarColor(this);
        pwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (pwd.getRight() - pwd.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                       if(pwdState){
                           pwd.setTransformationMethod(new PasswordTransformationMethod());
                           pwd.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_hide), null);
                       }else{
                           pwd.setTransformationMethod(null);
                           pwd.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_remove_red_eye_black_24dp), null);
                       }
                        pwdState=!pwdState;
                        return true;
                    }
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                final String password = pwd.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference userRef = database.getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user= dataSnapshot.getValue(User.class);
                                        if(user!=null){
                                            SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profilePhotoURI, user.getProfileImgURL()!=null?user.getProfileImgURL():"");
                                            SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileFirstName,user.getFirstName()!=null?user.getFirstName():"");
                                            SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileLastName, user.getLastName()!=null?user.getLastName():"");
                                            SharedPreferencesHelper.put(getApplicationContext(), PreferenceKeys.profileDisplayName, user.getDisplayName()!=null?user.getDisplayName():"");
                                        }
                                        userRef.removeEventListener(this);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(), "Password is less than 6 characters!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });


        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,BottomNavigationActivity.class));
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        findViewById(R.id.Registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
        setupUI(findViewById(R.id.loginParent));
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
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
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
                            User userObj= new User();
                            userObj.setDisplayName(user.getDisplayName());
                            userObj.setUid(user.getUid());
                            userObj.setEmailID(user.getEmail());
                            userObj.setPhoneNumber(user.getPhoneNumber());
                            userObj.setProfileImgURL(user.getPhotoUrl().toString());
                            db.setValue(userObj);
                            startBottomoNavActivity();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"Signin failed!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void startBottomoNavActivity(){
        startActivity(new Intent(this, BottomNavigationActivity.class));
        finish();
    }
    public void changeStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorWhite));
            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags = systemUiVisibilityFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        if(activity!=null & activity.getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
