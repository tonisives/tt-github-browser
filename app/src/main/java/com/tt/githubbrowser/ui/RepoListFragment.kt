package com.tt.githubbrowser.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.tt.githubbrowser.R
import com.tt.githubbrowser.databinding.RepoListFragmentBinding
import com.tt.githubbrowser.repository.Status
import com.tt.githubbrowser.ui.common.CarListAdapter
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepoListFragment : Fragment() {
    private val viewModel: RepoListViewModel by viewModel()

    private lateinit var binding: RepoListFragmentBinding
    private lateinit var adapter: CarListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<RepoListFragmentBinding>(
            inflater,
            R.layout.repo_list_fragment,
            container,
            false
        )

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = CarListAdapter(object : DataBindingComponent {}, get()) { repo ->
            println("click ${repo.name}")
        }

        this.adapter = adapter
        binding.listView.adapter = adapter
        postponeEnterTransition()
        binding.listView.doOnPreDraw {
            startPostponedEnterTransition()
        }

        viewModel.repos.observe(viewLifecycleOwner) {
            // update UI
            it.status.showWhenLoading(binding.listProgressBar)

            when (it.status) {
                Status.SUCCESS -> {
                    adapter.submitList(it.data)
                }
                Status.ERROR -> {
                    Toast.makeText(this@RepoListFragment.activity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
