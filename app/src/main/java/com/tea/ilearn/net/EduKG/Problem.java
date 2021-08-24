package com.tea.ilearn.net.EduKG;

import com.google.gson.annotations.SerializedName;

public class Problem {
    @SerializedName("qAnswer")
    String answer;
    int id;
    @SerializedName("qBody")
    String raw;

    String description;
    String[] choices;

    public String getAnswer() {
        return answer;
    }

    public int getId() {
        return id;
    }

    public String getRaw() {
        return raw;
    }

    private void parse() {
        String[] regexes = new String[]{
                "A[.．]", "B[.．]", "C[.．]", "D[.．]",
        };
        String remained;
        String[] res;
        res = raw.split(regexes[0]);    // "A."
        description = res[0];
        remained = res[1];
        choices = new String[4];
        for (int i = 1; i < 4; i++) {
            res = remained.split(regexes[i]);
            choices[i - 1] = res[0];
            remained = res[1];
        }
        choices[3] = remained;
    }

    public String getDescription() {
        if (description == null)
            parse();
        return description;
    }

    public String[] getChoices() {
        if (choices == null)
            parse();
        return choices;
    }
}
