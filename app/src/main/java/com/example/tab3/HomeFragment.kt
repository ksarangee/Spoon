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


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Initialize RecyclerView
        adapter = MyAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
