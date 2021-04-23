package course.apps.mymovies.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Movie {
    public FavouriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String backdropPath, double voteAverage, String releaseDate, String bigPosterPath) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, posterPath, backdropPath, voteAverage, releaseDate, bigPosterPath);
    }

    @Ignore
    public FavouriteMovie(Movie movie) {
        super(movie.getUniqueId(), movie.getId(),movie.getVoteCount(),movie.getTitle(),movie.getOriginalTitle(),movie.getOverview(),
                movie.getPosterPath(),movie.getBackdropPath(),movie.getVoteAverage(),movie.getReleaseDate(),
                movie.getBigPosterPath());
    }
}
