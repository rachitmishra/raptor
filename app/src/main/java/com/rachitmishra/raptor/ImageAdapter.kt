package com.rachitmishra.raptor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.rachitmishra.raptor.databinding.ItemBinding

fun ImageView.load(url: String) {
    val il = (context as MainActivity).imageLoader
    il.loadImage(SimpleImageLoaderImpl.ImageRequest(url = url), this)
}

class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val data = (1..20).map {
        "https://source.unsplash.com/random/800x800/?img=1&id=$it"
    }

    class ImageViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            binding.image.load(url)
            binding.url.text = url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(data[position])
    }
}
