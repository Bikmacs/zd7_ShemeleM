package com.bignerdranch.android.application_19

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate
import java.util.UUID

@Entity
@TypeConverters(CrimeTypeConverters::class)
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String,
    var isSolved: Boolean,
    var date: LocalDate,
    var suspect: String = ""
)
