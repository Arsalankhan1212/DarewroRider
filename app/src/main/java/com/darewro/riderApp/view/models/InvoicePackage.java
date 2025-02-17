package com.darewro.riderApp.view.models;

/**
 * Created by KMajeed on 15/01/2019.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class InvoicePackage implements Parcelable {

    public static final Creator<InvoicePackage> CREATOR = new Creator<InvoicePackage>() {
        @Override
        public InvoicePackage createFromParcel(Parcel in) {
            return new InvoicePackage(in);
        }

        @Override
        public InvoicePackage[] newArray(int size) {
            return new InvoicePackage[size];
        }
    };

    String id;
    String invoicePartnerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoicePartnerId() {
        return invoicePartnerId;
    }

    public void setInvoicePartnerId(String invoicePartnerId) {
        this.invoicePartnerId = invoicePartnerId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    String packageId;
    String packageType;
    String packageName;
    String discount;
    String tax;
    String price;
    String quantity;

    public InvoicePackage() {

    }

    protected InvoicePackage(Parcel in) {
        id = in.readString();
        invoicePartnerId = in.readString();
        packageId = in.readString();
        packageType = in.readString();
        packageName = in.readString();
        discount = in.readString();
        tax = in.readString();
        price = in.readString();
        quantity = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(invoicePartnerId);
        parcel.writeString(packageId);
        parcel.writeString(packageType);
        parcel.writeString(packageName);
        parcel.writeString(discount);
        parcel.writeString(tax);
        parcel.writeString(price);
        parcel.writeString(quantity);
    }
}

