package com.example.tab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tab3.databinding.DiaryCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.example.tab3.ui.notifications.NotificationsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {

    private lateinit var binding: DiaryCalendarBinding
    var db: AppDatabase? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DiaryCalendarBinding.inflate(inflater, container, false)
        val bottomSheetLayout: LinearLayout = binding.layout.bottomSheetLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetLayout?.visibility = View.VISIBLE
        Log.d("When Create", "onCreateView: ${bottomSheetBehavior?.state}")
        Log.d("When Create", "Layout visibility: ${bottomSheetLayout?.visibility}")
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        Log.d("ViewCreated", "onCreateView: ${bottomSheetBehavior?.state}")
        view?.post {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val todayCalendar = Calendar.getInstance()
        val todayYear = todayCalendar.get(Calendar.YEAR)
        val todayMonth = todayCalendar.get(Calendar.MONTH) + 1
        val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)
        showView(todayYear, todayMonth, todayDay)

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val today = dateFormat.format(todayCalendar.time)
        binding.layout.todayDate.text = today

        binding.layout.diaryimg.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            navigateToNewDiary(todayYear, todayMonth, todayDay)
        }


        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            showView(year, month+1, dayOfMonth)
            binding.layout.diaryimg.setOnClickListener {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                navigateToNewDiary(year, month+1, dayOfMonth) // Workout data 전체 보내기
            }

            val monthFormatted = String.format(Locale.getDefault(), "%02d", month+1)
            val dayFormatted = String.format(Locale.getDefault(), "%02d", dayOfMonth)
            binding.layout.todayDate.text = "$year/$monthFormatted/$dayFormatted"
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun showView(year:Int, month:Int, day:Int){
        CoroutineScope(Dispatchers.IO).launch{
            val dateWorkout = db?.diaryDao()?.getWorkoutByDate(year, month, day)
            var selectedWorkout: Diary? = dateWorkout?.firstOrNull()  //선택한 날짜의 diary 데이터
            Log.d("check", "$selectedWorkout")
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(50)
            )

            withContext(Dispatchers.Main) {
                if (selectedWorkout != null) {
                    // changing View to uploaded images
                    // **changing 일기 이미지
                    if (selectedWorkout.diaryImg != null) {
                        Glide.with(requireContext())
                            .load(selectedWorkout.diaryImg)
                            .apply(RequestOptions.bitmapTransform(transformation))
                            .into(binding.layout.diaryimg)
                        binding.layout.diaryContent.text = selectedWorkout.diaryContent
                    } else {
                        Glide.with(requireContext())
                            .load(R.drawable.blankimg)
                            .apply(RequestOptions.bitmapTransform(transformation))
                            .into(binding.layout.diaryimg)
                        binding.layout.diaryContent.text = resources.getString(R.string.nodiary)
                    }

                    // changing View finished
                } else {    // 선택된 날짜에 해당하는 데이터가 없는 경우
                    // db에 해당 날짜의 데이터를 추가
                    Glide.with(requireContext())
                        .load(R.drawable.blankimg)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(binding.layout.diaryimg)
                    binding.layout.diaryContent.text = resources.getString(R.string.nodiary)
                }
            }
        }
    }

    private fun navigateToNewDiary(year:Int, month: Int, day: Int){
        val newworkout = NewDiary(year, month, day)
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.calendarFragment, newworkout)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }}

