package com.jica.newpts;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.jica.newpts", appContext.getPackageName());


        String originalString = "Hello, this is a sample text. Remove everything before the first period.";
        char targetChar = '.';

        // 특정 문자(targetChar)가 처음으로 나오는 인덱스 찾기
        int indexOfTargetChar = originalString.indexOf(targetChar);

        while (true) {
            if (indexOfTargetChar != -1) { // 특정 문자가 발견되었다면
                break;
            }
            String resultString = originalString.substring(indexOfTargetChar + 1);
        }
        String resultString;

    }
}