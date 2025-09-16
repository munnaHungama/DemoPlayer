package com.munna.demoplayer.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.ui.PlayerView
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.munna.demoplayer.databinding.FragmentShortsBinding
import com.munna.demoplayer.playermanager.PlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShortsFragment : Fragment() {
    private var _binding: FragmentShortsBinding? = null
    private val binding get() = _binding!!
    private val vm: ShortsViewModel by viewModels()
    private var updateUiPIP : UpdateUiForPIP? = null
    private lateinit var adapter: ShortsPagingAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val snapHelper = PagerSnapHelper()

    private var currentPos = RecyclerView.NO_POSITION
    private var currentPlayerView: PlayerView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        updateUiPIP = context as UpdateUiForPIP
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShortsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ShortsPagingAdapter(requireContext(), viewLifecycleOwner)
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        snapHelper.attachToRecyclerView(binding.recyclerView)

        // Play the snapped item when scrolling settles
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(layoutManager) ?: return
                    val pos = layoutManager.getPosition(snapView)
                    Log.e("ShortsFragment", "scroll to ${RecyclerView.NO_POSITION} $pos $currentPos")
                    if (pos != RecyclerView.NO_POSITION && pos != currentPos) {
                        startPlaybackAt(pos)
                    }
                }
            }
        })

        val listener = object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val vh = binding.recyclerView.getChildViewHolder(view)
                if (vh is ShortsPagingAdapter.VideoVH && vh.bindingAdapterPosition == 0) {
                    binding.recyclerView.removeOnChildAttachStateChangeListener(this)
                    startPlaybackAt(0)
                }
            }
            override fun onChildViewDetachedFromWindow(view: View) = Unit
        }
        binding.recyclerView.addOnChildAttachStateChangeListener(listener)

        // Submit data
        viewLifecycleOwner.lifecycleScope.launch {
            vm.videos.collectLatest { data ->
                adapter.submitData(data)
            }
        }
    }

    private fun startPlaybackAt(position: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(position) as? ShortsPagingAdapter.VideoVH ?: return
        val item = adapter.itemAt(position) ?: return
        val url = item.url ?: return
        val player = PlayerManager.getPlayer()
        PlayerView.switchTargetView(player, currentPlayerView, holder.binding.playerView)
        currentPlayerView = holder.binding.playerView
        player.setMediaSource(PlayerManager.buildMediaSource(url))
        player.prepare()
        player.playWhenReady = true

        currentPos = position
    }
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        updateUiPIP?.updateUiForPIP(isInPictureInPictureMode)
        val snapView = snapHelper.findSnapView(layoutManager) ?: return
        val pos = layoutManager.getPosition(snapView)
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos) as? ShortsPagingAdapter.VideoVH ?: return

        if (isInPictureInPictureMode)
            holder.binding.bottomView.visibility = View.GONE
        else
            holder.binding.bottomView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (PlayerManager.getPlayer() != null)
        PlayerManager.getPlayer().play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Pause when leaving the screen
        try {
            PlayerManager.getPlayer().playWhenReady = false
        } catch (_: Exception) { }
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden)
            PlayerManager.getPlayer().pause()
        else
            PlayerManager.getPlayer().play()
    }

    interface UpdateUiForPIP{
        fun updateUiForPIP(isPIP: Boolean)
    }

}