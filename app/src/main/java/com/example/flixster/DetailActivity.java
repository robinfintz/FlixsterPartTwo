package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyDWvOBG9fS_XLC9IdpdfKcfcRd9XmW0uzc";
    private static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    TextView myTitle;
    TextView myOverview;
    RatingBar ratingBar;
    YouTubePlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        myTitle = findViewById(R.id.myTitle);
        myOverview = findViewById(R.id.myOverview);
        ratingBar = findViewById(R.id.ratingBar);
        player = findViewById(R.id.player);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        myTitle.setText(movie.getTitle());
        myOverview.setText(movie.getOverview());
        ratingBar.setRating((float)movie.getRating());
        boolean isPopular = movie.getRating() > 5;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        try {
                            JSONArray results = json.jsonObject.getJSONArray("results");
                            if(results.length() == 0) {
                                return;
                            }
                            String youtubeKey = results.getJSONObject(0).getString("key");
                            Log.d("Detail Activity", youtubeKey);
                            initializeYoutube(youtubeKey, isPopular);

                        } catch (JSONException e) {
                            Log.e("DetailActivity", "Failed to parse JSON", e);
                        }
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {

                    }
                });
    }

    private void initializeYoutube(final String youtubeKey, boolean isPopular) {
        player.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "OnInitializationSuccess");
                if(isPopular)
                    youTubePlayer.loadVideo(youtubeKey);
                else
                    youTubePlayer.cueVideo(youtubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "OnInitializationFailure");
            }
        });
    }
}