package lu.uni.project.eventmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lu.uni.project.eventmanager.R;
import lu.uni.project.eventmanager.adapter.EventsAdapter;
import lu.uni.project.eventmanager.fragment.HomeFragment;
import lu.uni.project.eventmanager.pojo.Event;
import lu.uni.project.eventmanager.pojo.User;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

private static final int RESULT_LOAD_IMAGE = 1;
    RadioGroup radioGroup;
    RadioButton radioButton;
    CountryCodePicker ccp;
    EditText phoneNumber;
    EditText inputEmail;
    EditText inputFirstName;
    EditText inputLastName;
    EditText addressLine1;
    EditText addressLine2;
    EditText postalCode;
    CountryPicker picker;
    EditText countryName;
    ImageView imageToUpload;
    Button regBtn;
    private static Uri imageURI;
    private ProgressBar progressBarHolder;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        imageToUpload = findViewById(R.id.imageToUpload);
        regBtn = findViewById(R.id.regBtn);
        inputEmail = findViewById(R.id.mailID);
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
        changeStatusBarColor(EditProfileActivity.this);
        countryName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!picker.isAdded()){
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
                return false;
            }
        });
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    inputEmail.setText(user.getEmailID());
                    inputEmail.setEnabled(false);
                    inputEmail.setInputType(InputType.TYPE_NULL);
                    inputFirstName.setText(user.getFirstName());
                    inputLastName.setText(user.getLastName());
                    phoneNumber.setText(user.getPhoneNumber());
                    String[] address = user.getAddress().split("\n");
                    addressLine1.setText(address[0]!=null?address[0]:"");
                    addressLine2.setText(address[1]!=null?address[1]:"");
                    countryName.setText(user.getCountry());
                    postalCode.setText(address.length>2?address[2].replace("Postal code:",""):"");
                    if(user.getProfileImgURL()!=null&& !user.getProfileImgURL().isEmpty()){
                        imageToUpload.setImageURI(Uri.parse(user.getProfileImgURL()));
                    }
                    ref.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        findViewById(R.id.regBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarHolder.setVisibility(View.VISIBLE);
                hideKeyboard(EditProfileActivity.this);
                final String email = inputEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBarHolder.setAnimation(inAnimation);
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ref.removeEventListener(this);
                        final User user = dataSnapshot.getValue(User.class);
                        if(user.getProfileImgURL()!=null&& !user.getProfileImgURL().isEmpty() && user.getProfileImgURL().contains("http")){
                            user.setFirstName(inputFirstName.getText().toString());
                            user.setLastName(inputLastName.getText().toString());
                            user.setPhoneNumber(phoneNumber.getText().toString());
                            String address=addressLine1.getText()+"\n"+addressLine2.getText()+"\n"+"Postal code:"+postalCode.getText();
                            user.setAddress(address);
                            user.setCountry(countryName.getText().toString());
                            user.setProfileImgURL(user.getProfileImgURL());
                            ref.setValue(user);
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            finish();
                        }else{
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
                                            final String[] imgDownloadURL = {""};

                                            img.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    imgDownloadURL[0] = task.getResult().toString();
                                                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    user.setFirstName(inputFirstName.getText().toString());
                                                    user.setLastName(inputLastName.getText().toString());
                                                    user.setPhoneNumber(phoneNumber.getText().toString());
                                                    String address=addressLine1.getText()+"\n"+addressLine2.getText()+"\n"+"Postal code:"+postalCode;
                                                    user.setAddress(address);
                                                    user.setProfileImgURL(imgDownloadURL[0]);
                                                    user.setCountry(countryName.getText().toString());
                                                    db.setValue(user);
                                                    Toast.makeText(EditProfileActivity.this, "Edited succesfully!", Toast.LENGTH_SHORT).show();
                                                    progressBarHolder.setAnimation(outAnimation);
                                                    progressBarHolder.setVisibility(View.GONE);
                                                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(EditProfileActivity.this, "Profile image upload failed",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                Toast.makeText(EditProfileActivity.this, "Image not found",Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                countryName = findViewById(R.id.countryName);
                countryName.setText(name);
                picker.dismiss();

            }
        });

    }

    public void init() {
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
        View view = activity.getCurrentFocus();
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
                    hideSoftKeyboard(EditProfileActivity.this);
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
