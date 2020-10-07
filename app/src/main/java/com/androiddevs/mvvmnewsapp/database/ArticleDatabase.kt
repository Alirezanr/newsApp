package com.androiddevs.mvvmnewsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.model.Article

@Database(
        entities = [Article::class],
        exportSchema = true,
        version = 2
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase()
{
    abstract fun getArticleDao(): ArticleDao;

    companion object
    {
        //other threads can immediately notice when a thread changes this instance
        @Volatile
        private var instance: ArticleDatabase? = null

        //use this variable to set instance synchronize
        // and make sure there is only a single instance of database at once
        private val LOCK = Any()

        //this fun will call when we create an instance of this class
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK)
        {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_db.db")
                    .fallbackToDestructiveMigration()
                    .build()
    }
}