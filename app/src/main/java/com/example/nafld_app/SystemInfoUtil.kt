package com.example.nafld_app

import android.os.Build
import android.util.Log

object SystemInfoUtil {
    fun logSystemInfo() {
        // Получаем информацию о модели устройства
        val deviceModel = Build.MODEL
        // Получаем производителя устройства
        val manufacturer = Build.MANUFACTURER
        // Получаем версию операционной системы
        val osVersion = Build.VERSION.RELEASE
        // Получаем архитектуру процессора
        val cpuAbi = Build.SUPPORTED_ABIS[0] // Основная архитектура

        Log.d("SystemInfo", "Model: $deviceModel")
        Log.d("SystemInfo", "Manufacturer: $manufacturer")
        Log.d("SystemInfo", "OS Version: $osVersion")
        Log.d("SystemInfo", "CPU Architecture: $cpuAbi")
    }
}
