package com.tea.ilearn.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tea.ilearn.Constant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

@Entity
public class Preference {
    @Id
    public long id;

    @Convert(converter = StringListConverter.class, dbType = String.class)
    ArrayList<String> subjects = new ArrayList<>(Arrays.asList(
            Constant.EduKG.CHINESE, Constant.EduKG.MATH
    ));

    boolean dark = true;

    public static class StringListConverter implements PropertyConverter<ArrayList<String>, String> {

        @Override
        public ArrayList<String> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            ArrayList<String> array = gson.fromJson(databaseValue, type);
            return array;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<String> originalObj) {
            originalObj = new ArrayList<>(originalObj);
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public boolean isDark() {
        return dark;
    }

    public Preference setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
        return this;
    }

    public Preference setDark(boolean dark) {
        this.dark = dark;
        return this;
    }
}
