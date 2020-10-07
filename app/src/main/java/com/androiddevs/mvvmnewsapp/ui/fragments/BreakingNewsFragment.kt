package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.utils.Constants
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news)
{
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val PROGRESSBAR_VISIBILE = 1
    private val PROGRESSBAR_INVISIBILE = 2

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener()
    {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
        {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
        {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = (isNotLoadingAndNotLastPage &&
                                  isNotAtBeginning &&
                                  isTotalMoreThanVisible &&
                                  isScrolling &&
                                  isAtLastItem)


            if (shouldPaginate)
            {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            // TODO:Error chance
            findNavController().navigate(
                    BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(it)
            )

            /* if get error use this instead
            findNavController().navigate(
                     R.id.action_breakingNewsFragment_to_articleFragment,
                     bundle
             )*/
        }
        viewModel.breakingNews.observe(this.viewLifecycleOwner, Observer { response ->
            when (response)
            {
                is Resource.Success ->
                {
                    setProgressbarVisibility(PROGRESSBAR_INVISIBILE)
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage)
                            rvBreakingNews.setPadding(0, 0, 0, 0)


                    }
                }
                is Resource.Error   ->
                {
                    setProgressbarVisibility(PROGRESSBAR_INVISIBILE)
                    response.message?.let { message ->
                        Log.i(Constants.TAG, "An error found in $message ")

                    }
                }
                is Resource.Loading ->
                {
                    setProgressbarVisibility(PROGRESSBAR_VISIBILE)
                }
            }

        })


    }

    private fun setProgressbarVisibility(state: Int)
    {
        when (state)
        {
            PROGRESSBAR_INVISIBILE ->
            {
                paginationProgressBar.visibility = View.INVISIBLE
                isLoading = false
            }
            PROGRESSBAR_VISIBILE ->
            {
                paginationProgressBar.visibility = View.VISIBLE
                isLoading = true
            }
        }
    }

    private fun setupRecyclerView()
    {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }


}