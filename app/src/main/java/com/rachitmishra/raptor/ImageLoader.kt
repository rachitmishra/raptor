package com.rachitmishra.raptor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.Executors

interface ImageLoader {

    fun loadImage(request: SimpleImageLoaderImpl.ImageRequest, imageView: ImageView)
}

class SimpleImageLoaderImpl(lifecycleOwner: LifecycleOwner) : ImageLoader, LifecycleEventObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val caches = arrayListOf<Cache>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val dispatcher = Executors.newFixedThreadPool(5)

    init {
        caches.add(InMemoryCacheImpl())
    }

    private lateinit var _imageView: WeakReference<ImageView>

    override fun loadImage(request: ImageRequest, imageView: ImageView) {
        imageView.tag = request.url
        _imageView = WeakReference<ImageView>(imageView)
        val localRequest = request.copy(onLoaded = { result ->
            val (url, bitmap) = result
            val nonNullImageView = _imageView.get()
            if (nonNullImageView != null) {
                mainHandler.post {
                    if (nonNullImageView.tag == url) {
                        nonNullImageView.setImageBitmap(bitmap)
                    }
                }
            }
            request.onLoaded?.invoke(result)
        })
        loadImage(localRequest)
    }

    data class ImageLoadResult(val key: String, val bitmap: Bitmap? = null, val isFromCache: Boolean = false)

    private fun checkInCache(url: String): Bitmap? {
        for (cache in caches) {
            val bitmap = cache.get(url)
            if (bitmap != null) {
                return bitmap
            }
        }

        return null
    }

    private fun loadImage(request: ImageRequest) {
        val (url, onLoaded, onError) = request
        val result = ImageLoadResult(url)
        val fromCache = checkInCache(url)
        if (fromCache != null) {
            request.onLoaded?.invoke(result.copy(bitmap = fromCache, isFromCache = true))
            return
        }

        try {
            dispatcher.execute {
                val connection = URL(url).openConnection()
                val networkResult = ImageLoadResult(url)
                val fromNetwork = BitmapFactory.decodeStream(connection.getInputStream())
                onLoaded?.invoke(networkResult.copy(bitmap = fromNetwork, isFromCache = false))
            }
        } catch (e: IOException) {
            onError?.invoke(e)
        } finally {
        }
    }

    data class ImageRequest(
        val url: String, val onLoaded: ((ImageLoadResult) -> Unit)? = null, val onError: ((Exception) -> Unit)? = null
    )

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            dispatcher.shutdownNow()
        }
    }
}
