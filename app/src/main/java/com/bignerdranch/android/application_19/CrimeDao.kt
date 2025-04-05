package com.bignerdranch.android.application_19

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
interface CrimeDao {
    @Insert
    fun insert(crime: Crime)

    @Update
    fun update(crime: Crime)

    @Query("SELECT * FROM Crime")
    fun getAllCrimes(): List<Crime>

    @Query("SELECT * FROM Crime WHERE id = :id")
    fun getCrimeById(id: UUID): Crime?
}
