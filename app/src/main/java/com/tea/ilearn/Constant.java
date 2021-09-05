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
        public static final String[][] INIT_KEYS = new String[][]{
                {"塘", "唐", "桃", "李", "语", "i", "宋", "白", "血", "言", "人", "百"}, //
                {"数", "学", "A", "P", "I", "从", "来", "没", "好", "过", "吐", "了"}, //
                {"日", "A", "B", "C", "a", "e", "i", "o", "u", "词", "动", "代"}, //
                {"力", "度", "能", "功", "平", "密", "射", "压", "电", "磁", "铁", "摩"}, //
                {"酸", "碱", "氧", "物", "化", "生", "火", "钾", "钠", "铝", "碳", "键"}, //
                {"粉", "人", "白", "能", "质", "壁", "体", "子", "氨", "D", "C", "血"}, //
                {"美", "苏", "中", "史", "日", "交", "政", "大", "小", "火", "战", "和"}, //
                {"年", "气", "山", "雨", "形", "型", "月", "候", "天", "面", "谷", "峡"}, //
                {"义", "责", "法", "道", "德", "人", "权", "力", "利", "立", "政", "府"}, //
        };

        public static final String ERROR_MSG    = "系统错误，请稍后重试或联系客服。";
    }
}
