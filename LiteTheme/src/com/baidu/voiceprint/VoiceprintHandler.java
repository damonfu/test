package com.baidu.voiceprint;

import com.baidu.android.vad.JNI;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.os.Process;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import android.os.Environment;
import android.text.format.Time;
import java.io.IOException;

/**
 * VoiceprintHandler for baidu voiceprint handling.
 * 
 */
public final class VoiceprintHandler {
	private final static boolean DBG = true;
    private final static String TAG = "VoiceprintHandler";

    public static final int SUCCESSFUL = SpeakerRec.SUCCESSFUL; //
    public static final int DATA_NOT_VALID = SpeakerRec.DATA_NOT_VALID; //
    public static final int MODEL_LOADING_FAILED = SpeakerRec.MODEL_LOADING_FAILED; //
    public static final int MORE_DATA_REQUIRED = SpeakerRec.MORE_DATA_REQUIRED; //
    public static final int DATA_TOO_MUCH = SpeakerRec.DATA_TOO_MUCH; //
    public static final int FILE_WRITING_ERROR = SpeakerRec.FILE_WRITING_ERROR; //
    public static final int OTHER_ERROR = SpeakerRec.FILE_WRITING_ERROR; //

    // DECISION
    public static final int REJECT = 0;
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;

    // PROGRAM_MODE
    public static final int ENROLL_PUBLIC = 0;
    public static final int ENROLL_PRIVATE = 1;
    public static final int TEST = 2;

    private int mVoiceTransState = VOICE_TRANS_UNINIT;
    public static final int VOICE_TRANS_INIT = 0;
    public static final int VOICE_TRANS_ADD_BUFFER = 1;
    public static final int VOICE_TRANS_TEST = 2;
    public static final int VOICE_TRANS_ENROLL = 3;
    public static final int VOICE_TRANS_CLEAR_UTTERANCE = 4;
    public static final int VOICE_TRANS_INIT_NEW_TEST = 5;
    public static final int VOICE_TRANS_UNINIT = 6;
    
    public static final String mModelPath = Environment.getDataDirectory()+"/workspace.dat";//"//mnt//sdcard//pcm//workspace.dat";
    public static final String mTestDumpDir = Environment.getDataDirectory()+"/dumptest";
    public static final String mEnrollDumpDir = Environment.getDataDirectory()+"/dumpenroll";
    // single instance
    private static VoiceprintHandler mInstance;
    private boolean isRec = false;
    private int mMode;

    private int mAmplitude = 0;
    private float mTestScore = 0;

    // recording and processing
    private RecordingTask mRecordingTask;
    private AudioRecord mRawRecorder; // raw audio recorder
    private static JNI mPostProcessor; // voice data post processing engine

    /* Raw recording configuration */
    private final static int SAMPLE_RATE = 16000; // Hz
    private final static int MAX_WAIT_DURATION = 150; //150*32ms
    private final static int MAX_SP_DURATION = 280; //280*32ms
    private final static int OUTPUT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private final static int CHANNEL_CONF = AudioFormat.CHANNEL_IN_MONO;
    private final static int RECORDING_INTERVAL = 100; // 0.1 second
    private final static int BUFFER_SIZE = 16384 * 4; // in bytes, 16KB
    private final static int RCV_BUFFER_SIZE = BUFFER_SIZE / 4 / 2; // in shorts
    private final static short[] mRawDataBuffer = new short[RCV_BUFFER_SIZE];
    /* Post processed data buffer size. */
    private final static int PP_BUFFER_SIZE = BUFFER_SIZE / 4; // in bytes

    // state listener
    private StateListener mStateListener = mNullStateListener; // state listener
                                                               // in use
    private StateListener mRegisteredStateListener; // registered state listener

    // a state listener that does nothing
    private final static StateListener mNullStateListener = new StateListener() {
        public void onPartialData(short[] data, int length, int lastFrame) {
        }

        public void onError(int errno) {
        }

        public void onVoiceStart() {
        }

        public void onVoiceEnd() {
        }
    };

