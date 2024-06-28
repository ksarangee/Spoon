package com.example.tab3

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tab3.R
import com.example.tab3.databinding.FragmentDashboardBinding
import com.example.tab3.ui.dashboard.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private val imageLoadLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
        updateImages(uriList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadImageButton.setOnClickListener {
            checkPermission()
        }
/*
        binding.toFrameActivityButton.setOnClickListener {
            toFrameActivity()
        }
 */
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_add -> {
                checkPermission()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun initRecyclerView() {
        imageAdapter = ImageAdapter(object : ImageAdapter.ItemClickListener {
            override fun onLoadMoreClick() {
                checkPermission()
            }
        })

        binding.imageRecyclerView.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadImage()
            }

            shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                showPermissionInfoDialog()
            }

            else -> {
                requestReadMediaImages()
            }
        }
    }

    private fun loadImage() {
        imageLoadLauncher.launch("image/*")
    }

    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 가져오기 위해 외부 저장소 읽기 권한이 필요합니다.")
            setNegativeButton("Cancel", null)
            setPositiveButton("OK") { _, _ ->
                requestReadMediaImages()
            }
        }.show()
    }

    private fun requestReadMediaImages() {
        requestPermissions(
            arrayOf(READ_EXTERNAL_STORAGE),
            REQUEST_READ_MEDIA_IMAGES
        )
    }

    private fun updateImages(uriList: List<Uri>) {
        Log.d("updateImages", "$uriList")
        val images = uriList.map { ImageItems.Image(it) }
        val updatedImages = imageAdapter.currentList.toMutableList().apply { addAll(images) }
        imageAdapter.submitList(updatedImages)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_READ_MEDIA_IMAGES -> {
                val resultCode = grantResults.firstOrNull() ?: PackageManager.PERMISSION_DENIED
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    loadImage()
                }
            }
        }
    }

    private fun toFrameActivity() {
        val images = imageAdapter.currentList.filterIsInstance<ImageItems.Image>().map { it.uri.toString() }.toTypedArray()
        val intent = Intent(requireContext(), FrameActivity::class.java)
            .putExtra("images", images)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_READ_MEDIA_IMAGES = 100
    }
}
