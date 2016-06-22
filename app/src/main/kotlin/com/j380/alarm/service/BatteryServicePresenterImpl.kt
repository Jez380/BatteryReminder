package com.j380.alarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.j380.alarm.R
import com.j380.alarm.interactor.BatteryInteractor
import com.j380.alarm.presenter.AlertViewPresenter

class BatteryServicePresenterImpl(val alertViewPresenter: AlertViewPresenter,
        val batteryInteractor: BatteryInteractor, val context: Context,
        val alarmManager: AlarmManager): BatteryServicePresenter {

    private val LOW_BATTERY_LEVEL = 25f

    private lateinit var batteryStatus: Intent

    override fun onBind(): IBinder? {
        return null
    }

    override fun onCreate() {
        setAlarm()
        alertViewPresenter.initView()
    }

    override fun onStartCommand() {
        checkBattery()
    }

    override fun onDestroy() {
        alertViewPresenter.hideAlert()
    }

    private fun setAlarm() {
        val lIntent = Intent(context.getString(R.string.checkBatteryIntentAction))
        val lPendingIntent = PendingIntent.getBroadcast(context, 0, lIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, lPendingIntent)
    }

    private fun checkBattery() {
        batteryStatus = getBatteryStatusIntent()
        batteryInteractor.setBatteryStatus(batteryStatus)

        if (batteryInteractor.isNotPluggedIn()) {
            showViewIfBatteryLevelIsLow(batteryInteractor.getBatteryLevel())
        }
    }

    private fun getBatteryStatusIntent(): Intent {
        val lFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return context.registerReceiver(null, lFilter)
    }

    private fun showViewIfBatteryLevelIsLow(batteryLevel: Int) {
        if (batteryLevel <= LOW_BATTERY_LEVEL) {
            alertViewPresenter.showLowBatteryAlert(batteryLevel)
        }
    }
}