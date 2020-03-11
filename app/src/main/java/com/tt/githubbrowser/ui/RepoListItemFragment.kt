package com.tt.githubbrowser.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.tt.githubbrowser.R
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class RepoListItemFragment : Fragment() {
    private val viewModel: RepoListItemViewModel by stateViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repo_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repo.observe(viewLifecycleOwner) {
            // update UI
        }
    }
}
