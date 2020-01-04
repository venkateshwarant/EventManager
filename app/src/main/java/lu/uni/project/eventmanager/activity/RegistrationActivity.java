package lu.uni.project.eventmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.pojo.User;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

private static final int RESULT_LOAD_IMAGE = 1;
    RadioGroup radioGroup;
    RadioButton radioButton;
    CountryCodePicker ccp;
    EditText phoneNumber;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfirmPassword;
    EditText inputFirstName;
    EditText inputLastName;
    RadioGroup genderGroup;
    EditText addressLine1;
    EditText addressLine2;
    EditText postalCode;
    EditText Country;
    CountryPicker picker;
    EditText countryName;
    ImageView imageToUpload;
    Button regBtn;
    private static Uri imageURI;
    private FirebaseAuth auth;
    private ProgressBar progressBarHolder;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        regBtn = (Button) findViewById(R.id.regBtn) ;
        inputEmail = (EditText) findViewById(R.id.mailID) ;
        inputPassword = findViewById(R.id.pwd) ;
        inputConfirmPassword = findViewById(R.id.confirm_pwd) ;
        progressBarHolder =  findViewById(R.id.progressBarHolder);
        inputFirstName =  findViewById(R.id.First_name_Text1);
        inputLastName =  findViewById(R.id.Last_name_Text2);
        addressLine1 =  findViewById(R.id.Address_Line1);
        addressLine2 =  findViewById(R.id.Address_Line2);
        postalCode =  findViewById(R.id.Postal_Code);
        countryName =  findViewById(R.id.countryName);
        phoneNumber =  findViewById(R.id.phone_number);
        imageToUpload.setOnClickListener(this);

        radioGroup=findViewById(R.id.radio_group);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        init();
        setupUI(findViewById(R.id.registerParent));
        changeStatusBarColor(RegistrationActivity.this);
        findViewById(R.id.regBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarHolder.setVisibility(View.VISIBLE);
                hideKeyboard(RegistrationActivity.this);
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password) | TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confirmPassword.contentEquals(password)) {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                    return;
                }

//                    progressBar.setVisibility(View.VISIBLE);
                //create user
                progressBarHolder.setAnimation(inAnimation);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed. " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                } else {
                                    final String[] imgDownloadURL = {""};
                                    if(imageURI!=null){
                                        try {
                                            StorageReference storage= FirebaseStorage.getInstance().getReference();
                                            StorageReference imgRef= storage.child("images").child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            InputStream profileImg = getContentResolver().openInputStream(imageURI);

                                            final StorageReference img= imgRef.child("user.jpg");
                                            UploadTask uploadTask = img.putStream(profileImg);

                                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if(task.isSuccessful()){

                                                        img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                imgDownloadURL[0] = task.getResult().toString();
                                                                DatabaseReference db= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                User userObj= new User();
                                                                userObj.setDisplayName(inputFirstName.getText()+" "+inputLastName.getText());
                                                                userObj.setFirstName(inputFirstName.getText().toString());
                                                                String address= addressLine1.getText()+"\n"+addressLine2.getText();
                                                                userObj.setAddress(address);
                                                                RadioButton radButton = findViewById(radioGroup.getCheckedRadioButtonId());
                                                                userObj.setGender(radButton.getText().toString());
                                                                userObj.setPostalCode(postalCode.getText().toString());
                                                                userObj.setLastName(inputLastName.getText().toString());
                                                                userObj.setPhoneNumber(ccp.getSelectedCountryCodeWithPlus()+phoneNumber.getText().toString());
                                                                userObj.setEmailID(email);
                                                                userObj.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                userObj.setProfileImgURL(imgDownloadURL[0]);
                                                                db.setValue(userObj);

                                                                Toast.makeText(RegistrationActivity.this, "Registered succesfully!", Toast.LENGTH_SHORT).show();


                                                                progressBarHolder.setAnimation(outAnimation);
                                                                progressBarHolder.setVisibility(View.GONE);
                                                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(RegistrationActivity.this, "Profile image upload failed",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } catch (FileNotFoundException e) {
                                            Toast.makeText(RegistrationActivity.this, "Image not found",Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        DatabaseReference db= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        User userObj= new User();
                                        userObj.setDisplayName(inputFirstName.getText()+" "+inputLastName.getText());
                                        userObj.setFirstName(inputFirstName.getText().toString());
                                        String address= addressLine1.getText()+"\n"+addressLine2.getText()+"\nPostal code:"+postalCode.getText().toString();
                                        userObj.setAddress(address);
                                        userObj.setPhoneNumber(phoneNumber.getText().toString());
                                        userObj.setEmailID(email);
                                        userObj.setProfileImgURL(imgDownloadURL[0]);
                                        db.setValue(userObj);

                                        Toast.makeText(RegistrationActivity.this, "Registered succesfully!", Toast.LENGTH_SHORT).show();

                                        progressBarHolder.setAnimation(outAnimation);
                                        progressBarHolder.setVisibility(View.GONE);
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                        finish();
                                    }



                                }
                            }
                        });
            }
        });


        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {

            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                countryName = (EditText)findViewById(R.id.countryName);

               // countryIcon =  findViewById(R.id.countryIcon

                countryName.setText(name);

                //countryIcon.setImageResource(flagDrawableResID);

                picker.dismiss();

            }
        });

    }

    public void init()
    {
        ccp=findViewById(R.id.ccp);
        phoneNumber=findViewById(R.id.Phone_number);
    }
    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);
    }

    public void openPicker(View view){
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imageToUpload) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null) {
            imageURI = data.getData();
            imageToUpload.setImageURI(imageURI);
            imageToUpload.invalidate();
        }
    }

    public void changeStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.Orange));
            View decorView = window.getDecorView();
            int systemUiVisibilityFlags = decorView.getSystemUiVisibility();
            systemUiVisibilityFlags = systemUiVisibilityFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(systemUiVisibilityFlags);
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                    hideSoftKeyboard(RegistrationActivity.this);
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
