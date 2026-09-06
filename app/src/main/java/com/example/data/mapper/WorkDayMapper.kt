package com.example.data.mapper

import com.example.data.WorkDayEntity
import com.example.domain.model.WorkDay

fun WorkDayEntity.toDomain(): WorkDay = WorkDay(
    year = year,
    month = month,
    dayNumber = dayNumber,
    enterHour = enterHour,
    enterMinute = enterMinute,
    exitHour = exitHour,
    exitMinute = exitMinute,
    isDayOff = isDayOff,
    note = note
)

fun WorkDay.toEntity(): WorkDayEntity = WorkDayEntity(
    year = year,
    month = month,
    dayNumber = dayNumber,
    enterHour = enterHour,
    enterMinute = enterMinute,
    exitHour = exitHour,
    exitMinute = exitMinute,
    isDayOff = isDayOff,
    note = note
)
