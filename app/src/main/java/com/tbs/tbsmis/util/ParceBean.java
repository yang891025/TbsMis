package com.tbs.tbsmis.util;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ParceBean implements Parcelable{
    private  Bitmap dw;
    private String name;
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getDw() {
        return this.dw;
    }

    public void setDw(Bitmap dw) {
        this.dw = dw;
    }

     public static final Creator<ParceBean> CREATOR = new Parcelable.Creator<ParceBean>() {
            @Override
			public ParceBean createFromParcel(Parcel source) { 
                ParceBean pb = new ParceBean(); 
                pb.name = source.readString(); 
                pb.dw = Bitmap.CREATOR.createFromParcel(source);
                return pb; 
            } 
            @Override
			public ParceBean[] newArray(int size) { 
                return new ParceBean[size]; 
            } 
        }; 
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name);
        this.dw.writeToParcel(parcel, 0);
    }
}