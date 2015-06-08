package yi.support.v1.menu;

import android.os.Handler;

class AnsycTaskExecutor {
    private Handler mHandler = new Handler();
    private Runnable mTask;
    
    public void execute(Runnable task) {
        cancel();
        mHandler.post(mTask = task);
    }
    
    public void cancel() {
        if (mTask != null) {
            mHandler.removeCallbacks(mTask);
            mTask = null;
        }
    }
}