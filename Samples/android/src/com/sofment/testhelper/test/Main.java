package com.sofment.testhelper.test;

import net.bugs.testhelper.Interface;
import net.bugs.testhelper.TestHelper;
import net.bugs.testhelper.view.View;

import static net.bugs.testhelper.helpers.LoggerUtil.i;

public class Main {

    private static TestHelper testHelper;
    public static void main(String[] args) {
        testHelper = new TestHelper("config.properties", "0997b9b80ae4d10d");
        installAndLaunchApp();

        mainTestLogic();

        removeApk();
    }

    private static void mainTestLogic() {
        acceptAgreement();

        if(!waitForCategoryesList()) return;

        if(!waitForVideoList()) return;

        if(!chooseFirstVideoInList())

            if(!launchVideoStream()) return;

        if(!closeAds()) return;

        testHelper.sleep(30000);
        exitFomVideoStream();

        exitFromApp();
    }

    private static void exitFromApp() {
        for(int i = 0; i < 3; i ++) {
            testHelper.pressBack();
            testHelper.sleep(1000);
        }
    }

    private static void exitFomVideoStream() {
        testHelper.pressBack();

        View positiveButton = testHelper.getViewByDescriptor("Positive");
        if(positiveButton.exists()) {
            i("exit from video");
            positiveButton.click();
        }
    }

    private static boolean closeAds() {
        View button = testHelper.getViewByDescriptor("adCloseDescription");

        if(!button.exists()) return false;

        i("click on close ads button");
        button.click();
        return true;
    }

    private static boolean launchVideoStream() {
        View button = testHelper.getViewByDescriptor("Play video", true, false);

        if(!button.exists()) return false;
        i("Click on play button");
        button.click();

        View positiveButton = testHelper.getViewByDescriptor("Positive");
        if(positiveButton.exists()) {
            i("Click on online cinema player");
            positiveButton.click();
        }

        return testHelper.waitForExistsByDescriptor("adCloseDescription", 60000);
    }

    private static boolean chooseFirstVideoInList() {
        View videoList = testHelper.getViewByDescriptor("Video content", true, false);
        if(!videoList.exists()) return false;

        View firstVideo = videoList.getChildAt(0);
        if(!firstVideo.exists()) return false;

        i("click on first video in list.");
        firstVideo.click();

        if(!testHelper.waitForExistsByDescriptor("Play video", 10000)) {
            i("Press back button");
            testHelper.pressBack();
            return testHelper.waitForExistsByDescriptor("Play video", 10000);
        }
        return true;
    }

    private static boolean waitForVideoList() {
        View gridView = testHelper.getViewByDescriptor("Cinema Categories", true, false);

        while(gridView.swipeUp());
        while(gridView.swipeDown());

        View category = gridView.getChildAt(0);
        if(!category.exists()) return false;
        i("click on the first category");
        category.click();

        return testHelper.waitForExistsByDescriptor("Video content", 60000);
    }

    private static boolean waitForCategoryesList() {
        View button = testHelper.getViewByDescriptor("All Films");
        if(!button.exists()) return false;
        i("click on all films button");
        button.click();

        i("Wait for element exists by descriptor: Cinema Categories");
        return testHelper.waitForExistsByDescriptor("Cinema Categories", 60000);
    }

    private static void acceptAgreement() {
        View next = testHelper.getViewByDescriptor("Agreement Next", true, true);
        if(next.exists()) {
            i("click on the button \"Continue\"");
            next.click();
            testHelper.sleep(3000);
        }
    }

    private static void installAndLaunchApp() {
        installApk();

        //run application
        testHelper.startActivity("ru.five.tv.five.online/.SplashActivity");
        i("Online cinema launched");
        testHelper.sleep(5000);
    }

    private static void installApk() {
        i("install online-cinema.apk");
        testHelper.execAdbCommand(" install apk/online-cinema.apk", defaultCallBack);
    }

    private static void removeApk() {
        i("uninstall online-cinema.apk");
        testHelper.execAdbCommand(" uninstall ru.five.tv.five.online", defaultCallBack);
    }

    private static Interface.ICallBack defaultCallBack = new Interface.ICallBack() {
        @Override
        public void callback(String s) {

        }

        @Override
        public boolean isCancel() {
            return false;
        }
    };
}
