package com.example.android.popularmoviesv1;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String title;
    private double userRating;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private static final String posterBaseUrl = "http://image.tmdb.org/t/p/w185";
    private int id;

    public Movie(String title, double userRating, String releaseDate, String synopsis, String posterPath, int id) {
        this.title = title;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.id = id;
        this.posterUrl = posterBaseUrl + posterPath;
    }

    public String getPosterUrl(){
        return posterUrl;
    }

   @Override
    public String toString(){
       return "Title: " + title + ", Rating: " + userRating + ", Date: " + releaseDate + ", Plot: " + synopsis + ", ID: " + id + ", JPG: " + posterUrl;
   }

    public String getTitle() {
        return title;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseYearAndMonth(){
        String[] parts = releaseDate.split("-");
        if(parts.length<2){
            if(releaseDate.equalsIgnoreCase("null")) {
                return "Not supplied";
            } else {
                return "Unknown";
            }
        }
        return parts[1].trim() + "/" + parts[0].trim();
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        dest.writeString(posterUrl);
        dest.writeInt(id);
    }

    public Movie(Parcel parcel) {
        this.title = parcel.readString();
        this.userRating = parcel.readDouble();
        this.releaseDate = parcel.readString();
        this.synopsis = parcel.readString();
        this.posterUrl = parcel.readString();
        this.id = parcel.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };
}
