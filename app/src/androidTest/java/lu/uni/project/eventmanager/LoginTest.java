package lu.uni.project.eventmanager;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.common.SignInButton;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import lu.uni.project.eventmanager.activity.LoginActivity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LoginTest{


    LoginActivity activity = null;

    /*public LoginTest() {
        super(LoginActivity.class);
    }*/

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule(LoginActivity.class);


    /*@Override
    public void setUp() throws Exception {
        super.setUp();

        this.setActivityInitialTouchMode(false);

        this.activity = this.getActivity();
        this.startButton = (Button) this.activity.findViewById(R.id.start_button);
    }*/
    @Test
    public void onCreateTest()
    {
        activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.login_button);
        assertNotNull(viewById);
        assertThat(viewById, Matchers.<View>instanceOf(Button.class));
        //Assert.assertTrue(login_button.performClick());

        View viewById1 = activity.findViewById(R.id.sign_in_button);
        assertNotNull(viewById1);
        assertThat(viewById1, Matchers.<View>instanceOf(SignInButton.class));

        View viewById2 = activity.findViewById(R.id.emailID);
        assertNotNull(viewById2);
        assertThat(viewById2, Matchers.<View>instanceOf(EditText.class));

        View viewById3 = activity.findViewById(R.id.password);
        assertNotNull(viewById3);
        assertThat(viewById3, Matchers.<View>instanceOf(EditText.class));




    }




}
