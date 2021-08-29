package com.tea.ilearn;

import java.util.ArrayList;
import java.util.Arrays;

public class Constant {
    public static class EduKG {
        public static final String CHINESE      = "chinese";
        public static final String ENGLISH      = "english";
        public static final String MATH         = "math";
        public static final String PHYSICS      = "physics";
        public static final String CHEMISTRY    = "chemistry";
        public static final String BIOLOGY      = "biology";
        public static final String HISTORY      = "history";
        public static final String GEO          = "geo";
        public static final String POLITICS     = "politics";
        public static final ArrayList<String> SUBJECTS = new ArrayList<>(
                Arrays.asList(
                        CHINESE, ENGLISH, MATH, PHYSICS,
                        CHEMISTRY, BIOLOGY, HISTORY, GEO, POLITICS
                )
        );

        public static final String ERROR_MSG    = "系统错误，请稍后重试或联系客服。";
    }
}