    /**
     * VoiceprintHandler State Listener. Callbacks of the VoiceprintHandler.
     * Notice: do not block on these callbacks.
     */
    public interface StateListener {
        // Error numbers
        /* generic error */
        int ERROR = -1;
        int START_ERROR = -2;
        int ILLEGAL_STATE = -3;
        int INVALID_OPERATION = -4;
        int SPEECH_TOO_SHORT = -5;
        int SILENCE_TOO_LONG = -6;

        /**
         * PartialData. Called on partial data ready.
         * 
         * @param data
         *            buffer
         * @param begin
         *            begin index
         * @param length
         *            length of data
         */
        void onPartialData(short[] data, int length, int lastFrame);

        /**
         * Error. Called on error condition.
         */
        void onError(int errno);

        /**
         * Voice Start Point. Called on voice start point detected.
         */
        void onVoiceStart();

        /**
         * Voice End Point. Called on voice end point detected.
         */
        void onVoiceEnd();
    }

    /**
     * Recording task.
     * 
     * This class exists only for coordinationg wait and notify.
     */

    private class RecordingTask extends Thread {
        // current recording
        private boolean isRecording = false;
        private boolean isExiting = false; // after become true, never become
                                           // false again

        @Override
        public void run() {
            if(DBG) Log.d(TAG, "ReadingTask began");

            // get an higher priority
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (true) {
                try {
                    synchronized (this) {
                        if (!isRecording)
                            wait();

                        if (!isRecording) {
                            if(DBG) Log.d(TAG, "Fake wakeup? ");
                            continue; // fake wakeup
                        }
                    }

                    // now, we are trying to recording
                    doRecording();                

                    // recording finished
                    synchronized (this) {
                        isRecording = false;
                    }

                } catch (InterruptedException e) {
                    synchronized (this) {
                        if (isExiting) {
                            if(DBG) Log.d(TAG, "RecordingTask Exited");
                            // terminate this thread
                            return;
                        } else if (!isRecording) {
                            // stop current recording
                            // currently does nothing
                            if(DBG) Log.d(TAG, "RecordingTask stopped");
                        } else {
                            Log.w(TAG,
                                    "Recording thread interrupted by something unknown.");
                            continue;
                        }
                    }
                }
            }

        }

        /**
         * Recording. Recording can be done multiple times.
         */
        public synchronized void startRecording() {
            if(DBG) Log.d(TAG, "startRecording");
            if (isRecording) {
            	if(DBG) Log.d(TAG, "Double start recording.");
                return;
            }

            mStateListener = mRegisteredStateListener;
            isRecording = true;
            notify();
        }

        /**
         * Stop current recording.
         */
        public synchronized void stopRecording() {
            
            if (!isRecording)
                return;
            if(DBG) Log.e(TAG, "stopRecording");
            mStateListener = mNullStateListener; // donot send messages any more
            isRecording = false;
            //closeDumpfile2();
            interrupt();
        }

        /**
         * Terminate recording thread.
         */
        public synchronized void close() {
            isRecording = false;
            isExiting = true;
            interrupt();
        }
    }

    // load the post processing library.
    static {
             // Init post processor
        mPostProcessor = new JNI();
        int ret = mPostProcessor.mfeSetParam(JNI.PARAM_MAX_WAIT_DURATION, MAX_WAIT_DURATION);
        if(DBG) Log.d(TAG, "JNI.PARAM_MAX_WAIT_DURATION: " + ret);
        if(DBG) Log.d(TAG, "get JNI.PARAM_MAX_WAIT_DURATION: " + mPostProcessor.mfeGetParam(JNI.PARAM_MAX_WAIT_DURATION));
        ret = mPostProcessor.mfeSetParam(JNI.PARAM_MAX_SP_DURATION, MAX_SP_DURATION);
        if(DBG) Log.d(TAG, "JNI.PARAM_MAX_SP_DURATION: " + ret);
        if(DBG) Log.d(TAG, "get JNI.PARAM_MAX_SP_DURATION: " + mPostProcessor.mfeGetParam(JNI.PARAM_MAX_SP_DURATION));             
        if (mPostProcessor.mfeInit(SAMPLE_RATE, 0) < 0)
            throw new IllegalStateException("mfeInit() failed.");
        if (mPostProcessor.mfeOpen() < 0)
            throw new IllegalStateException("mfeOpen() failed.");

    }

