package com.azirtime.remote.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class ViewUtils {

    /**
     * 遍历布局，并禁用所有子控件
     *
     * @param viewGroup 布局对象
     */
    public static void setEnableSubControls(ViewGroup viewGroup, boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    spinner.setClickable(enable);
                    spinner.setEnabled(enable);

                } else if (v instanceof ListView) {
                    ((ListView) v).setClickable(false);
                    ((ListView) v).setEnabled(false);

                } else {
                    setEnableSubControls((ViewGroup) v, enable);
                }
            } else {
                v.setClickable(enable);
                v.setEnabled(enable);
            }
        }
    }
}
