package com.munna.demoplayer.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.munna.demoplayer.databinding.FragmentDownloadsBinding
import com.munna.demoplayer.download.DownloadsViewModel
import com.munna.demoplayer.playermanager.PlayerManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadsFragment : Fragment() {
    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!
    private val vm: DownloadsViewModel by viewModels()

    private lateinit var adapter: DownloadsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PlayerManager.getPlayer().pause()
        adapter = DownloadsListAdapter(requireContext()) { entity ->
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(entity.filePath), "video/*")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }
        binding.downloadsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.downloadsRecycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            vm.downloads.collectLatest { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e("fsdafsafsa", "$hidden")
    }


}