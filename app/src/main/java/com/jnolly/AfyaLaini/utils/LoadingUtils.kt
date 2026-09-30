package com.jnolly.AfyaLaini.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.jnolly.AfyaLaini.R

object LoadingUtils {
    
    fun showLoading(view: View) {
        view.visibility = View.VISIBLE
    }
    
    fun hideLoading(view: View) {
        view.visibility = View.GONE
    }
    
    fun showContent(loadingView: View, contentView: View) {
        loadingView.visibility = View.GONE
        contentView.visibility = View.VISIBLE
    }
    
    fun showLoadingContent(loadingView: View, contentView: View) {
        loadingView.visibility = View.VISIBLE
        contentView.visibility = View.GONE
    }
    
    fun addSkeletonToRecyclerView(recyclerView: RecyclerView, itemLayoutRes: Int, count: Int = 6): SkeletonAdapter {
        val adapter = SkeletonAdapter(itemLayoutRes, count)
        recyclerView.adapter = adapter
        return adapter
    }
    
    fun removeSkeletonFromRecyclerView(recyclerView: RecyclerView, realAdapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = realAdapter
    }
    
    class SkeletonAdapter(
        private val itemLayoutRes: Int,
        private val itemCount: Int
    ) : RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder>() {
        
        class SkeletonViewHolder(view: View) : RecyclerView.ViewHolder(view)
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
            replaceWithSkeleton(view)
            return SkeletonViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
            // Start skeleton animation - just a placeholder
        }
        
        override fun getItemCount(): Int = itemCount
        
        private fun replaceWithSkeleton(view: View) {
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    if (child is ViewGroup) {
                        replaceWithSkeleton(child)
                    } else if (!(child is ProgressBar) && !(child is FrameLayout)) {
                        val params = child.layoutParams
                        val skeleton = FrameLayout(view.context).apply {
                            layoutParams = params
                            setBackgroundResource(R.drawable.bg_skeleton)
                        }
                        val index = view.indexOfChild(child)
                        view.removeViewAt(index)
                        view.addView(skeleton, index)
                    }
                }
            }
        }
    }
}