package tae.co.uk.ltd;

import android.test.InstrumentationTestCase;
import android.widget.TextView;

import tae.co.uk.ltd.mvp.view.activity.MainActivity;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends InstrumentationTestCase {

    private MainActivity mMainActivity;
    private TextView mTextView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testWidget() {
        String expected = "Hello world!";
        String actual = "Hello world!";
        assertEquals(expected, actual);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}