package com.tea.ilearn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        public static final ArrayList<String> SUBJECTS_EN = new ArrayList<>(
                Arrays.asList(
                        CHINESE, MATH, ENGLISH,
                        PHYSICS, CHEMISTRY, BIOLOGY,
                        HISTORY, GEO, POLITICS
                )
        );
        public static final ArrayList<String> SUBJECTS_ZH = new ArrayList<>(
                Arrays.asList(
                        "语文", "数学", "英语",
                        "物理", "化学", "生物",
                        "历史", "地理", "政治"
                )
        );
        public static final Map<String, String> EN_ZH = new HashMap<String, String>(){{
            for (int i = 0; i < SUBJECTS_EN.size(); ++i) {
                put(SUBJECTS_EN.get(i), SUBJECTS_ZH.get(i));
            }
        }};
        public static final Map<String, String> ZH_EN = new HashMap<String, String>(){{
            for (int i = 0; i < SUBJECTS_EN.size(); ++i) {
                put(SUBJECTS_ZH.get(i), SUBJECTS_EN.get(i));
            }
        }};

        public static final String ERROR_MSG    = "系统错误，请稍后重试或联系客服。";
    }
}
