package com.androiddevs.mvvmnewsapp.utils

//Handles network response.
//sealed class is like abstract class that we can define witch class can inherit from this class.
//T is network respons and in this project its NewsResponse
sealed class Resource<T>(
    //body of response:
    val data: T? = null,
    //message of response:
    val message: String? = null)
{
    //class's that can extend from this class:
    class Success<T>(data:T) : Resource<T>(data)
    class Error<T>(message: String,data: T?=null): Resource<T>(data,message)
    class Loading<T>:Resource<T>()
}