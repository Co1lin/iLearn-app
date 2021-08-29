package com.tea.ilearn.net.edukg;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LinkResults {
    public class LinkedEntity {
        @SerializedName("entity_type")
        String entityType;
        @SerializedName("entity_url")
        String entityUri;
        @SerializedName("start_index")
        Integer startIndex;
        @SerializedName("end_index")
        Integer endIndex;
        String entity;

        public String getEntityType() {
            return entityType;
        }

        public String getEntityUri() {
            return entityUri;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public Integer getEndIndex() {
            return endIndex;
        }

        public String getEntity() {
            return entity;
        }
    }
    ArrayList<LinkedEntity> results;

    public ArrayList<LinkedEntity> getResults() {
        return results;
    }
}

