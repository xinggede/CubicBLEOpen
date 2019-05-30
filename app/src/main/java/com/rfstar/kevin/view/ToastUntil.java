package com.rfstar.kevin.view;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rfstar.kevin.R;

/**
 * toast
 *
 * @author wuheng textView.setText(Html.fromHtml(
 * "<font size=\"3\" color=\"red\">�?天天�?好�??�?</font><font size=\"3\" color=\"green\">??�好???</font>"
 * ));
 */
public class ToastUntil {
    private static TextView text = null;
    private static Toast toast = null;

    public static View initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast, null);
        text = (TextView) view.findViewById(R.id.toast_message);
        return view;
    }

    public static void makeText(Context context, String text, int duration) {
        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(duration);
            toast.setView(initView(context));
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 150);
        }
        ToastUntil.text.setText(text);
        toast.show();
    }

    public static void makeText(Context context, int text, int duration) {

        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(duration);
            toast.setView(initView(context));
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        ToastUntil.text.setText(text);
        toast.show();
    }

    public static void makeTextTop(Context context, String text, int duration) {
        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(duration);
            toast.setView(initView(context));
            toast.setGravity(Gravity.TOP, 0, 0);
        }
        ToastUntil.text.setText(Html
                .fromHtml("<font size=\"3\" color=\"green\">" + "position"
                        + "</font><font size=\"3\" color=\"red\">" + text
                        + "</font>"));

        toast.show();
    }
}
