package com.xiakexing.locate;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Person implements Parcelable {
	private String name;
	private int age;

	private static final String TAG = "Person";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>() {
		@Override
		public Person createFromParcel(Parcel source) {
			Log.d(TAG, "createFromParcel");
			Person mPerson = new Person();
			mPerson.name = source.readString();
			mPerson.age = source.readInt();
			return mPerson;
		}

		@Override
		public Person[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Person[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		Log.d(TAG, "describeContents");
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Log.d(TAG, "writeToParcel");
		dest.writeString(name);
		dest.writeInt(age);
	}
}