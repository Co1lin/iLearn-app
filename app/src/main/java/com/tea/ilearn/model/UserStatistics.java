package com.tea.ilearn.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

@Entity
public class UserStatistics {
    @Id
    public long id;

    @SerializedName("first_date")
    String firstDate;

    @SerializedName("entities_viewed")
    @Convert(converter = IntegerArrayConverter.class, dbType = String.class)
    ArrayList<Integer> entitiesViewed = new ArrayList<>(Collections.nCopies(7, 0));

    public UserStatistics setFirstDate(String firstDate) {
        this.firstDate = firstDate;
        return this;
    }

    public UserStatistics setEntitiesViewed(ArrayList<Integer> entitiesViewed) {
        this.entitiesViewed = entitiesViewed;
        return this;
    }

    public UserStatistics increaseLastNum() {
        entitiesViewed.set(6, entitiesViewed.get(6) + 1);
        return this;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public ArrayList<Integer> getEntitiesViewed() {
        return entitiesViewed;
    }

    public static class IntegerArrayConverter implements PropertyConverter<ArrayList<Integer>, String> {

        @Override
        public ArrayList<Integer> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
            ArrayList<Integer> relations = gson.fromJson(databaseValue, type);
            return relations;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<Integer> originalObj) {
            originalObj = new ArrayList<>(originalObj);
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }
}
