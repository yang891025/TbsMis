package com.tbs.intstaller;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import com.tbs.tbsmis.R;

import java.util.List;

/**
 * Created by TBS on 2016/6/20.
 */
public class SchemeDownActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_result);
        // 尝试获取WebApp页面上过来的URL
        Uri uri = this.getIntent().getData();
        if (uri != null) {
            StringBuffer sb = new StringBuffer();
            // 完整的url信息
            sb.append("url: " + uri);
            // scheme部分
            sb.append("\nscheme: " + uri.getScheme());
            // host部分
            sb.append("\nhost: " + uri.getHost());
            // 访问路劲
            sb.append("\npath: ");
            List<String> pathSegments = uri.getPathSegments();
            for (int i = 0; pathSegments != null && i < pathSegments.size(); i++) {
                sb.append("/" + pathSegments.get(i));
            }
            // Query部分
            sb.append("\nquery: ?" + uri.getQuery());
            //System.out.println(sb);
            //tv.setText(sb.toString());
        }
    }
}
