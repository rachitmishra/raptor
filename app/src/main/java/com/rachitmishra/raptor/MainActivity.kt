package com.rachitmishra.raptor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rachitmishra.raptor.databinding.ActivityMainBinding
import com.rachitmishra.raptor.databinding.ItemBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val imageLoader: ImageLoader by lazy { SimpleImageLoaderImpl(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        fun reload() {
//            imageLoader.loadImage(
//                SimpleImageLoaderImpl.ImageRequest(url = "https://source.unsplash.com/random/800x800/?img=1"),
//                binding.image
//            )
//        }
//
//        binding.reload.setOnClickListener {
//            reload()
//        }
//
//        reload()

        binding.recyclerView.adapter = ImageAdapter()
    }

}
