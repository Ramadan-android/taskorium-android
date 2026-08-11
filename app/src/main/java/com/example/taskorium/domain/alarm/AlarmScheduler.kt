package com.example.taskorium.domain.alarm

import com.example.taskorium.domain.model.Task

interface AlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}