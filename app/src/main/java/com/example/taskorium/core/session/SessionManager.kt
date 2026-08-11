package com.example.taskorium.core.session

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    @Volatile
    var token: String? = null

    @Volatile
    var refreshToken: String? = null
}