package com.example.data.mapper

import com.example.data.AppSettingsEntity
import com.example.domain.model.AppSettings

fun AppSettingsEntity.toDomain(): AppSettings = AppSettings(
    id = id,
    dailyRequiredMinutes = dailyRequiredMinutes,
    offDaysOfWeek = offDaysOfWeek,
    themeMode = themeMode,
    calendarType = calendarType,
    userName = userName,
    avatarId = avatarId,
    minEnterMinutes = minEnterMinutes,
    maxExitMinutes = maxExitMinutes,
    minDailyMinutes = minDailyMinutes,
    maxDailyMinutes = maxDailyMinutes,
    hasCompletedOnboarding = hasCompletedOnboarding
)

fun AppSettings.toEntity(): AppSettingsEntity = AppSettingsEntity(
    id = id,
    dailyRequiredMinutes = dailyRequiredMinutes,
    offDaysOfWeek = offDaysOfWeek,
    themeMode = themeMode,
    calendarType = calendarType,
    userName = userName,
    avatarId = avatarId,
    minEnterMinutes = minEnterMinutes,
    maxExitMinutes = maxExitMinutes,
    minDailyMinutes = minDailyMinutes,
    maxDailyMinutes = maxDailyMinutes,
    hasCompletedOnboarding = hasCompletedOnboarding
)
