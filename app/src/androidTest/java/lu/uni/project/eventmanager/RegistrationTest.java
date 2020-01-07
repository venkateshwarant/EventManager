package lu.uni.project.eventmanager;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.common.SignInButton;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import lu.uni.project.eventmanager.activity.LoginActivity;
import lu.uni.project.eventmanager.activity.RegistrationActivity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RegistrationTest {

    RegistrationActivity activity = null;

    @Rule
    public ActivityTestRule<RegistrationActivity> activityRule = new ActivityTestRule(RegistrationActivity.class);


    /*@Override
    public void setUp() throws Exception {
        super.setUp();

        this.setActivityInitialTouchMode(false);

        this.activity = this.getActivity();
        this.startButton = (Button) this.activity.findViewById(R.id.start_button);
    }*/
    @Test
    public void activityTest()
    {
        activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.imageToUpload);
        assertNotNull(viewById);
        assertThat(viewById, Matchers.<View>instanceOf(ImageView.class));
        //Assert.assertTrue(login_button.performClick());

        View viewById1 = activity.findViewById(R.id.regBtn);
        assertNotNull(viewById1);
        assertThat(viewById1, Matchers.<View>instanceOf(Button.class));

        View viewById2 = activity.findViewById(R.id.mailID);
        assertNotNull(viewById2);
        assertThat(viewById2, Matchers.<View>instanceOf(EditText.class));

        View viewById3 = activity.findViewById(R.id.pwd);
        assertNotNull(viewById3);
        assertThat(viewById3, Matchers.<View>instanceOf(EditText.class));

        View viewById4 = activity.findViewById(R.id.confirm_pwd);
        assertNotNull(viewById4);
        assertThat(viewById4, Matchers.<View>instanceOf(EditText.class));

        View viewById5 = activity.findViewById(R.id.progressBarHolder);
        assertNotNull(viewById5);
        assertThat(viewById5, Matchers.<View>instanceOf(ProgressBar.class));

        View viewById6 = activity.findViewById(R.id.First_name_Text1);
        assertNotNull(viewById6);
        assertThat(viewById6, Matchers.<View>instanceOf(EditText.class));

        View viewById7 = activity.findViewById(R.id.Last_name_Text2);
        assertNotNull(viewById7);
        assertThat(viewById7, Matchers.<View>instanceOf(EditText.class));

        View viewById8 = activity.findViewById(R.id.Address_Line1);
        assertNotNull(viewById8);
        assertThat(viewById8, Matchers.<View>instanceOf(EditText.class));

        View viewById9 = activity.findViewById(R.id.Address_Line2);
        assertNotNull(viewById9);
        assertThat(viewById9, Matchers.<View>instanceOf(EditText.class));

        View viewById10 = activity.findViewById(R.id.Postal_Code);
        assertNotNull(viewById10);
        assertThat(viewById10, Matchers.<View>instanceOf(EditText.class));


    }



}
