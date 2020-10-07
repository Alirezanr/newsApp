package com.androiddevs.mvvmnewsapp.reepository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.database.ArticleDatabase
import com.androiddevs.mvvmnewsapp.model.Article

class NewsRepository(val db: ArticleDatabase)
{
    //call getBreakingNews from NewsApi and get the response
    suspend fun getBreakingNews(country: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews( country,pageNumber)

    suspend fun searchNews(searchQuery:String)=
        RetrofitInstance.api.searchForNews(searchQuery)
    /**insert an article to database or update the existing article*/
    suspend fun upsertArticle(article:Article)=db.getArticleDao().upsertArticle(article)
    /**delete an article from database*/
    suspend fun deleteArticle(article: Article)=db.getArticleDao().deleteArticle(article)
    /**get all articles from database in a list of Articles as LiveData*/
    fun getArticle()=db.getArticleDao().getAllArticles()
}