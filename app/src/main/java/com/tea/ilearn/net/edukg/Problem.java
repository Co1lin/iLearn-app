package com.tea.ilearn.net.edukg;

import com.google.gson.annotations.SerializedName;

public class Problem {
    @SerializedName("qAnswer")
    String answer;
    int id;
    @SerializedName("qBody")
    String raw = "";
    boolean drop = false;

    String description;
    String[] choices;

    public String getAnswer() {
        if (answer.contains("A"))
            return "A";
        else if (answer.contains("B"))
            return "B";
        else if (answer.contains("C"))
            return "C";
        else if (answer.contains("D"))
            return "D";
        return "";
    }

    public int getId() {
        return id;
    }

    public String getRaw() {
        return raw;
    }

    private void parse() {
        try {
            String[] regexes = new String[]{
                    "A[.．]", "B[.．]", "C[.．]", "D[.．]",
            };
            String remained;
            String[] res;
            res = raw.split(regexes[0]);    // "A.津津乐道发 B. 基分散的交流放大是 C圣诞快乐防守打法D"
            description = res[0];
            remained = res[1];
            choices = new String[4];
            for (int i = 1; i < 4; i++) {
                res = remained.split(regexes[i]);
                choices[i - 1] = res[0];
                remained = res[1];
            }
            choices[3] = remained;
        } catch (Exception e) {
            drop = true;
            description = null;
            choices = null;
        }
    }

    public String getDescription() {
        if (description == null && !drop)
            parse();
        return description;
    }

    public String[] getChoices() {
        if (choices == null && !drop)
            parse();
        return choices;
    }
}