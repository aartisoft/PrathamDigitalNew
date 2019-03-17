package com.pratham.prathamdigital.ui.fragment_age_group;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.ui.QRLogin.QRLogin_;
import com.pratham.prathamdigital.ui.connect_dialog.ConnectDialog;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment_;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EFragment(R.layout.fragment_age_group)
public class FragmentSelectAgeGroup extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    @Click(R.id.scan_qr)
    public void setScanQR() {
        KotlinPermissions.with(getActivity())
                .permissions(Manifest.permission.CAMERA)
                .onAccepted(new ResponsePermissionCallback() {
                    @Override
                    public void onResult(@NotNull List<String> permissionResult) {
                        Intent intent = new Intent(getActivity(), QRLogin_.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    }
                })
                .ask();
    }

    @Click(R.id.iv_age_3_to_6)
    public void open3to6Groups(View view) {
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, true);
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.addFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click(R.id.iv_age_8_to_14)
    public void open8to14Groups(View view) {
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, false);
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.addFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click(R.id.admin_panel)
    public void openAdminPanel() {
        if (!PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.enableWifi();
        if (!PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() && !PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            ConnectDialog connectDialog = new ConnectDialog.Builder(getActivity()).build();
            connectDialog.isDismissOnClickBack();
            connectDialog.isDismissOnTouchBackground();
            connectDialog.setOnDismissListener(new BlurPopupWindow.OnDismissListener() {
                @Override
                public void onDismiss(BlurPopupWindow popupWindow) {
                    onActivityResult(3, Activity.RESULT_OK, null);
                }
            });
            connectDialog.show();
        } else {
            onActivityResult(3, Activity.RESULT_OK, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                PD_Utility.showFragment(getActivity(), new AdminPanelFragment_(), R.id.frame_attendance,
                        null, AdminPanelFragment.class.getSimpleName());
            }
        }
    }
}