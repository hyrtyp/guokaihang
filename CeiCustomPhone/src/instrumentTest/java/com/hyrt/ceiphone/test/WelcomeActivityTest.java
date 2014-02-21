package com.hyrt.ceiphone.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

import com.hyrt.ceiphone.WelcomeActivity;
import com.jayway.android.robotium.solo.Solo;
import android.test.UiThreadTest;
import com.squareup.spoon.Spoon;

/**
 * Created by yepeng on 13-12-13.
 */
public class WelcomeActivityTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {


    private Solo solo;

    @TargetApi(Build.VERSION_CODES.FROYO)
    public WelcomeActivityTest(){
        super(WelcomeActivity.class);
    }



    @Override
    public void setUp() throws Exception {
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    @UiThreadTest
    public void testui(){
        solo.assertMemoryNotLow();
        Spoon.screenshot(this.getActivity(), "WelcomeActivity");
        assertNotNull(null);
    }


}
