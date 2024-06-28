package com.example.tab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.example.tab3.databinding.FragmentDiaryAddBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewDiary(year:Int, month:Int, Day:Int) : Fragment() {
    private lateinit var binding: FragmentDiaryAddBinding
    lateinit var getDiary: ActivityResultLauncher<String>
    var db: AppDatabase? = null
    var DiaryData = mutableListOf<Diary>()
    var inputimage: Uri? = null
    val selectedDate = "$year-$month-$Day"
    val addYear = year
    val addMonth = month
    val addDay = Day

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiaryAddBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        Log.d("date", "$selectedDate")

        getDiary =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
//                    requireActivity().contentResolver.takePersistableUriPermission(
//                        it,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
                    inputimage = it
                    binding.diaryImage.setImageURI(it)
                }

            }

        binding.selectdiaryimg.setOnClickListener {
            openImagePicker()
        }

        binding.diarySave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                createWorkoutAndNavigateBack(inputimage)
                navigateToCalendar()
                CheckDataBase()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToCalendar()
        }

        return binding.root

    }

    private fun openImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
        getDiary.launch("image/*")
    }

    private fun createWorkoutAndNavigateBack(img: Uri?) {

        val diarytext = binding.diaryText.text.toString()
        val dateDiary = db?.diaryDao()?.getWorkoutByDate(addYear, addMonth, addDay)
        var selectedWorkout: Diary? = dateDiary?.firstOrNull()

        if(diarytext.isNotEmpty() && diarytext.isNotEmpty()){
            val newDiary = Diary(
                year = addYear,
                month = addMonth,
                date = addDay,
                diaryImg = img,
                diaryContent = diarytext
            )
            db?.diaryDao()?.insertOrUpdate(newDiary)
            DiaryData.add(newDiary)
            Log.d("newLog", newDiary.toString())
        }else{

        }


    }

    private fun navigateToCalendar() {
        // Navigate to the "my_address" fragment
        val myCalendar = DiaryFragment()

        val fragmentTransaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.addDiaryPage, myCalendar)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun CheckDataBase(){
        val savedDiary = db?.diaryDao()?.getAll()?: emptyList()
        Log.d("savedDiary", savedDiary.toString())
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
