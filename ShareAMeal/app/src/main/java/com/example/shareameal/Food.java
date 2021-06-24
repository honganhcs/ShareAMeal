package com.example.shareameal;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
  private String name;
  private String description;
  private int quantity;
  private String imageUrl;
  private String foodId;

  public Food() {}

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setFoodId(String foodId) {
    this.foodId = foodId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getFoodId() {
    return foodId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(description);
    dest.writeInt(quantity);
    dest.writeString(imageUrl);
    dest.writeString(foodId);
  }

  public Food(Parcel source) {
    this.name = source.readString();
    this.description = source.readString();
    this.quantity = source.readInt();
    this.imageUrl = source.readString();
    this.foodId = source.readString();
  }

  public static final Parcelable.Creator<Food> CREATOR =
      new Parcelable.Creator<Food>() {
        public Food createFromParcel(Parcel in) {
          return new Food(in);
        }

        public Food[] newArray(int size) {
          return new Food[size];
        }
      };
}
