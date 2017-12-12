package com.pitaya.baselib;

public class EnvHelper {
    private String checkoutUrl;
    private String vipPayUrl;
    private String printerUrl;

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public String getVipPayUrl() {
        return vipPayUrl;
    }

    public String getPrinterUrl() {
        return printerUrl;
    }


    public void switchToDebug() {
        checkoutUrl = "http://www.baidu.com";
        vipPayUrl = "http://www.baidu.com";
        printerUrl = "http://www.baidu.com";
    }

    public void switchToStage() {
        checkoutUrl = "http://www.jianshu.com";
        vipPayUrl = "http://www.jianshu.com";
        printerUrl = "http://www.jianshu.com";
    }

    public void switchToRelease() {
        checkoutUrl = "http://www.google.com";
        vipPayUrl = "http://www.google.com";
        printerUrl = "http://www.google.com";
    }

    private static volatile EnvHelper mInstance;

    public static EnvHelper getInstance() {
        if (mInstance == null) {
            synchronized (UserCenter.class) {
                if (mInstance == null) {
                    mInstance = new EnvHelper();
                }
            }
        }
        return mInstance;
    }
}