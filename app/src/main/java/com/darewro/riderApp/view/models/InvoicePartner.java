package com.darewro.riderApp.view.models;

/**
 * Created by KMajeed on 15/01/2019.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class InvoicePartner implements Parcelable {

   public static final Creator<InvoicePartner> CREATOR = new Creator<InvoicePartner>() {
        @Override
        public InvoicePartner createFromParcel(Parcel in) {
            return new InvoicePartner(in);
        }

        @Override
        public InvoicePartner[] newArray(int size) {
            return new InvoicePartner[size];
        }
    };

    String id;
    String invoiceId;
    String partnerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSubamount() {
        return subAmount;
    }

    public void setSubamount(String subamount) {
        this.subAmount = subamount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<InvoicePackage> getInvoicePackages() {
        return invoiceItems;
    }

    public void setInvoicePackages(List<InvoicePackage> invoicePackages) {
        this.invoiceItems = invoicePackages;
    }

    String partnerName;
    String discount;
    String subAmount;
    String tax;
    String total;
    List<InvoicePackage> invoiceItems;

    public InvoicePartner() {

    }

    protected InvoicePartner(Parcel in) {
        id = in.readString();
        invoiceId = in.readString();
        partnerId = in.readString();
        partnerName = in.readString();
        discount = in.readString();
        subAmount = in.readString();
        tax = in.readString();
        total = in.readString();
        invoiceItems = in.createTypedArrayList(InvoicePackage.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(invoiceId);
        parcel.writeString(partnerId);
        parcel.writeString(partnerName);
        parcel.writeString(discount);
        parcel.writeString(subAmount);
        parcel.writeString(tax);
        parcel.writeString(total);
        parcel.writeTypedList(invoiceItems);
    }
}

