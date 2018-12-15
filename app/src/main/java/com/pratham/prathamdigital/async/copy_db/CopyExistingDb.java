package com.pratham.prathamdigital.async.copy_db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.PD_Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CopyExistingDb extends AsyncTask<String, String, Boolean> {

    File db_file;
    File folder_file;
    Context context;
    Interface_copyingDb interface_copyingDb;

    public CopyExistingDb(Context context, Interface_copyingDb interface_copyingDb) {
        this.context = context;
        this.interface_copyingDb = interface_copyingDb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        interface_copyingDb.copyingExistingDb();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(context);
            if (sdPath.size() > 0) {
                folder_file = new File(sdPath.get(0), PD_Constant.PRADIGI_FOLDER + "/" +
                        FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                if (folder_file.exists()) {
                    db_file = new File(folder_file.getAbsolutePath(), PrathamDatabase.DB_NAME);
                    if (db_file.exists()) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                        if (db == null) return false;
                        Cursor content_cursor = null;
                        try {
                            content_cursor = db.rawQuery("SELECT * FROM TableContent", null);
                            //populate contents
                            List<Modal_ContentDetail> contents = new ArrayList<>();
                            if (content_cursor.moveToFirst()) {
                                while (!content_cursor.isAfterLast()) {
                                    Modal_ContentDetail detail = new Modal_ContentDetail();
                                    detail.setNodeid(content_cursor.getString(content_cursor.getColumnIndex("nodeid")));
                                    detail.setNodetype(content_cursor.getString(content_cursor.getColumnIndex("nodetype")));
                                    detail.setNodetitle(content_cursor.getString(content_cursor.getColumnIndex("nodetitle")));
                                    detail.setNodekeywords(content_cursor.getString(content_cursor.getColumnIndex("nodekeywords")));
                                    detail.setNodeeage(content_cursor.getString(content_cursor.getColumnIndex("nodeeage")));
                                    detail.setNodedesc(content_cursor.getString(content_cursor.getColumnIndex("nodedesc")));
                                    detail.setNodeimage(content_cursor.getString(content_cursor.getColumnIndex("nodeimage")));
                                    detail.setNodeserverimage(content_cursor.getString(content_cursor.getColumnIndex("nodeserverimage")));
                                    detail.setResourceid(content_cursor.getString(content_cursor.getColumnIndex("resourceid")));
                                    detail.setResourcetype(content_cursor.getString(content_cursor.getColumnIndex("resourcetype")));
                                    detail.setResourcepath(content_cursor.getString(content_cursor.getColumnIndex("resourcepath")));
                                    detail.setLevel(content_cursor.getInt(content_cursor.getColumnIndex("level")));
                                    detail.setContent_language(content_cursor.getString(content_cursor.getColumnIndex("content_language")));
                                    detail.setParentid(content_cursor.getString(content_cursor.getColumnIndex("parentid")));
                                    detail.setContentType(content_cursor.getString(content_cursor.getColumnIndex("contentType")));
                                    detail.setDownloaded(true);
                                    detail.setOnSDCard(true);
                                    contents.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            BaseActivity.modalContentDao.addContentList(contents);
                            content_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //populate villages
                        Cursor village_cursor = null;
                        try {
                            village_cursor = db.rawQuery("SELECT * FROM Village", null);
                            List<Modal_Village> villages = new ArrayList<>();
                            if (village_cursor.moveToFirst()) {
                                while (!village_cursor.isAfterLast()) {
                                    Modal_Village vill = new Modal_Village();
                                    vill.setVillageId(village_cursor.getInt(village_cursor.getColumnIndex("VillageId")));
                                    vill.setVillageCode(village_cursor.getString(village_cursor.getColumnIndex("VillageCode")));
                                    vill.setVillageName(village_cursor.getString(village_cursor.getColumnIndex("VillageName")));
                                    vill.setBlock(village_cursor.getString(village_cursor.getColumnIndex("Block")));
                                    vill.setDistrict(village_cursor.getString(village_cursor.getColumnIndex("District")));
                                    vill.setState(village_cursor.getString(village_cursor.getColumnIndex("State")));
                                    vill.setCRLId(village_cursor.getString(village_cursor.getColumnIndex("CRLId")));
                                    villages.add(vill);
                                    village_cursor.moveToNext();
                                }
                            }
                            BaseActivity.villageDao.insertAllVillages(villages);
                            village_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //populate crls
                        Cursor crl_cursor = null;
                        try {
                            crl_cursor = db.rawQuery("SELECT * FROM CRL", null);
                            List<Modal_Crl> crls = new ArrayList<>();
                            if (crl_cursor.moveToFirst()) {
                                while (!crl_cursor.isAfterLast()) {
                                    Modal_Crl crl = new Modal_Crl();
                                    crl.setCRLId(crl_cursor.getString(crl_cursor.getColumnIndex("CRLId")));
                                    crl.setRoleId(crl_cursor.getString(crl_cursor.getColumnIndex("RoleId")));
                                    crl.setRoleName(crl_cursor.getString(crl_cursor.getColumnIndex("RoleName")));
                                    crl.setProgramId(crl_cursor.getString(crl_cursor.getColumnIndex("ProgramId")));
                                    crl.setProgramName(crl_cursor.getString(crl_cursor.getColumnIndex("ProgramName")));
                                    crl.setState(crl_cursor.getString(crl_cursor.getColumnIndex("State")));
                                    crl.setFirstName(crl_cursor.getString(crl_cursor.getColumnIndex("FirstName")));
                                    crl.setLastName(crl_cursor.getString(crl_cursor.getColumnIndex("LastName")));
                                    crl.setMobile(crl_cursor.getString(crl_cursor.getColumnIndex("Mobile")));
                                    crl.setEmail(crl_cursor.getString(crl_cursor.getColumnIndex("Email")));
                                    crl.setBlock(crl_cursor.getString(crl_cursor.getColumnIndex("Block")));
                                    crl.setDistrict(crl_cursor.getString(crl_cursor.getColumnIndex("District")));
                                    crl.setUserName(crl_cursor.getString(crl_cursor.getColumnIndex("UserName")));
                                    crl.setPassword(crl_cursor.getString(crl_cursor.getColumnIndex("Password")));
                                    crls.add(crl);
                                    crl_cursor.moveToNext();
                                }
                            }
                            BaseActivity.crLdao.insertAllCRL(crls);
                            crl_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //populate groups
                        Cursor grp_cursor = null;
                        try {
                            grp_cursor = db.rawQuery("SELECT * FROM Groups", null);
                            List<Modal_Groups> groups = new ArrayList<>();
                            if (grp_cursor.moveToFirst()) {
                                while (!grp_cursor.isAfterLast()) {
                                    Modal_Groups grp = new Modal_Groups();
                                    grp.setGroupId(grp_cursor.getString(grp_cursor.getColumnIndex("GroupId")));
                                    grp.setGroupName(grp_cursor.getString(grp_cursor.getColumnIndex("GroupName")));
                                    grp.setVillageId(grp_cursor.getString(grp_cursor.getColumnIndex("VillageId")));
                                    grp.setProgramId(grp_cursor.getInt(grp_cursor.getColumnIndex("ProgramId")));
                                    grp.setGroupCode(grp_cursor.getString(grp_cursor.getColumnIndex("GroupCode")));
                                    grp.setSchoolName(grp_cursor.getString(grp_cursor.getColumnIndex("SchoolName")));
                                    grp.setVIllageName(grp_cursor.getString(grp_cursor.getColumnIndex("VIllageName")));
                                    grp.setDeviceId(grp_cursor.getString(grp_cursor.getColumnIndex("DeviceId")));
                                    groups.add(grp);
                                    grp_cursor.moveToNext();
                                }
                            }
                            BaseActivity.groupDao.insertAllGroups(groups);
                            grp_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //populate students
                        Cursor stu_cursor = null;
                        try {
                            stu_cursor = db.rawQuery("SELECT * FROM Students", null);
                            List<Modal_Student> students = new ArrayList<>();
                            if (stu_cursor.moveToFirst()) {
                                while (!stu_cursor.isAfterLast()) {
                                    Modal_Student stu = new Modal_Student();
                                    stu.setGroupId(stu_cursor.getString(stu_cursor.getColumnIndex("GroupId")));
                                    stu.setStudentId(stu_cursor.getString(stu_cursor.getColumnIndex("StudentId")));
                                    stu.setFirstName(stu_cursor.getString(stu_cursor.getColumnIndex("FirstName")));
                                    stu.setMiddleName(stu_cursor.getString(stu_cursor.getColumnIndex("MiddleName")));
                                    stu.setLastName(stu_cursor.getString(stu_cursor.getColumnIndex("LastName")));
                                    stu.setStud_Class(stu_cursor.getString(stu_cursor.getColumnIndex("Stud_Class")));
                                    stu.setStud_Class(stu_cursor.getString(stu_cursor.getColumnIndex("Stud_Class")));
                                    stu.setAge(stu_cursor.getString(stu_cursor.getColumnIndex("Age")));
                                    stu.setGender(stu_cursor.getString(stu_cursor.getColumnIndex("Gender")));
                                    stu.setFullName(stu.getFirstName() + " " + stu.getMiddleName() + " " + stu.getLastName());
                                    stu.setSentFlag(1);
                                    //                    stu.setGroupName();
                                    stu_cursor.moveToNext();
                                }
                            }
                            BaseActivity.studentDao.insertAllStudents(students);
                            stu_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean copied) {
        super.onPostExecute(copied);
        if (copied)
            interface_copyingDb.successCopyingExistingDb(folder_file.getAbsolutePath());
        else
            interface_copyingDb.failedCopyingExistingDb();
    }
}
