package com.hzeng.expan;

public class Setting {
/*
    private static final String STOP_CHARACTORS = new String(){".。?？!！，,、\n\f\r\t（）【】：；};

    public static String getStopCharactors() {
        return STOP_CHARACTORS.toString();
    }
*/
    public static boolean isInStopCharactor(char ch) {
        return ! String.valueOf(ch).matches("[a-z0-9A-Z\u4e00-\u9fa5]");
    }


    private static final int MATCH_DOCS = 10;

    public static int getMatchDocs() {
        return MATCH_DOCS;
    }


    private static final String[] SEED = new String[]{"白血病", "淋巴瘤", "贫血", "鼻炎", "白癜风"};

    public static String[] getSeed() {
        return SEED;
    }


    private static final int STEPS = 100;

    public static int getSteps() {
        return STEPS;
    }



    private static final int CONTEXT_WINDOW_SIZE = 2;

    public static int getContextWindowSize() {
        return CONTEXT_WINDOW_SIZE;
    }


    private static final int CONTEXT_FEATURE_RANK = 6;

    public static int getContextFeatureRank() {
        return CONTEXT_FEATURE_RANK;
    }


    private static final int ENTITY_RANK = 3;

    public static int getEntityRank() {
        return ENTITY_RANK;
    }



    private static final int CONTEXT_FEATURE_ENTITY_PRERANK = 5;

    public static int getContextFeatureEntityPrerank() {
        return CONTEXT_FEATURE_ENTITY_PRERANK;
    }


    private static final String TAGGER_MODULE_PATH = "lib/stanford-postagger-full/models/chinese-nodistsim.tagger";

    public static String getTaggerModulePath() {
        return TAGGER_MODULE_PATH;
    }
}
