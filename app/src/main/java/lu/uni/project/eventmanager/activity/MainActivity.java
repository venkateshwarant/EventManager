package lu.uni.project.eventmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import lu.uni.project.eventmanager.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        if(mauth.getCurrentUser()==null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            startActivity(new Intent(this, BottomNavigationActivity.class));
        }
        finish();
    }
}