    /**
     * Factory method.
     */
    synchronized public static VoiceprintHandler getRecorder() {
        if (mInstance == null)
            mInstance = new VoiceprintHandler();

        return mInstance;
    }

    /**
     * Constructor.
     */
    public VoiceprintHandler() {
        // Init raw recorder
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                CHANNEL_CONF, OUTPUT_FORMAT);
        if (bufferSize < BUFFER_SIZE)
            bufferSize = BUFFER_SIZE;

        mRawRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, CHANNEL_CONF, OUTPUT_FORMAT, bufferSize);

        // Init worker process
        mRecordingTask = new RecordingTask();
        mRecordingTask.start();
        Thread.yield();

        if(DBG) Log.d(TAG, "Initialized");

    }

    /**
     * Close this object. This recorder can do nothing except gc-ed after this
     * call.
     */
    synchronized public void close() {
        isRec = false;
        freeRecorder();
        mRecordingTask.close();
        mRawRecorder.release();
        if(DBG) Log.d(TAG, "Closed");
    }

    public static synchronized void freeRecorder() {
        VoiceprintHandler.mInstance = null;
    }

    public int getAmplitude() {
        int temp = mAmplitude;
        mAmplitude = 0;
        return temp;
    }

    public boolean isRecording() {
        return isRec;
    }

    // recording and post processing
    private void doRecording() throws InterruptedException {
        if (mStateListener == null) {
            if(DBG) Log.d(TAG,
                    "do recording while these's no StateListener registered.");
            return;
        }

        mPostProcessor.mfeStop();
        if (mPostProcessor.mfeStart() < 0) {
            mStateListener.onError(StateListener.START_ERROR);
            return;
        }

        try {
            mRawRecorder.startRecording();
        } catch (IllegalStateException e) {
            mPostProcessor.mfeStop();
            mStateListener.onError(StateListener.ILLEGAL_STATE);
            return;
        }
        try {
            while (true) {
                // sleep a while before retrieving data
                Thread.sleep(RECORDING_INTERVAL);
                int length = mRawRecorder.read(mRawDataBuffer, 0,
                        RCV_BUFFER_SIZE);
                if (length == AudioRecord.ERROR_INVALID_OPERATION) {
                    //mStateListener.onError(StateListener.INVALID_OPERATION);
                    mStateListener.onVoiceEnd();
                    mStateListener = mNullStateListener;
                    return;
                }

                if (length == AudioRecord.ERROR_BAD_VALUE) {
                    mStateListener.onError(StateListener.ERROR);
                    return;
                }
                // fixed by phill, 2011-7-19

                if (length == 0)
                    continue;
                /* try to get amplitude of the voice */
                short[] targetBuffer = new short[length];
                int v = 0;
                for (int i = 0; i < length; i++) {
                    short tmp = mRawDataBuffer[i];
                    targetBuffer[i] = tmp;
                    if (tmp < 0) {
                        tmp = (short)-tmp;
                    }
                    if (mAmplitude < tmp) {
                        mAmplitude = tmp;
                    }
                }
                
                mPostProcessor.mfeSendData(mRawDataBuffer, length);
                //addToDumpfile2(targetBuffer, length);
                int ppFlag = mPostProcessor.mfeDetect();
                
                switch (ppFlag) {
                case 0:
                    mStateListener.onPartialData(targetBuffer, length, 0);
                    if(DBG) Log.d(TAG, "vad voice not begin yet");
                    break;
                case 1:
                    if(DBG) Log.d(TAG, "vad onVoiceStart()");
                    mStateListener.onVoiceStart();
                    mStateListener.onPartialData(targetBuffer, length, 0);
                    break;
                case 2:
                    if(DBG) Log.d(TAG, "vad onVoiceEnd()");
                case 3:
                    if(DBG) Log.d(TAG, "vad onError(SILENCE_TOO_LONG)");
                case 4:
                    if(DBG) Log.d(TAG, "vad onError(SPEECH_TOO_SHORT)");  
                default:
                    if(DBG) Log.d(TAG, "vad other error in PostProcessor");
                    mStateListener.onPartialData(targetBuffer, length, 1);
                    mStateListener.onVoiceEnd();
                    mStateListener = mNullStateListener;
                    return;                                   
                }
            }
        } finally {
            // stop RawRecorder and PostProcessor
            try {
                if (mPostProcessor.mfeStop() < 0) {
                	if(DBG) Log.d(TAG, "Failed to stop PostProcessor");
                    mStateListener.onError(StateListener.ILLEGAL_STATE);
                }
                mRawRecorder.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to stop AudioRecord :" + e.toString());
                mStateListener.onError(StateListener.ILLEGAL_STATE);
            }
        }

    }

    /**
     * Set state listener.
     * 
     * @param listener
     *            .
     * @return original listener or null if there isn't one.
     */
    public StateListener setStateListener(StateListener listener) {
        StateListener original = mRegisteredStateListener;
        mRegisteredStateListener = listener;
        return original;
    }

    /**
     * Start Recording.
     */
    public void startRecording() {
        isRec = true;
        mRecordingTask.startRecording();
    }

    /**
     * Stop Recording.
     */
    public void stopRecording() {
        isRec = false;
        mRecordingTask.stopRecording();
    }

    /**
     * Stop Recording.
     */
    
    public int sendData(short[] data, int length, int lastFrame) {
        if (mVoiceTransState == VOICE_TRANS_UNINIT || mVoiceTransState == VOICE_TRANS_ENROLL ||
            mVoiceTransState == VOICE_TRANS_TEST ) {
        	if(DBG) Log.d(TAG, "calling speakerAddToBuffer in wrong state: " + mVoiceTransState);
            return OTHER_ERROR;
        }
        mVoiceTransState = VOICE_TRANS_ADD_BUFFER;  
        int ret = SpeakerRec.speakerAddToBuffer(data, length, lastFrame);
        if(DBG) Log.d(TAG, "speakerAddToBuffer return: " + ret);
        //addToDumpfile(data, length);
        if (lastFrame > 0) {
            //closeDumpfile();
            //closeDumpfile2();
        }
        return ret;
    }

    /**
     * Begin a Voice Transaction.
     * 
     * @return success or fail to begin a voice transaction
     */
    public boolean beginVoiceTransaction(int mode) throws IOException {
        if (mVoiceTransState != VOICE_TRANS_UNINIT) {
            Log.i(TAG, "calling beginVoiceTransaction in wrong state: " + mVoiceTransState);
            return false;
        }
        if(DBG) Log.d(TAG, "calling speakerInit:" );
        int ret = SpeakerRec.speakerInit(mode, mModelPath);
        mMode = mode;
        if(DBG) Log.d(TAG, "speakerInit: " + ret);
        if (ret == SpeakerRec.SUCCESSFUL) {
            mVoiceTransState = VOICE_TRANS_INIT;
        }
        return (ret == SpeakerRec.SUCCESSFUL);
    }

    /**
     * Begin a Voice Transaction.
     * 
     * @return success or fail to begin a voice transaction
     */
    public boolean beginVoiceTransaction(int mode, String modeFilePath) throws IOException {
        
        if (mVoiceTransState != VOICE_TRANS_UNINIT) {
        	if(DBG) Log.d(TAG, "calling beginVoiceTransaction in wrong state: " + mVoiceTransState);
            return false;
        }
        if(DBG) Log.d(TAG, "calling speakerInit:" );
        int ret = SpeakerRec.speakerInit(mode, modeFilePath);
        mMode = mode;
        if(DBG) Log.d(TAG, "speakerInit: " + ret);
        if (ret == SpeakerRec.SUCCESSFUL) {
            mVoiceTransState = VOICE_TRANS_INIT;
        }
        return (ret == SpeakerRec.SUCCESSFUL);
    }

    /**
     * Abort a Voice Transaction. client can call this at any time.
     * 
     * @param id
     *            the transaction to be aborted.
     */
    public void clearVoiceTestTransaction() {
        if (mVoiceTransState != VOICE_TRANS_TEST) {
        	if(DBG) Log.d(TAG, "calling clearVoiceTestTransaction in wrong state: " + mVoiceTransState);
            return;
        }    
        if(DBG) Log.d(TAG, "calling speakerInitNewTest: ");
        int ret = SpeakerRec.speakerInitNewTest();
        if(DBG) Log.d(TAG, "speakerInitNewTest result: " + ret);
        mVoiceTransState = VOICE_TRANS_INIT_NEW_TEST;
        //closeDumpfile();
    }

    public void clearVoiceEnrollTransaction() {        
        if (mVoiceTransState == VOICE_TRANS_ADD_BUFFER) {
        	if(DBG) Log.d(TAG, "mVoiceTransState VOICE_TRANS_ADD_BUFFER");
        	if(DBG) Log.d(TAG, "call speakerClearCurrUtterance: ");
            SpeakerRec.speakerClearCurrUtterance();
            if(DBG) Log.d(TAG, "speakerClearCurrUtterance: ");
        }
        if(DBG) Log.d(TAG, "clearVoiceEnrollTransaction: calling speakerClearSession: ");
        
        int ret = SpeakerRec.speakerClearSession();
        if(DBG) Log.d(TAG, "speakerClearSession result: " + ret);
        mVoiceTransState = VOICE_TRANS_UNINIT;
        //closeDumpfile();
    }

    /**
     * End voice transaction. End a sucessful voice transaction.
     */
    public synchronized void endVoiceTransaction() {
        close();
        if (mVoiceTransState == VOICE_TRANS_UNINIT) {
        	if(DBG) Log.d(TAG, "double endVoiceTransaction ");
            return;
        }          
        if(DBG) Log.d(TAG, "endVoiceTransaction: calling speakerClearSession: ");
        int ret = SpeakerRec.speakerClearSession();
        if(DBG) Log.d(TAG, "speakerClearSession result: " + ret);
        mVoiceTransState = VOICE_TRANS_UNINIT;
        //closeDumpfile();
    }

    /**
     * test the voiceprint of the recorded voice
     * */
    public synchronized int testVoiceprint() throws IOException {
        int decision = 0;
        mTestScore = -1;

        if (mVoiceTransState != VOICE_TRANS_ADD_BUFFER) {
        	if(DBG) Log.d(TAG, "calling testVoiceprint in wrong state: " + mVoiceTransState);
            return decision;
        }  

        if(DBG) Log.d(TAG, "before testVoiceprint");
        int ret = SpeakerRec.speakerTest();
        if(DBG) Log.d(TAG, "testVoiceprint result: " + ret);
        if (ret == SpeakerRec.SUCCESSFUL) {
            mTestScore = SpeakerRec.speakerGetScore();
            if (mTestScore > 0)
                decision = SpeakerRec.speakerGetDecision();
        }
        if(DBG) Log.d(TAG, "Singapore test score: " + mTestScore + " ; decision: " + decision);
        mVoiceTransState = VOICE_TRANS_TEST;
        //closeDumpfile();
        return decision;
    }

    /**
     * test the voiceprint of the recorded voice
     * */
    public synchronized float getTestScore() {
        return mTestScore;
    }
    
    /**
     * Get voice result.
     * 
     */
    public synchronized int enrollVoiceprint() throws IOException {
        if (mVoiceTransState != VOICE_TRANS_ADD_BUFFER) {
        	if(DBG) Log.d(TAG, "calling enrollVoiceprint in wrong state: " + mVoiceTransState);
            return OTHER_ERROR;
        }
        
        if(DBG) Log.d(TAG, "before speakerEnroll  score: ");
        int ret = SpeakerRec.speakerEnroll();
        if(DBG) Log.d(TAG, "speakerEnroll result: " + ret);
        mVoiceTransState = VOICE_TRANS_ENROLL;
        //closeDumpfile();
        return ret;
    }
    
