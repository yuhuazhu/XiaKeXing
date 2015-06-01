package com.xiakexing.locate;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon4Loc implements Parcelable {
	private float x;
	private float y;
	private float z;
	private float distance;

	public Beacon4Loc(float x, float y, float z, float distance) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.distance = distance;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public final Parcelable.Creator<Beacon4Loc> CREATOR = new Creator<Beacon4Loc>() {

		@Override
		public Beacon4Loc createFromParcel(Parcel source) {
			float x = source.readFloat();
			float y = source.readFloat();
			float z = source.readFloat();
			float distance = source.readFloat();
			Beacon4Loc beacon = new Beacon4Loc(x, y, z, distance);
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
		dest.writeFloat(x);
		dest.writeFloat(y);
		dest.writeFloat(z);
		dest.writeFloat(distance);
	}
}