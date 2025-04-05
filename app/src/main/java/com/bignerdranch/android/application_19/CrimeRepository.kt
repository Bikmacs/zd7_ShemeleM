package com.bignerdranch.android.application_19

import android.content.Context
import androidx.room.Room
import java.util.UUID

class CrimeRepository(context: Context) {
    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        "crime_database"
    ).build()

    private val crimeDao = database.crimeDao()

    fun getAllCrimes() = crimeDao.getAllCrimes()

    fun getCrimeById(id: UUID) = crimeDao.getCrimeById(id)

    fun insertCrime(crime: Crime) = crimeDao.insert(crime)

    fun updateCrime(crime: Crime) = crimeDao.update(crime)
}