/*    
    private DataOutputStream dos = null;
    public void createDumpfile(){
        if (dos == null) {

            
            if(DBG) Log.e(TAG, "+++++dump, create a new dump file, mMode: " + mMode);
            File dumpDir;
            if (mMode == TEST) {
                dumpDir = new File(mTestDumpDir); 
            } else {
                dumpDir = new File(mEnrollDumpDir);            
            }
            File mDumpFile = null;
            dos = null;
            try {
                if (!dumpDir.isDirectory()) {
                    dumpDir.mkdirs();
                }
            
                Time time = new Time();
                time.setToNow();
                String filename = "Dump_" + time.format("%H%M%S");
                mDumpFile = new File(dumpDir + "/" + filename + ".pcm");
                int i = 0;
                final int limit = 100;
                while (!mDumpFile.createNewFile() && i < limit) {
                    ++i;
                    mDumpFile = new File(String.format("%s/%s(%d)%s", dumpDir,
                            filename, i, ".pcm"));
                }
            
                if (i >= limit) {
                    if(DBG) Log.d(TAG, "+++++dump can't create file");
                    return;
                }
            
                if (mDumpFile != null) {
                    dos = new DataOutputStream(new FileOutputStream(mDumpFile));
                }
            } catch (IOException e) {
                if(DBG) Log.d(TAG, "+++++dump sdcard error");
                return;
            }

        }

    }

    public void closeDumpfile(){
        try {
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            dos = null;
        } catch (IOException e) {
            Log.e(TAG, "+++++dump close stream error : " +e.toString());
        }
    }
    public void addToDumpfile(short[] data, int length){
        if (dos == null) {
           createDumpfile();
        }
        try {
            if (dos != null) {
                Log.e(TAG, "+++ addToDumpfile, length: " + length);
                for (int tempLen = 0; tempLen < length; tempLen++) {
                    dos.writeShort(data[tempLen]);
                }
            } else {
                Log.w(TAG, "+++ dump error, null file");
            }
        } catch (IOException e) {
            Log.e(TAG, "+++++dump write data error :"+ e.toString());
        }

    }

    private DataOutputStream dos2 = null;
    public void createDumpfile2(){
        if (dos2 == null) {           
            if(DBG) Log.e(TAG, "+++++dump2, create a new dump file, mMode: " + mMode);
            File dumpDir;
            if (mMode == TEST) {
                dumpDir = new File(mTestDumpDir); 
            } else {
                dumpDir = new File(mTestDumpDir);            
            }
            File mDumpFile = null;
            dos2 = null;
            try {
                if (!dumpDir.isDirectory()) {
                    dumpDir.mkdirs();
                }
            
                Time time = new Time();
                time.setToNow();
                String filename = "Dump_record_" + time.format("%H%M%S");
                mDumpFile = new File(dumpDir + "/" + filename + ".pcm");
                int i = 0;
                final int limit = 100;
                while (!mDumpFile.createNewFile() && i < limit) {
                    ++i;
                    mDumpFile = new File(String.format("%s/%s(%d)%s", dumpDir,
                            filename, i, ".pcm"));
                }
            
                if (i >= limit) {
                    if(DBG) Log.d(TAG, "+++++dump2 can't create file");
                    return;
                }
            
                if (mDumpFile != null) {
                    dos2 = new DataOutputStream(new FileOutputStream(mDumpFile));
                }
            } catch (IOException e) {
                if(DBG) Log.d(TAG, "+++++dump2 sdcard error");
                return;
            }

        }

    }

    public void addToDumpfile2(short[] data, int length){
        if (dos2 == null) {
           return;
        }
        try {
            if (dos2 != null) {
                Log.e(TAG, "+++ addToDumpfile2, length: " + length);
                for (int tempLen = 0; tempLen < length; tempLen++) {
                    dos2.writeShort(data[tempLen]);
                }
            } else {
                Log.w(TAG, "+++ dump2 error, null file");
            }
        } catch (IOException e) {
            Log.e(TAG, "+++++dump2 write data error :"+ e.toString());
        }

    }
    public void closeDumpfile2(){
        try {
            if (dos2 != null) {
                dos2.flush();
                dos2.close();
            }
            dos2 = null;
        } catch (IOException e) {
            Log.e(TAG, "+++++dump2 close stream error : " +e.toString());
        }
    }

    public void cleanupDumpfile(){
        File dumpDir;
        if (mMode == TEST) {
            dumpDir = new File(mTestDumpDir); 
        } else {
            dumpDir = new File(mEnrollDumpDir);            
        }
        if (dumpDir.isDirectory()) {
            File[] array = dumpDir.listFiles();
            if (null == array) {
                return;                
            }
            for (int i =0; i < array.length; i++){
                File f = array[i];
                f.delete();
            }
        }
    }*/
}

