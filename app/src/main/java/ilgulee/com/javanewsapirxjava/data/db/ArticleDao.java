package ilgulee.com.javanewsapirxjava.data.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import ilgulee.com.javanewsapirxjava.data.db.entity.Article;
import io.reactivex.Flowable;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHeadline(Article article);

    @Query("SELECT * FROM us_headlines")
    Flowable<List<Article>> getHeadlines();

    @Query("DELETE FROM us_headlines")
    void deleteAll();
}
