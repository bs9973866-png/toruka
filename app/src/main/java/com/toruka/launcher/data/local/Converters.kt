package com.toruka.launcher.data.local

import androidx.room.TypeConverter
import com.toruka.launcher.data.model.AccountType
import com.toruka.launcher.data.model.RendererType

class Converters {
    
    @TypeConverter
    fun fromRendererType(type: RendererType): String = type.name
    
    @TypeConverter
    fun toRendererType(name: String): RendererType = try {
        RendererType.valueOf(name)
    } catch (e: IllegalArgumentException) {
        RendererType.GL4ES
    }
    
    @TypeConverter
    fun fromAccountType(type: AccountType): String = type.name
    
    @TypeConverter
    fun toAccountType(name: String): AccountType = try {
        AccountType.valueOf(name)
    } catch (e: IllegalArgumentException) {
        AccountType.OFFLINE
    }
}
