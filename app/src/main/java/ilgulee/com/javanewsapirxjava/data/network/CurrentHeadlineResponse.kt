package ilgulee.com.javanewsapirxjava.data.network

import com.google.gson.annotations.SerializedName
import ilgulee.com.javanewsapirxjava.data.db.entity.Article

data class CurrentHeadlineResponse(
        @SerializedName("articles")
        val articles: List<Article>)