package com.rachitmishra.raptor

import android.graphics.Bitmap

interface Cache {

    fun get(key: String): Bitmap?
    fun put(key: String, bitmap: Bitmap)
}

class InMemoryCacheImpl : Cache {

    private val cache = hashMapOf<String, Bitmap>()

    override fun get(key: String): Bitmap? {
        return cache.getOrDefault(key, null)
    }

    override fun put(key: String, bitmap: Bitmap) {
        cache[key] = bitmap
    }
}
