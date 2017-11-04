package com.livelife.pioalert;

import java.io.Serializable;

/**
 * Created by Shoeb on 23/10/17.
 */

public class CategoryProductModal implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryProductModal(String name, String id,boolean isSelected) {
        this.name = name;
        this.id = id;
        this.isSelected = isSelected;
    }

    String name = "";
    String id= "";
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
