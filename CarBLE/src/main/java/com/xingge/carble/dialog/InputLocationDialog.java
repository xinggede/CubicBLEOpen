package com.xingge.carble.dialog;


import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.xingge.carble.R;
import com.xingge.carble.util.Tool;


public class InputLocationDialog extends BaseDialog {
    private EditText etLatitude1, etLatitude2, etLatitude3, etLongitude1, etLongitude2, etLongitude3;
    private TextView tvLa1, tvLa2, tvLa3, tvLo1, tvLo2, tvLo3;

    private Button btConfirm, btCancel;

    public InputLocationDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        etLatitude1 = getDialog().findViewById(R.id.et_latitude1);
        etLatitude2 = getDialog().findViewById(R.id.et_latitude2);
        etLatitude3 = getDialog().findViewById(R.id.et_latitude3);
        etLongitude1 = getDialog().findViewById(R.id.et_longitude1);
        etLongitude2 = getDialog().findViewById(R.id.et_longitude2);
        etLongitude3 = getDialog().findViewById(R.id.et_longitude3);

        tvLa1 = getDialog().findViewById(R.id.tv_la1);
        tvLa2 = getDialog().findViewById(R.id.tv_la2);
        tvLa3 = getDialog().findViewById(R.id.tv_la3);
        tvLo1 = getDialog().findViewById(R.id.tv_lo1);
        tvLo2 = getDialog().findViewById(R.id.tv_lo2);
        tvLo3 = getDialog().findViewById(R.id.tv_lo3);

        btConfirm = getDialog().findViewById(R.id.bt_confirm);
        btCancel = getDialog().findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void showType(int type) {
        etLatitude1.setText("");
        etLatitude2.setText("");
        etLatitude3.setText("");
        etLongitude1.setText("");
        etLongitude2.setText("");
        etLongitude3.setText("");
        etLatitude1.requestFocus();
        requestLayout(type);
        super.show();
    }

    private void requestLayout(int type) {
        tvLa1.setText("°");
        tvLo1.setText("°");

        etLatitude1.setVisibility(View.VISIBLE);
        etLatitude2.setVisibility(View.VISIBLE);
        etLongitude1.setVisibility(View.VISIBLE);
        etLongitude2.setVisibility(View.VISIBLE);

        tvLa1.setVisibility(View.VISIBLE);
        tvLa2.setVisibility(View.VISIBLE);
        tvLo1.setVisibility(View.VISIBLE);
        tvLo2.setVisibility(View.VISIBLE);

        if (type == 0) {
            tvLa2.setGravity(Gravity.BOTTOM);
            tvLo2.setGravity(Gravity.BOTTOM);
            tvLa2.setText(".");
            tvLo2.setText(".");

            tvLa3.setText("'");
            tvLo3.setText("'");
        } else if (type == 1) {
            tvLa2.setGravity(Gravity.TOP);
            tvLo2.setGravity(Gravity.TOP);

            tvLa2.setText("'");
            tvLo2.setText("'");

            tvLa3.setText("''");
            tvLo3.setText("''");
        } else {
            etLatitude1.setVisibility(View.GONE);
            etLatitude2.setVisibility(View.GONE);
            etLongitude1.setVisibility(View.GONE);
            etLongitude2.setVisibility(View.GONE);

            tvLa1.setVisibility(View.GONE);
            tvLa2.setVisibility(View.GONE);
            tvLo1.setVisibility(View.GONE);
            tvLo2.setVisibility(View.GONE);

            tvLa3.setText("°");
            tvLo3.setText("°");

            etLatitude3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            etLongitude3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }
    }

    public String getTitle(int type) {
        StringBuilder sb = new StringBuilder();
        if (type == 2) {
            sb.append("经度:").append(etLongitude3.getText()).append(tvLo3.getText()).append("\n");
            sb.append("纬度:").append(etLatitude3.getText()).append(tvLa3.getText()).append("\n");
        } else {
            sb.append("经度:").append(etLongitude1.getText()).append(tvLo1.getText()).
                    append(etLongitude2.getText()).append(tvLo2.getText()).
                    append(etLongitude3.getText()).append(tvLo3.getText()).append("\n");
            sb.append("纬度:").append(etLatitude1.getText()).append(tvLa1.getText()).
                    append(etLatitude2.getText()).append(tvLa2.getText()).
                    append(etLatitude3.getText()).append(tvLa3.getText()).append("\n");
        }
        return sb.toString();
    }

    public LatLng getLatlng(int type) {
        double lat, lng;
        double d, m, s;
        if (type == 0) {
            d = Tool.stringToDouble(etLatitude1.getText().toString().trim());
            if (d > 89) {
                d = 89;
            }

            m = Tool.stringToDouble(etLatitude2.getText().toString().trim());
            if (m > 59) {
                m = 59;
            }

            s = Tool.stringToDouble(etLatitude3.getText().toString().trim());
            lat = d + m / 60 + s / 600000;

            d = Tool.stringToDouble(etLongitude1.getText().toString().trim());
            if (d > 179) {
                d = 179;
            }

            m = Tool.stringToDouble(etLongitude2.getText().toString().trim());
            if (m > 59) {
                m = 59;
            }

            s = Tool.stringToDouble(etLongitude3.getText().toString().trim());
            lng = d + m / 60 + s / 600000;
        } else if (type == 1) {
            d = Tool.stringToDouble(etLatitude1.getText().toString().trim());
            if (d > 89) {
                d = 89;
            }
            m = Tool.stringToDouble(etLatitude2.getText().toString().trim());
            if (m > 59) {
                m = 59;
            }
            s = Tool.stringToDouble(etLatitude3.getText().toString().trim());
            lat = d + m / 60 + s / 3600;

            d = Tool.stringToDouble(etLongitude1.getText().toString().trim());
            if (d > 179) {
                d = 179;
            }

            m = Tool.stringToDouble(etLongitude2.getText().toString().trim());
            if (m > 59) {
                m = 59;
            }
            s = Tool.stringToDouble(etLongitude3.getText().toString().trim());
            lng = d + m / 60 + s / 3600;
        } else {
            d = Tool.stringToDouble(etLatitude1.getText().toString().trim());

            if (d > 89) {
                d = 89;
            }
            lat = d;

            d = Tool.stringToDouble(etLongitude1.getText().toString().trim());
            if (d > 179) {
                d = 179;
            }
            lng = d;
        }
        return new LatLng(lat, lng);
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_input_location;
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
    }

}