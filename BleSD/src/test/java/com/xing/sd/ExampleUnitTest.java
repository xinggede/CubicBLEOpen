package com.xing.sd;

import com.xing.sd.util.Tool;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parserIp() {
        String data = "0,192168001099,192168001001,255255255000,8000";
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            String text = vs[1];
            System.out.println(Integer.parseInt(text.substring(0, 3)) + "." + Integer.parseInt(text.substring(3, 6)) + "." + Integer.parseInt(text.substring(6, 9)) + "." + Integer.parseInt(text.substring(9)));
            text = vs[2];
            System.out.println(Integer.parseInt(text.substring(0, 3)) + "." + Integer.parseInt(text.substring(3, 6)) + "." + Integer.parseInt(text.substring(6, 9)) + "." + Integer.parseInt(text.substring(9)));
            text = vs[3];
            System.out.println(Integer.parseInt(text.substring(0, 3)) + "." + Integer.parseInt(text.substring(3, 6)) + "." + Integer.parseInt(text.substring(6, 9)) + "." + Integer.parseInt(text.substring(9)));
        }
    }


}