package com.example.data.mapper

import com.example.data.MonthTargetEntity
import com.example.domain.model.MonthTarget

fun MonthTargetEntity.toDomain(): MonthTarget = MonthTarget(
    year = year,
    month = month,
    dailyRequiredMinutes = dailyRequiredMinutes
)

fun MonthTarget.toEntity(): MonthTargetEntity = MonthTargetEntity(
    year = year,
    month = month,
    dailyRequiredMinutes = dailyRequiredMinutes
)
