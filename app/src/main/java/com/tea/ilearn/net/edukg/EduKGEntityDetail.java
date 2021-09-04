package com.tea.ilearn.net.edukg;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.tea.ilearn.model.Category;
import com.tea.ilearn.model.SearchHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;
import io.objectbox.converter.PropertyConverter;
import io.objectbox.relation.ToMany;

@Entity
public class EduKGEntityDetail {
    @Id
    public long id;
    @Index @Unique
    String uri;

    public ToMany<SearchHistory> searchHistories;

    @Convert(converter = EduKGRelationsConverter.class, dbType = String.class)
    @SerializedName("content")
    ArrayList<EduKGRelation> relations;
    @Convert(converter = EduKGPropertiesConverter.class, dbType = String.class)
    @SerializedName("property")
    ArrayList<EduKGProperty> properties;
    String label;
    String subject;
    String category;
    @Backlink(to = "entities")
    public ToMany<Category> categories;
    @Transient
    ArrayList<String> categoriesBuf;
    boolean starred;
    boolean viewed;

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        else if (this == obj)
            return true;
        else if (this.getClass() != obj.getClass())
            return false;
        else
            return uri.equals(((EduKGEntityDetail) obj).getUri());
    }

    public static class EduKGRelationsConverter implements PropertyConverter<ArrayList<EduKGRelation>, String> {

        @Override
        public ArrayList<EduKGRelation> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EduKGRelation>>(){}.getType();
            ArrayList<EduKGRelation> relations = gson.fromJson(databaseValue, type);
            return relations;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<EduKGRelation> originalObj) {
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }

    public static class EduKGPropertiesConverter implements PropertyConverter<ArrayList<EduKGProperty>, String> {

        @Override
        public ArrayList<EduKGProperty> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EduKGProperty>>(){}.getType();
            ArrayList<EduKGProperty> properties = gson.fromJson(databaseValue, type);
            return properties;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<EduKGProperty> originalObj) {
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }

    public String getSubject() {
        return subject;
    }

    public String getCategory() {
        return category;
    }

    public String getUri() {
        return uri;
    }

    public ArrayList<EduKGProperty> getProperties() {
        return properties;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<EduKGRelation> getRelations() {
        return relations;
    }

    public ArrayList<String> getCategoriesBuf() {
        return categoriesBuf;
    }

    public boolean isStarred() {
        return starred;
    }

    public boolean isViewed() {
        return viewed;
    }

    public EduKGEntityDetail setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public EduKGEntityDetail setCategory(String category) {
        this.category = category;
        return this;
    }

    public EduKGEntityDetail setCategory(ArrayList<String> categories) {
        this.category = String.join(" ", categories);
        this.categoriesBuf = categories;
        return this;
    }

    public EduKGEntityDetail setCategoriesBuf(ArrayList<String> categoriesBuf) {
        this.categoriesBuf = categoriesBuf;
        return this;
    }

    public EduKGEntityDetail setStarred(boolean starred) {
        this.starred = starred;
        return this;
    }

    public EduKGEntityDetail setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public EduKGEntityDetail setViewed(boolean viewed) {
        this.viewed = viewed;
        return this;
    }

    public EduKGEntityDetail setLabel(String label) {
        this.label = label;
        return this;
    }

    public EduKGEntityDetail setRelations(ArrayList<EduKGRelation> relations) {
        this.relations = relations;
        return this;
    }

    public EduKGEntityDetail setProperties(ArrayList<EduKGProperty> properties) {
        this.properties = properties;
        return this;
    }
}
