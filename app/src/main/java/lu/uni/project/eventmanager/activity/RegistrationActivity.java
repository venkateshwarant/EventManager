package lu.uni.project.eventmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hbb20.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;


import lu.uni.project.eventmanager.R;
//import com.ybs.countrypicker.CountryPickerListener;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

private static final int RESULT_LOAD_IMAGE = 1;
    RadioGroup radioGroup;
    RadioButton radioButton;
    CountryCodePicker ccp;
    EditText phoneNumber;
    CountryPicker picker;
   EditText countryName;
    ImageView imageToUpload;
    Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        regBtn = (Button) findViewById(R.id.regBtn) ;

        imageToUpload.setOnClickListener(this);

        radioGroup=findViewById(R.id.radio_group);
        init();

        findViewById(R.id.regBtn).setOnClickListener(this);


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
        }else if(v.getId()==R.id.regBtn){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null)
        {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

}
