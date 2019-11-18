package com.lu.portable.detect.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.kernal.demo.plateid.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogListActivity extends Activity {
    private List <Map <String, String>> loadFileListFromStorage() {
        ArrayList localArrayList = new ArrayList();
        Object localObject1 = Environment.getExternalStorageDirectory();
        File[] arrayOfFile = new File(((File) localObject1).getPath() + "/Adata/").listFiles();
        int i = 0;
        while (i < arrayOfFile.length) {
            File localFile = arrayOfFile[(arrayOfFile.length - i - 1)];
            if (localFile.isFile()) {
                HashMap localHashMap = new HashMap();
                localObject1 = localFile.getName();
                Object localObject2 = Pattern.compile("data _(.*?).txt").matcher((CharSequence) localObject1);
                localObject1 = "--";
                if (((Matcher) localObject2).matches())
                    localObject1 = ((Matcher) localObject2).group(1);
                long l1 = localFile.lastModified();
                long l2 = localFile.length();
                localObject2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                localObject1 = String.format("开始时间：%s", new Object[]{localObject1});
                localObject2 = String.format("结束时间：%s", new Object[]{((SimpleDateFormat) localObject2).format(Long.valueOf(l1))});
                String str = String.format("%sB", new Object[]{Long.valueOf(l2)});
                localHashMap.put("path", localFile.getPath());
                localHashMap.put("startTime", localObject1);
                localHashMap.put("endTime", localObject2);
                localHashMap.put("size", str);
                localArrayList.add(localHashMap);
            }
            i += 1;
        }
        return (List <Map <String, String>>) (List <Map <String, String>>) localArrayList;
    }

    private void openFile(File paramFile) {
        Intent intent = new Intent();
        // intent.addFlags(268435456);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(paramFile), "text/plain");
        startActivity(intent);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_log_list);
        ListView logListView = findViewById(R.id.listView);
        final List fileInfoList = loadFileListFromStorage();
        logListView.setAdapter(new SimpleAdapter(this, fileInfoList, R.layout.log_list_item, new String[]{"startTime", "endTime", "size"}, new int[]{R.id.tv_log_startTime, R.id.tv_log_endTime, R.id.tv_log_size}));
        logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView <?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                File file = new File((String) ((Map) fileInfoList.get(paramInt)).get("path"));
                openFile(file);
            }
        });
    }
}
