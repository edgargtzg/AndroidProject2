package edgargtzg.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class which contains details of a movie.
 */
public class MovieEntry implements Parcelable {

    private final String ORIGINAL_TITLE = "original_title";
    private final String MOVIE_POSTER = "poster_path";
    private final String PLOT_SYNOPSIS = "overview";
    private final String USER_RATING = "vote_average";
    private final String RELEASE_DATE = "release_date";

    private String mOriginalTitle = "";
    private String mMoviePoster = "";
    private String mPlotSynopsis = "";
    private String mUserRating = "";
    private String mReleaseDate = "";

    public MovieEntry(JSONObject movieJsonObject) {
        parseMovieData(movieJsonObject);
    }

    protected MovieEntry(Parcel in) {
        mOriginalTitle = in.readString();
        mMoviePoster = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
    }

    public static final Creator<MovieEntry> CREATOR = new Creator<MovieEntry>() {
        @Override
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        @Override
        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };

    private void parseMovieData(JSONObject movieJsonObject) {
        try {
            mOriginalTitle = (String)movieJsonObject.get(ORIGINAL_TITLE);
            mMoviePoster = (String)movieJsonObject.get(MOVIE_POSTER);
            mPlotSynopsis = (String)movieJsonObject.get(PLOT_SYNOPSIS);
            mUserRating = movieJsonObject.get(USER_RATING).toString();
            mReleaseDate = (String)movieJsonObject.get(RELEASE_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mMoviePoster);
        dest.writeString(mPlotSynopsis);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
    }


}
