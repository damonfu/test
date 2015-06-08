package com.baidu.voiceprint;

public class SpeakerRec {

    // Error code
    public static final int SUCCESSFUL = 0; //
    public static final int DATA_NOT_VALID = 1; //
    public static final int MODEL_LOADING_FAILED = 2; //
    public static final int MORE_DATA_REQUIRED = 3; //
    public static final int DATA_TOO_MUCH = 4; //
    public static final int FILE_WRITING_ERROR = 5; //
    public static final int OTHER_ERROR = 6; //

    // DECISION
    public static final int REJECT = 0;
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;

    // PROGRAM_MODE
    public static final int ENROLL_PUBLIC = 0;
    public static final int ENROLL_PRIVATE = 1;
    public static final int TEST = 2;

    static {
        System.loadLibrary("SpeakerRec");
    }

    /*
     * Initialise the necessary objects. In this function, the speaker model
     * will be loaded.* The size of the speaker model is about 3MB.
     */
    public static final native int speakerInit(int program_mode, String filename);

    // 加载数据
    public static final native int speakerAddToBuffer(short[] data, int iSize,
            int isLastFrame);

    // 注册
    public static final native int speakerEnroll();

    // 识别测试
    public static final native int speakerTest();

    // 识别测试
    public static final native int speakerInitNewTest();

    // 清除数据
    public static final native int speakerClearCurrUtterance();

    // 清除当前内存资源
    public static final native int speakerClearSession();

    // 获得识别score
    public static final native float speakerGetScore();

    // 获得识别Decision
    public static final native int speakerGetDecision();

}
