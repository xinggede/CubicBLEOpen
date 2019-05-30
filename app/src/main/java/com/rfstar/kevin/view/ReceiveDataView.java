package com.rfstar.kevin.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rfstar.kevin.R;

public class ReceiveDataView extends LinearLayout {
    private TextView lengthTxt = null, countTimesTxt = null, messageTxt;
    private ScrollView messageBG = null;

    @SuppressWarnings("static-access")
    public ReceiveDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.receive_data_view,
                this, true);
        lengthTxt = (TextView) this.findViewById(R.id.lengthTxt);
        countTimesTxt = (TextView) this.findViewById(R.id.countTimesTxt);
        messageTxt = (TextView) this.findViewById(R.id.messageTxt);
        messageBG = (ScrollView) this.findViewById(R.id.messageBg);

        // <body>
        // <tt>打字机风格显示</tt>
        //
        // <samp>等宽文字设置内容</samp>
        // <kdb>键盘输入</kdb>
        // </body>
    }

    public String getLengthTxt() {
        return lengthTxt.toString();
    }

    public void setLengthTxt(String lengthTxt) {
        this.lengthTxt.setText(lengthTxt);
    }

    public String getCountTimesTxt() {
        return countTimesTxt.getText().toString();
    }

    /**
     * 次数递增
     */
    public void setCountTimesTxt() {
        int counts = Integer.valueOf(countTimesTxt.getText().toString());
        counts++;
        this.countTimesTxt.setText(counts + "");

    }

    public String getEdit() {
        return messageTxt.getText().toString();
    }

    public void setEdit(String edit) {
        // this.edit.setText(Html.fromHtml("<font size=\"3\" color=\"red\">"
        // + edit +
        // "</font> <br> <samp>ABCDabcd1234567890</samp>"+"<br><code>ABCDabcd1234567890</code>"));
        this.messageTxt.setText(edit);
        this.lengthTxt.setText("" + edit.length());
    }

    private StringBuffer allMessageStr = new StringBuffer(); // 接收的所有数据

    /**
     * 追加的数据
     *
     * @param subString
     */
    public void appendString(String subString) {

        String tempStr = null;
        allMessageStr.append(subString);

        int size = this.allMessageStr.length();
        if (size > 500) {
            tempStr = allMessageStr.substring(size - 500, size);
        } else {
            tempStr = allMessageStr.toString();
        }
        this.messageTxt.setText(tempStr);

        this.lengthTxt.setText(size + "");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                messageBG.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @SuppressWarnings("static-access")
    public void changeEditBackground(boolean boo) {
        if (boo) {
            this.messageBG
                    .setBackgroundResource(R.drawable.corner_pressed_round_body);
        } else {
            this.messageBG
                    .setBackgroundResource(R.drawable.corner_round_body);
            this.messageTxt.setText(allMessageStr);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    messageBG.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    /**
     * 重置
     */
    public void reset() {
        this.lengthTxt.setText(0 + "");
        this.allMessageStr.delete(0, this.allMessageStr.length());
        this.countTimesTxt.setText(0 + "");
        this.messageTxt.setText("");
    }
}
