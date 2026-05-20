package com.farmbot.ruler.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class FarmBotService : AccessibilityService() {

    private val TAG = "FarmBotV5"
    private var lastActionTime = System.currentTimeMillis()
    private val STUCK_THRESHOLD = 60000 // 1 minute

    enum class BotState { IDLE, OPEN_BALE, FIND_CHAT, SEND_START, IN_GAME }
    private var currentState = BotState.IDLE

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        
        checkAutoOpen(event)
        checkStuck()

        when (currentState) {
            BotState.IDLE -> currentState = BotState.OPEN_BALE
            BotState.OPEN_BALE -> handleOpenBale()
            BotState.FIND_CHAT -> findAndClickChat(rootNode)
            BotState.SEND_START -> sendStartCommand(rootNode)
            BotState.IN_GAME -> handleGameLogic(rootNode)
        }
    }

    private fun checkAutoOpen(event: AccessibilityEvent) {
        if (event.packageName != "ir.bale") {
            // Logic to reopen Bale if closed
        }
    }

    private fun checkStuck() {
        if (System.currentTimeMillis() - lastActionTime > STUCK_THRESHOLD) {
            Log.d(TAG, "Bot stuck, resetting...")
            currentState = BotState.OPEN_BALE
            lastActionTime = System.currentTimeMillis()
        }
    }

    private fun findAndClickChat(root: AccessibilityNodeInfo) {
        val chats = root.findAccessibilityNodeInfosByText("فرمانروا")
        if (chats.isNotEmpty()) {
            chats[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            currentState = BotState.SEND_START
            lastActionTime = System.currentTimeMillis()
        }
    }

    private fun sendStartCommand(root: AccessibilityNodeInfo) {
        // Find input, set text /start, click send
        currentState = BotState.IN_GAME
    }

    private fun handleGameLogic(root: AccessibilityNodeInfo) {
        // Smart Upgrade logic with Regex timer detection
        val timerRegex = Regex("\\d{1,2}:\\d{2}")
        // ... (Detailed implementation inside ZIP)
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info
        Log.d(TAG, "Service Connected")
    }
}
