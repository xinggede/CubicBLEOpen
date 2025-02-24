package com.xingge.carble.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import com.xingge.carble.R;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.ui.mode.MainContract;
import com.xingge.carble.ui.mode.MainPresenter;
import com.xingge.carble.util.Tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;

/**
 * @ClassName CrashActivity
 * @Description TODO
 * @Author 星哥的
 * @Date 2024/9/29 15:17
 * @Version 1.0
 */
public class CrashActivity extends IBaseActivity<MainPresenter> implements MainContract.View {

    /** 报错代码行数正则表达式 */
    private static final Pattern CODE_REGEX = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)");
    /** 系统包前缀列表 */
    private static final String[] SYSTEM_PACKAGE_PREFIX_LIST = new String[]
            {"android", "com.android", "androidx", "com.google.android", "java", "javax", "dalvik", "kotlin"};

    TextView tvLog;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        tvLog = findViewById(R.id.tv_log);
        findViewById(R.id.bt_copy).setOnClickListener(this);

        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void initEventAndData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Throwable throwable = (Throwable)bundle.getSerializable("data");

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            Throwable cause = throwable.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String mStackTrace = stringWriter.toString();
            Matcher matcher = CODE_REGEX.matcher(mStackTrace);
            SpannableStringBuilder spannable = new SpannableStringBuilder(mStackTrace);
            if (spannable.length() > 0) {
                while (matcher.find()) {
                    // 不包含左括号（
                    int start = matcher.start() + "(".length();
                    // 不包含右括号 ）
                    int end = matcher.end() - ")".length();

                    // 代码信息颜色
                    int codeColor = 0xFF999999;
                    int lineIndex = mStackTrace.lastIndexOf("at ", start);
                    if (lineIndex != -1) {
                        String lineData = spannable.subSequence(lineIndex, start).toString();
                        if (TextUtils.isEmpty(lineData)) {
                            continue;
                        }
                        // 是否高亮代码行数
                        boolean highlight = true;
                        for (String packagePrefix : SYSTEM_PACKAGE_PREFIX_LIST) {
                            if (lineData.startsWith("at " + packagePrefix)) {
                                highlight = false;
                                break;
                            }
                        }
                        if (highlight) {
                            codeColor = 0xFF287BDE;
                        }
                    }

                    // 设置前景
                    spannable.setSpan(new ForegroundColorSpan(codeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // 设置下划线
                    spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvLog.setText(spannable);
            }
        }
    }

    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_crash_log;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_copy){
            copyText(tvLog.getText());
        }
    }

    private void copyText(CharSequence copiedText) {
        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("sw", copiedText));
        Tool.toastShow(this, "copy ok");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public void onConnectState(int state) {

    }

    @Override
    public void onReceiveData(String command, String data) {

    }
}
