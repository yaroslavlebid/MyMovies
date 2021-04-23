package course.apps.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import course.apps.mymovies.adapters.ReviewAdapter;
import course.apps.mymovies.adapters.TrailerAdapter;
import course.apps.mymovies.data.FavouriteMovie;
import course.apps.mymovies.data.MainViewModel;
import course.apps.mymovies.data.Movie;
import course.apps.mymovies.data.Review;
import course.apps.mymovies.data.Trailer;
import course.apps.mymovies.utils.JSONUtils;
import course.apps.mymovies.utils.NetworkUtils;

public class ActivityDetail extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle, textViewOriginalTitle, textViewRating, textReleaseDate,
            textViewOverview;
    RecyclerView recyclerViewTrailers, recyclerViewReviews;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    ScrollView scrollViewInfo;
    private int id;

    private  MainViewModel viewModel;
    private Movie movie;

    private ImageView imageViewAddToFavourite;

    private FavouriteMovie favouriteMovie;
    private static String lang;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourites:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        lang = Locale.getDefault().getLanguage();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("id"))
        {
            id = intent.getIntExtra("id", -1);
        }
        else
        {
            finish();
        }

        movie = viewModel.getMovieById(id);
        Log.i("MyLog", "Activity with movie " + movie.getTitle() + "was started!");

        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.placeholder_vertical).into(imageViewBigPoster);
        Log.i("MyLog", "Poster url: " + movie.getBigPosterPath());



        textViewTitle.setText(movie.getTitle());
        textViewOverview.setText(movie.getOverview());
        textReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        setFavourite();

        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);


        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);

        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        //Log.i("MyLog", "Trailers: " + jsonObjectTrailers.toString());
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);

        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);

        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);

        scrollViewInfo.smoothScrollTo(0,0);
    }

    public void onClickChangeFavourite(View view) {

        if (favouriteMovie == null)
        {

            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else
        {

            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite()
    {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null)
        {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        }
        else
        {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
}