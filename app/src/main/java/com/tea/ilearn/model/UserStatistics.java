package com.tea.ilearn.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tea.ilearn.net.edukg.EduKGRelation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

@Entity
public class UserStatistics {
    @Id
    public long id;

    Date firstDate;

    @Convert(converter = IntegerArrayConverter.class, dbType = String.class)
    ArrayList<Integer> entitiesViewed = new ArrayList<>();

    public UserStatistics setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
        return this;
    }

    public UserStatistics setEntitiesViewed(ArrayList<Integer> entitiesViewed) {
        this.entitiesViewed = entitiesViewed;
        return this;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public ArrayList<Integer> getEntitiesViewed() {
        return entitiesViewed;
    }

    public static class IntegerArrayConverter implements PropertyConverter<ArrayList<Integer>, String> {

        @Override
        public ArrayList<Integer> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EduKGRelation>>(){}.getType();
            ArrayList<Integer> relations = gson.fromJson(databaseValue, type);
            return relations;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<Integer> originalObj) {
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }
}
