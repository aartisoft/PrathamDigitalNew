package com.pratham.prathamdigital.ui.fragment_admin_panel;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminPanelContract {
    interface AdminPanelView {
        void openPullDataFragment();

        void onLoginFail();

        void onLoginSuccess();
    }

    interface AdminPanelPresenter {
        void checkLogin(String userName, String password);

        void pushData();

        void setView(AdminPanelFragment adminPanelFragment);
    }
}
