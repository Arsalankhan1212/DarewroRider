package com.darewro.rider.view.models;

import android.graphics.drawable.Drawable;

public class OnboardingObject {
    private Drawable drawable;
    private int drawableId;
    private String name, details;

    public OnboardingObject(int drawableId, String name, String details) {
        this.drawableId = drawableId;
        this.name = name;
        this.details = details;
    }

    public OnboardingObject(Drawable drawable, String name, String details) {
        this.drawable = drawable;
        this.name = name;
        this.details = details;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
