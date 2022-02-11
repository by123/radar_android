package com.brotherhood.o2o.manager;

import com.brotherhood.o2o.application.NearApplication;
import com.facebook.stetho.Stetho;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class StethoManager {

    private static StethoManager instance;

    public static StethoManager getInstance() {
        if (instance == null) {
            instance = new StethoManager();
        }
        return instance;
    }

    private StethoManager() {

    }

    public void init() {
        IDSEnvManager.IDSEnv env = IDSEnvManager.getInstance().getEnv();
        switch (env) {
            case DEV:
                Stetho.initializeWithDefaults(NearApplication.mInstance);
                break;
            case TEST:
                Stetho.initializeWithDefaults(NearApplication.mInstance);
                break;
            case OFFICIAL:
                break;
        }
        //        Stetho.initialize(
//                Stetho.newInitializerBuilder(this)
//                        .enableDumpapp(
//                                Stetho.defaultDumperPluginsProvider(this))
//                        .enableWebKitInspector(
//                                Stetho.defaultInspectorModulesProvider(this))
//                        .build());
    }


}
