package ilgulee.com.javanewsapirxjava.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "us_headlines")
data class Article(
        @SerializedName("author")
        val author: String?,
        @SerializedName("content")
        val content: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("publishedAt")
        val publishedAt: String?,
        @SerializedName("source")
        @Embedded(prefix = "source_")
        val source: Source?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("url")
        val url: String?,
        @SerializedName("urlToImage")
        val urlToImage: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}
