package com.jiabo.printer;

/**
 * 打印服务执行结果的回调
 */
interface ICallback {
	oneway void onRunResult(boolean isSuccess, int code, String msg);
}