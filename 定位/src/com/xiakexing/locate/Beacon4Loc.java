package com.xiakexing.locate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon4Loc implements Parcelable {
	double x;
	double y;
	double distance;
	double sigma;
	
	class Stu{
		List list;
	}
	
	ArrayList<Stu> list = new ArrayList<Stu>();
	
	public Beacon4Loc(double x, double y, double sigma, double distance) {
		this.x = x;
		this.y = y;
		this.sigma = sigma;
		this.distance = distance;
	}

	public final Parcelable.Creator<Beacon4Loc> CREATOR = new Creator<Beacon4Loc>() {

		@Override
		public Beacon4Loc createFromParcel(Parcel source) {
			double x = source.readDouble();
			double y = source.readDouble();
			double sigma = source.readDouble();
			double distance = source.readDouble();
			Beacon4Loc beacon = new Beacon4Loc(x, y, sigma, distance);
			return beacon;
		}

		@Override
		public Beacon4Loc[] newArray(int size) {
			return new Beacon4Loc[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(sigma);
		dest.writeDouble(distance);
	}
}