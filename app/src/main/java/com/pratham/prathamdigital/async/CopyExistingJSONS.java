package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CopyExistingJSONS extends AsyncTask<String, String, Boolean> {

    Context context;
    Interface_copying interface_copying;

    public CopyExistingJSONS(Context context, Interface_copying interface_copying) {
        this.context = context;
        this.interface_copying = interface_copying;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interface_copying.copyingExisting();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            File parent;
            if (PrathamApplication.contentExistOnSD)
                parent = new File(PrathamApplication.contentSDPath);
            else
                parent = new File(PrathamApplication.pradigiPath);
            if (parent.isDirectory() && parent.exists()) {
                for (File childs : parent.listFiles()) {
                    if (childs.getName().endsWith(".json")) {
                        try {
                            FileInputStream is = new FileInputStream(childs);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            is.read(buffer);
                            is.close();
                            String mResponse = new String(buffer);
                            Gson gson = new Gson();
                            Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                            }.getType();
                            List<Modal_ContentDetail> tempContents = gson.fromJson(mResponse, listType);
                            for (Modal_ContentDetail detail : tempContents) {
                                detail.setDownloaded(true);
                                if (PrathamApplication.contentExistOnSD) detail.setOnSDCard(true);
                                else detail.setOnSDCard(false);
                            }
                            BaseActivity.modalContentDao.addContentList(tempContents);
                            childs.delete();//todo this might give crash. Please check
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean copied) {
        super.onPostExecute(copied);
//        if (copied && interface_copying != null)
//            interface_copying.successCopyingExisting(folder_file.getAbsolutePath());
//        else
//            interface_copying.failedCopyingExisting();
    }
}
