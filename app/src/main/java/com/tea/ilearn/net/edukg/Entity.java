package com.tea.ilearn.net.edukg;

import androidx.annotation.Nullable;

public class Entity {
    String label;
    String category;
    String uri;

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
            return uri.equals(((Entity) obj).getUri());
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return category;
    }

    public String getUri() {
        return uri;
    }
}
