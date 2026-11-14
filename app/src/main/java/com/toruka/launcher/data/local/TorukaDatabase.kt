package com.toruka.launcher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.toruka.launcher.data.model.Account
import com.toruka.launcher.data.model.Profile

@Database(
    entities = [Profile::class, Account::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TorukaDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: TorukaDatabase? = null

        fun getDatabase(context: Context): TorukaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TorukaDatabase::class.java,
                    "toruka_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
