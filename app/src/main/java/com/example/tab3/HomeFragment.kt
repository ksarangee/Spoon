package com.example.tab3

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tab3.databinding.FragmentHomeBinding
import com.example.tab3.ui.home.HomeViewModel
import com.example.tab3.ui.home.MyItem
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.appcompat.widget.SwitchCompat
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: MyAdapter
    override fun onResume() {
        super.onResume()
        // 프래그먼트가 화면에 다시 나타날 때마다 권한 요청을 수행
        if (binding.switchSync.isChecked) {
            requestPermissions()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            HomeViewModel::class.java)
        //val switchSync = binding.switchSync


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Initialize RecyclerView
        val switchSync = binding.switchSync
        switchSync.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 동기화 ON
                requestPermissions()
            } else {
                // 동기화 OFF
                // 필요한 동작 추가
            }
        }



        adapter = MyAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        //homeViewModel.deleteAllContacts()
        // Observe contacts LiveData
        homeViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            val sortedContacts = contacts.sortedWith(compareByDescending<MyItem> { it.isFavorite }.thenBy { it.name })
            adapter.items.clear()
            adapter.items.addAll(sortedContacts)
            adapter.notifyDataSetChanged()
        }
        /*
        homeViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            val dataList = contacts.toMutableList()
            adapter.items.clear()
            adapter.items.addAll(dataList)
            adapter.notifyDataSetChanged()
        }

         */

        val decoration = MyAdapter.AddressAdapterDecoration()
        binding.recyclerView.addItemDecoration(decoration)

        adapter.numberClick = object : MyAdapter.NumberClick {
            override fun onNumberClick(view: View, position: Int) {
                val item = adapter.items[position]
                val number: String = item.number
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
            }
        }

        adapter.favoriteClick = object : MyAdapter.FavoriteClick {
            override fun onFavoriteClick(view: View, position: Int) {
                val item = adapter.items[position]
                item.isFavorite = !item.isFavorite
                homeViewModel.updateContact(item)
            }
        }
        adapter.deleteClick = object : MyAdapter.DeleteClick {
            override fun onDeleteClick(view: View, position: Int) {
                Log.d("MyTag", "This is a debug message")
                val item = adapter.items[position]
                homeViewModel.deleteContact(item.profile)
            }
        }
        binding.buttonAddContact.setOnClickListener {
            showAddContactDialog()
        }

/*
        binding.buttonAddContact.setOnClickListener {
            val newContact = MyItem(profile = (adapter.items.size + 1), name = "New Contact", number = "1234567890", isFavorite = false)
            homeViewModel.addContact(newContact)
            Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
        }

 */

        return root
    }
    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contact, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)

        AlertDialog.Builder(context)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = editTextName.text.toString()
                val number = editTextNumber.text.toString()

                if (name.isNotBlank() && number.isNotBlank()) {
                    val newContact = MyItem(profile = homeViewModel.readMaxId(), name = name, number = number, isFavorite = false)
                    homeViewModel.addContact(newContact)
                    Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
                    homeViewModel.addMaxId()
                } else {
                    Toast.makeText(requireContext(), "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            if (permissionsToRequest.isEmpty()) {
                Toast.makeText(requireContext(), "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                showConsentDialog()
            } else {
                Toast.makeText(requireContext(), "일부 권한이 거부되었습니다. 권한을 허용해야 앱이 정상 동작합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }

            if (deniedPermissions.isEmpty()) {
                Toast.makeText(requireContext(), "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                showConsentDialog()
            } else {
                Toast.makeText(requireContext(), "일부 권한이 거부되었습니다. 권한을 허용해야 앱이 정상 동작합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadContacts() {
        val contactsList = mutableListOf<MyItem>()

        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                contactsList.add(MyItem(profile = homeViewModel.readMaxId(), name = name, number = number, isFavorite = false))
                homeViewModel.addMaxId()
            }
        }
        addContacts(contactsList)
        //showConsentDialog(contactsList)
    }

    private fun showConsentDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("연락처 추가")
            .setMessage("연락처를 추가하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                loadContacts()
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    private fun addContacts(contactsList: List<MyItem>) {
        // 여기에서 연락처를 추가하는 작업을 수행합니다.
        // 예를 들어, ViewModel을 사용하여 연락처를 추가할 수 있습니다.
        for (contact in contactsList) {
            homeViewModel.addContact(contact)
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
