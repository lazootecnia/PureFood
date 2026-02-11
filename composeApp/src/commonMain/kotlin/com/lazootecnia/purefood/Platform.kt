package com.lazootecnia.purefood

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform