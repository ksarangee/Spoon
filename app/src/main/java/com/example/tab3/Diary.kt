package com.example.tab3

import android.net.Uri
import androidx.room.*

@Entity(tableName = "tb_diary", primaryKeys = ["year", "month", "date"])

data class Diary(
    var year: Int,
    var month: Int,
    var date: Int,
    var diaryImg: Uri?,
    var diaryContent: String?
)