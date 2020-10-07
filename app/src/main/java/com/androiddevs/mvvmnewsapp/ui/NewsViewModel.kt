package com.androiddevs.mvvmnewsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.model.Article
import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.reepository.NewsRepository
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel()
{
    //Breaking news list
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    //Handles pagination (page number)
    var breakingNewsPage = 1

    var breakingNewsResponse: NewsResponse? = null

    //Search news list
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    //Handles pagination (page number)
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init
    {
        getBreakingNews("us")
    }

    //viewModelScope makes sures that Coroutine stays alive as long as viewModel is alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        //set state of network request
        breakingNews.postValue(Resource.Loading())

        //save network response in this object
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)

        breakingNews.postValue(handleBreakingNewsResponse(response))
    }


    fun searchNews(searchQuery: String) = viewModelScope.launch {
        if (searchQuery != "")
        {
            searchNews.postValue(Resource.Loading())
            //save network response in this object
            val response = newsRepository.searchNews(searchQuery)
            searchNews.value = handleSearchNewsResponse(response)
        }
        else{
            searchNewsPage=1
        }
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>
    {
        if (response.isSuccessful)
        {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null)
                {
                    breakingNewsResponse = resultResponse
                }
                else
                {
                    var oldArticles = breakingNewsResponse?.articles
                    var newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>
    {
        searchNews.value=null
        if (response.isSuccessful)
        {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null)
                {
                    searchNewsResponse = resultResponse
                }
                else
                {
                    var oldArticles = searchNewsResponse?.articles
                    var newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    //database operations section:

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun removeArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedArticles() = newsRepository.getArticle()

}
