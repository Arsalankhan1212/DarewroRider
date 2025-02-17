package com.darewro.riderApp.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by KMajeed on 09/02/2019.
 */

public class Item implements Parcelable{


        public static final Creator<com.darewro.riderApp.data.api.models.Item> CREATOR = new Creator<com.darewro.riderApp.data.api.models.Item>() {
            @Override
            public com.darewro.riderApp.data.api.models.Item createFromParcel(Parcel in) {
                return new com.darewro.riderApp.data.api.models.Item(in);
            }

            @Override
            public com.darewro.riderApp.data.api.models.Item[] newArray(int size) {
                return new com.darewro.riderApp.data.api.models.Item[size];
            }
        };
        String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemObject getItemObject() {
        return itemObject;
    }

    public void setItemObject(ItemObject itemObject) {
        this.itemObject = itemObject;
    }

    String name;
        ItemObject itemObject;

        protected Item(Parcel in) {
            id = in.readString();
            name = in.readString();
            itemObject = in.readParcelable(ItemObject.class.getClassLoader());
        }

        public Item(String id, String name, ItemObject itemObject) {

            this.id = id;
            this.name = name;
            this.itemObject = itemObject;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }



        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(name);
            parcel.writeParcelable(itemObject,i);
        }

}
