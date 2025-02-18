package com.darewro.rider.data.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Packages implements Parcelable {

    public static final Creator<Packages> CREATOR = new Creator<Packages>() {
        @Override
        public Packages createFromParcel(Parcel in) {
            return new Packages(in);
        }

        @Override
        public Packages[] newArray(int size) {
            return new Packages[size];
        }
    };
    String id;
    String packageType;
    String discount;
    String tax;
    String price;
    String quantity;
    String packageID;
    String packageName;
    List<ProductVarientsValues> productVarientsValues;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    Item item;
    protected Packages(Parcel in) {
        id = in.readString();
        packageType = in.readString();
        discount = in.readString();
        tax = in.readString();
        price = in.readString();
        quantity = in.readString();
        packageID = in.readString();
        packageName = in.readString();
        item = in.readParcelable(Item.class.getClassLoader());
    }

    public Packages(String id, String packageType, String discount, String tax, String price, String quantity, String packageID, String packageName, List<ProductVarientsValues> productVarientsValues, Item item) {

        this.id = id;
        this.packageType = packageType;
        this.discount = discount;
        this.tax = tax;
        this.price = price;
        this.quantity = quantity;
        this.packageID = packageID;
        this.packageName = packageName;
        this.productVarientsValues = productVarientsValues;
        this.item =  item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<ProductVarientsValues> getProductVarientsValues() {
        return productVarientsValues;
    }

    public void setProductVarientsValues(List<ProductVarientsValues> productVarientsValues) {
        this.productVarientsValues = productVarientsValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(packageType);
        parcel.writeString(discount);
        parcel.writeString(tax);
        parcel.writeString(price);
        parcel.writeString(quantity);
        parcel.writeString(packageID);
        parcel.writeString(packageName);
        parcel.writeParcelable(item,i);
    }
}
