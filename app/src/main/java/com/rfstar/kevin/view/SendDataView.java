package com.rfstar.kevin.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rfstar.kevin.R;

/**
 * 发送数据的Viwew
 *
 * @author kevin
 */
public class SendDataView extends LinearLayout implements TextWatcher {
    private TextView lengthTxt = null, countTimesTxt = null;
    private EditText edit = null;
    private Context context = null;

    @SuppressWarnings("static-access")
    public SendDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.send_data_view, this,
                true);
        lengthTxt = (TextView) this.findViewById(R.id.lengthTxt);
        countTimesTxt = (TextView) this.findViewById(R.id.countTimesTxt);
        edit = (EditText) this.findViewById(R.id.editText1);
        edit.addTextChangedListener(this);
    }

    public int getLengthTxt() {
        return lengthTxt.toString().length();
    }

    // public void setLengthTxt(int length) {
    // this.lengthTxt.setText((lengthTxt.length() + length) + "");
    // }

    /**
     * 获取发送的次数
     *
     * @return
     */
    public int getCountTimes() {
        return Integer.valueOf(countTimesTxt.getText().toString());
    }

    /**
     * 次数递增1
     */
    public void setCountTimes() {
        if (!edit.getText().toString().equals("")) {
            int times = this.getCountTimes();
            times++;
            this.countTimesTxt.setText(times + "");
        } else {
            ToastUntil.makeText(this.context, "发送的数据不能为空", 1);
        }
    }

    public String getEdit() {
        return edit.getText().toString();
    }

    public void setEdit(String edit) {
        this.edit.setText(edit);
        this.lengthTxt.setText("" + edit.length());
    }

    /**
     * 清除
     */
    public void clear() {
        this.countTimesTxt.setText("0");
        this.edit.setText("");
    }

    /**
     * 重置
     */
    public void reset() {
        this.countTimesTxt.setText("0");
        this.edit.setText("VGH:CONNECT\r\n");
    }

    private CharSequence temp;
    private int editStart;
    private int editEnd;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        this.lengthTxt.setText("" + s.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        editStart = this.edit.getSelectionStart();// 光标开始的位置
        editEnd = this.edit.getSelectionEnd();
//        if (temp.length() > 20) {
//            ToastUntil.makeText(this.context, "输入的字符不能超过20个", 1);
//            s.delete(editStart - 1, editEnd);
//            int tempSelection = editStart; // 重新定位光标的位置
//            this.edit.setText(s);
//            this.edit.setSelection(tempSelection);
//        }
    }

}
