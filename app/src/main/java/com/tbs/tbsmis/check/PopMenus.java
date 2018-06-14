package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.FtsManagerActivity;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.FileViewActivity;
import com.tbs.tbsmis.file.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"ResourceAsColor", "ShowToast"})
public class PopMenus
{
    private final JSONArray jsonArray;
    private final Context context;
    private final PopupWindow popupWindow;
    private final LinearLayout listView;
    private final int width;
    private final int height;
    private final View containerView;
    private final WebView webview;
    private String type;

    public PopMenus(WebView webview, Context context, JSONArray _jsonArray,
                    int _width, int _height) {
        this.context = context;
        jsonArray = _jsonArray;
        width = _width;
        height = _height;
        this.webview = webview;
        this.containerView = LayoutInflater.from(context).inflate(R.layout.popmenus,
                null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        this.containerView.setLayoutParams(lp);
        // ���� listview
        this.listView = (LinearLayout) this.containerView
                .findViewById(R.id.layout_subcustommenu);
        try {
            this.setSubMenu();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.listView.setBackgroundColor(context.getResources().getColor(R.color.black));
        this.listView.setFocusableInTouchMode(true);
        this.listView.setFocusable(true);

        this.popupWindow = new PopupWindow(this.containerView,
                this.width == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : this.width,
                this.height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : this.height);
    }

    // ����ʽ ���� pop�˵� parent ���½�
    public void showAsDropDown(View parent) {
        this.popupWindow.setBackgroundDrawable(new ColorDrawable());
        this.popupWindow.showAsDropDown(parent);
        // ����������������ʧ
        this.popupWindow.setOutsideTouchable(true);
        // ʹ��ۼ�
        this.popupWindow.setFocusable(true);
        // ˢ��״̬
        this.popupWindow.update();

        this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            // ��dismiss�лָ�͸����
            @Override
            public void onDismiss() {
            }
        });
    }

    public void showAtLocation(View parent) {
        this.popupWindow.setBackgroundDrawable(new ColorDrawable());
        this.containerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        int x = location[0] - 5;
        int y = parent.getHeight() - parent.getHeight() * 1 / 5;
        // Utils.toast(context, y +""); //location[1] - popupHeight -
        // parent.getHeight()
        this.popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, x, y);

        // ����������������ʧ
        this.popupWindow.setOutsideTouchable(true);
        // ʹ��ۼ�
        this.popupWindow.setFocusable(true);
        // ˢ��״̬
        this.popupWindow.update();

        this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            // ��dismiss�лָ�͸����
            @Override
            public void onDismiss() {
            }
        });
    }

    // ���ز˵�
    public void dismiss() {
        this.popupWindow.dismiss();
    }

    void setSubMenu() throws JSONException {
        this.listView.removeAllViews();
        for (int i = 0; i < this.jsonArray.length(); i++) {
            final JSONObject ob = this.jsonArray.getJSONObject(i);
            LinearLayout layoutItem = (LinearLayout) ((LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.pomenu_menuitem, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            this.containerView.setLayoutParams(lp);
            layoutItem.setFocusable(true);
            TextView tv_funbtntitle = (TextView) layoutItem
                    .findViewById(R.id.pop_item_textView);
            View pop_item_line = layoutItem.findViewById(R.id.pop_item_line);
            if (i + 1 == this.jsonArray.length()) {
                pop_item_line.setVisibility(View.GONE);
            }
            String name = ob.getString("name");
            this.type = ob.getString("type");
            tv_funbtntitle.setText(name);
            layoutItem.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (PopMenus.this.type.equalsIgnoreCase("view")) {
                        try {
                            PopMenus.this.webview.loadUrl(ob.getString("url"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (PopMenus.this.type.equalsIgnoreCase("button")) {
                        try {
                            String key = ob.getString("key");
                            ActionBar bar = ((FileExplorerTabActivity) PopMenus.this.context).getActionBar();
                            if (bar.getSelectedNavigationIndex() != Util.SDCARD_TAB_INDEX) {
                                if (key.equalsIgnoreCase("rename")) {
                                    ((FtsManagerActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.CATEGORY_TAB_INDEX)).actionRename();
                                } else if (key.equalsIgnoreCase("cancle")) {
                                    ((FtsManagerActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.CATEGORY_TAB_INDEX)).actionCancle();
                                }
                            } else {
                                if (key.equalsIgnoreCase("rename")) {
                                    ((FileViewActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.SDCARD_TAB_INDEX)).Rename();
                                } else if (key.equalsIgnoreCase("cancle")) {
                                    ((FileViewActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.SDCARD_TAB_INDEX)).Cancle();
                                } else if (key.equalsIgnoreCase("info")) {
                                    ((FileViewActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.SDCARD_TAB_INDEX)).Info();
                                } else if (key.equalsIgnoreCase("selectall")) {
                                    ((FileViewActivity) ((FileExplorerTabActivity) PopMenus.this.context)
                                            .getFragment(Util.SDCARD_TAB_INDEX)).SelectAll();
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(PopMenus.this.context, "类型不支持", Toast.LENGTH_SHORT).show();
                        PopMenus.this.dismiss();
                    }
                    PopMenus.this.dismiss();
                }
            });
            this.listView.addView(layoutItem);
        }

        this.listView.setVisibility(View.VISIBLE);
    }

}
