package com.tt.githubbrowser.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil.inflate
import androidx.recyclerview.widget.DiffUtil
import com.tt.githubbrowser.R
import com.tt.githubbrowser.databinding.RepoListItemFragmentBinding

import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.util.AppExecutors

class CarListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Repo) -> Unit)?
) : DataBoundListAdapter<Repo, RepoListItemFragmentBinding>(
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
    override fun createBinding(parent: ViewGroup): RepoListItemFragmentBinding {
        val binding = inflate<RepoListItemFragmentBinding>(
                LayoutInflater.from(parent.context),
                R.layout.repo_list_item_fragment,
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

    override fun bind(binding: RepoListItemFragmentBinding, item: Repo) {
        binding.repo = item
    }
}
