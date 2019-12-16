package lu.uni.project.eventmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hbb20.CountryCodePicker;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;


import lu.uni.project.eventmanager.R;
//import com.ybs.countrypicker.CountryPickerListener;

public class RegistrationActivity extends AppCompatActivity {


    RadioGroup radioGroup;
    RadioButton radioButton;
    CountryCodePicker ccp;
    EditText phoneNumber;
    CountryPicker picker;
   EditText countryName;
    //ImageView countryIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        radioGroup=findViewById(R.id.radio_group);
        init();

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

        /*picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {*/


       /* fn = (EditText) findViewById(R.id.FirstName);
        ln = (EditText) findViewById(R.id.LastName);
        final Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
        {
           btn.setText("Button Clicked");
        }
        });*/




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
}
