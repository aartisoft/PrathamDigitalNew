package com.pratham.prathamdigital;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.AttendanceDao;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.CRLdao;
import com.pratham.prathamdigital.dbclasses.GroupDao;
import com.pratham.prathamdigital.dbclasses.LogDao;
import com.pratham.prathamdigital.dbclasses.ModalContentDao;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.dbclasses.ScoreDao;
import com.pratham.prathamdigital.dbclasses.SessionDao;
import com.pratham.prathamdigital.dbclasses.StatusDao;
import com.pratham.prathamdigital.dbclasses.StudentDao;
import com.pratham.prathamdigital.dbclasses.VillageDao;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_PushData;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import net.alhazmy13.catcho.library.Catcho;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int UPDATE_CONNECTION = 1;
    private static final int HIDE_SYSTEM_UI = 2;
    private static final int REQUEST_WRITE_PERMISSION = 6;
    private static final int GET_LOCATION_PERMISSION = 7;
    private static final int GET_READ_PHONE_STATE = 8;
    public static String sessionId = UUID.randomUUID().toString();
    public static AttendanceDao attendanceDao;
    public static CRLdao crLdao;
    public static GroupDao groupDao;
    public static ModalContentDao modalContentDao;
    public static ScoreDao scoreDao;
    public static SessionDao sessionDao;
    public static StatusDao statusDao;
    public static StudentDao studentDao;
    public static VillageDao villageDao;
    public static LogDao logDao;
    public static String language = "";
    public static TTSService ttsService;
    Merlin merlin;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CONNECTION:
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    EventMessage message = new EventMessage();
                    message.setMessage(PD_Constant.CONNECTION_STATUS);
                    if (wifi != null && wifi.isConnected()) {
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_dialog_connect_wifi_item));
                        message.setConnection_name(PrathamApplication.wiseF.getCurrentNetwork().getSSID());
                    } else if (mobile != null && mobile.isConnected()) {
                        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String carrierName = manager.getNetworkOperatorName();
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_4g_network));
                        message.setConnection_name(carrierName);
                    } else {
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_no_wifi));
                        message.setConnection_name(PD_Constant.NO_CONNECTION);
                    }
                    EventBus.getDefault().post(message);
                    break;
                case HIDE_SYSTEM_UI:
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    );
                    break;
                case REQUEST_WRITE_PERMISSION:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .onAccepted(new ResponsePermissionCallback() {
                                @Override
                                public void onResult(@NotNull List<String> permissionResult) {
                                    BackupDatabase.backup(BaseActivity.this);
                                }
                            })
                            .ask();
                    break;
                case GET_LOCATION_PERMISSION:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                            .onAccepted(new ResponsePermissionCallback() {
                                @Override
                                public void onResult(@NotNull List<String> permissionResult) {
                                    new LocationService(BaseActivity.this).checkLocation();
                                    mHandler.sendEmptyMessage(GET_READ_PHONE_STATE);
                                }
                            })
                            .ask();
                    break;
                case GET_READ_PHONE_STATE:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.READ_PHONE_STATE)
                            .onAccepted(new ResponsePermissionCallback() {
                                @Override
                                public void onResult(@NotNull List<String> permissionResult) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        Modal_Status statusObj = new Modal_Status();
                                        statusObj.setStatusKey("SerialID");
                                        statusObj.setValue(Build.getSerial());
                                        BaseActivity.statusDao.insert(statusObj);
                                    }
                                }
                            })
                            .ask();
                    break;
            }
        }
    };
    Connectable connectable = new Connectable() {
        @Override
        public void onConnect() {
            mHandler.sendEmptyMessage(UPDATE_CONNECTION);
        }
    };
    Disconnectable disconnectable = new Disconnectable() {
        @Override
        public void onDisconnect() {
            mHandler.sendEmptyMessage(UPDATE_CONNECTION);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mHandler.sendEmptyMessage(HIDE_SYSTEM_UI);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onCreate(savedInstanceState);
        //Utility initialized for shuffeling the color codes
        PD_Utility pd_utility = new PD_Utility(this);
        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class)
                .build();
        initializeDatabaseDaos();
        initializeConnectionService();
        initializeTTS();
        language = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
        if (language.isEmpty())
            FastSave.getInstance().saveString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
    }

    private void initializeTTS() {
        ttsService = new TTSService(getApplication());
        ttsService.setActivity(BaseActivity.this);
        ttsService.setSpeechRate(0.7f);
        ttsService.setLanguage(new Locale("en", "IN"));
    }

    private void initializeConnectionService() {
        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks().build(BaseActivity.this);
        merlin.registerConnectable(connectable);
        merlin.registerDisconnectable(disconnectable);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initializeDatabaseDaos() {
        PrathamDatabase db = PrathamDatabase.getDatabaseInstance(BaseActivity.this);
        attendanceDao = db.getAttendanceDao();
        crLdao = db.getCrLdao();
        groupDao = db.getGroupDao();
        modalContentDao = db.getModalContentDao();
        scoreDao = db.getScoreDao();
        sessionDao = db.getSessionDao();
        statusDao = db.getStatusDao();
        studentDao = db.getStudentDao();
        villageDao = db.getVillageDao();
        logDao = db.getLogDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(GET_LOCATION_PERMISSION);
        merlin.bind();
        BackupDatabase.backup(this);
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        BackupDatabase.backup(this);
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHandler.sendEmptyMessage(HIDE_SYSTEM_UI);
    }

    @Subscribe
    public void requestStoragePermission(final String permission) {
        if (permission.equalsIgnoreCase(PD_Constant.WRITE_PERMISSION)) {
            mHandler.sendEmptyMessage(REQUEST_WRITE_PERMISSION);
        }
    }

    @Subscribe
    public void updateFlagsWhenPushed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                Gson gson = new Gson();
                Modal_PushData pushedData = gson.fromJson(message.getPushData(), Modal_PushData.class);
                for (Modal_PushData.Modal_PushSessionData pushed :
                        pushedData.getPushSession()) {
                    BaseActivity.sessionDao.updateFlag(pushed.getSessionId());
                    for (Modal_Score score : pushed.getScores())
                        BaseActivity.scoreDao.updateFlag(pushed.getSessionId());
                    for (Attendance att : pushed.getAttendances())
                        BaseActivity.attendanceDao.updateSentFlag(pushed.getSessionId());
                }
                for (Modal_Student student : pushedData.getStudents())
                    BaseActivity.studentDao.updateSentStudentFlags(student.getStudentId());
            }
        }
    }
}