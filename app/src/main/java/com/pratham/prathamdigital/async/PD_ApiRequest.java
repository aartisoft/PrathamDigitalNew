package com.pratham.prathamdigital.async;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by HP on 30-12-2016.
 */

public class PD_ApiRequest {
    Context mContext;
    ContentContract.contentPresenter contentPresenter;
//    OkHttpClient okHttpClient;

    public PD_ApiRequest(Context context, ContentContract.contentPresenter contentPresenter) {
        this.mContext = context;
        this.contentPresenter = contentPresenter;
//        okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
    }

    public void getContentFromRaspberry(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (contentPresenter != null)
                                contentPresenter.recievedContent(requestType, response, contentList);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (contentPresenter != null)
                                contentPresenter.recievedError(requestType, contentList);
                            Log.d("Error::", anError.getErrorDetail());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContentFromInternet(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (contentPresenter != null)
                                contentPresenter.recievedContent(requestType, response, contentList);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (contentPresenter != null)
                                contentPresenter.recievedError(requestType, contentList);
                            Log.d("Error:", anError.getErrorDetail());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushDataToRaspberry(final String requestType, String url, String data,
                                    String filter_name, String table_name) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                .addBodyParameter("filter_name", filter_name)
                .addBodyParameter("table_name", table_name)
                .addBodyParameter("facility", FastSave.getInstance().getString(PD_Constant.FACILITY_ID, ""))
                .addBodyParameter("data", data)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
//                        contentPresenter.notifySuccess(requestType, "success");
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.d("Error::", anError.getErrorDetail());
                        Log.d("Error::", anError.getMessage());
                        Log.d("Error::", anError.getResponse().toString());
                    }
                });
    }

    public void getacilityIdfromRaspberry(final String requestType, String url, JSONObject data) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(data)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        contentPresenter.recievedContent(requestType, response, null);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (contentPresenter != null)
//                            contentPresenter.notifyError(requestType/*, null*/);
                            Log.d("Error::", anError.getErrorDetail());
                        Log.d("Error::", anError.getMessage());
                        Log.d("Error::", anError.getResponse().toString());
                    }
                });
    }

    private String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
    }


    public static void downloadImage(String url, String filename) {
        File dir = new File(PrathamApplication.pradigiPath + "/PrathamImages"); //Creating an internal dir;
        if (!dir.exists()) dir.mkdirs();
        AndroidNetworking.download(url, dir.getAbsolutePath(), filename)
                .setPriority(Priority.HIGH)
                .build()
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("image::", "DownloadComplete");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("image::", "Not Downloaded");
                    }
                });
    }
}
