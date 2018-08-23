package com.wulian.wlcamera.tools;

/**
 * Created by zbl on 2018/1/8.
 * 控制间隔时间 <p/>
 * 主要用于控制按键，防止多次点击
 */

public class TimeLock {
    private long lastTime;
    private long lockTime = 800;

    public void lock() {
        lastTime = System.currentTimeMillis();
    }

    public boolean isLock() {
        return System.currentTimeMillis() - lastTime < lockTime;
    }

    /**
     * 设置间隔时间，默认800毫秒
     *
     * @param lockTime
     */
    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }
}
