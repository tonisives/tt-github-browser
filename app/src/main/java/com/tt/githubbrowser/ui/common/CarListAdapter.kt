package com.tt.githubbrowser.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.tt.githubbrowser.R
import com.tt.githubbrowser.databinding.FragmentRepoListItemBinding

import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.util.AppExecutors

class CarListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Repo) -> Unit)?
) : DataBoundListAdapter<Repo, FragmentRepoListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
            return oldItem.name == newItem.name
        }
    }
) {
    override fun createBinding(parent: ViewGroup): FragmentRepoListItemBinding {
        val binding = DataBindingUtil
            .inflate<FragmentRepoListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.fragment_repo_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.setOnClickListener {
            binding.repo?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: FragmentRepoListItemBinding, item: Repo) {
        binding.repo = item
    }
}
