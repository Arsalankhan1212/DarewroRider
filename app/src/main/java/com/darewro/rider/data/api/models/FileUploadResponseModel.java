package com.darewro.rider.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FileUploadResponseModel implements Parcelable {
    String id,pathUrl;

    public FileUploadResponseModel(String id, String pathUrl) {
        this.id = id;
        this.pathUrl = pathUrl;
    }

    protected FileUploadResponseModel(Parcel in) {
        id = in.readString();
        pathUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pathUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileUploadResponseModel> CREATOR = new Creator<FileUploadResponseModel>() {
        @Override
        public FileUploadResponseModel createFromParcel(Parcel in) {
            return new FileUploadResponseModel(in);
        }

        @Override
        public FileUploadResponseModel[] newArray(int size) {
            return new FileUploadResponseModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }
}
