/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentFirstTimeGuidanceBinding
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.util.device.SizeMetrics
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel

private const val TAG = "FirstTimeGuidance"

class FirstTimeGuidance : DialogFragment() {

    private var _binding: FragmentFirstTimeGuidanceBinding? = null
    private val binding
        get() = _binding!!
    private val deviceWidth
        get() = SizeMetrics(requireContext()).getPxWindowSize().first
    private val deviceHeight
        get() = SizeMetrics(requireContext()).getPxWindowSize().second
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    companion object {
        fun newInstance() = FirstTimeGuidance()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = FragmentFirstTimeGuidanceBinding.inflate(requireActivity().layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setDimAmount(0f)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        guideWhenShowMainFirstTime()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }

    fun drawRect(canvas: Canvas) {
        val paintRect = Paint()
        paintRect.color = Color.argb(170,0,0,0)
        paintRect.style = Paint.Style.FILL
        canvas.drawRect(
            0f,
            0f,
            deviceWidth.toFloat(),
            deviceHeight.toFloat(),
            paintRect)
    }

    fun drawCircle(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val paintCircle = Paint()
        paintCircle.isAntiAlias = true
        paintCircle.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawCircle(cx, cy, radius, paintCircle)
    }

    fun setVisibility(btnGreen: Boolean, card: Boolean, cardColor: Int, fontColor: Int, overlay:Boolean, swipe: Boolean, tap: Boolean) {
        val activity = activity as MainActivity
        activity.binding.recordButtonOn.isVisible = !btnGreen
        activity.binding.recordButtonOff.isVisible = btnGreen
        binding.sampleCard.layoutParams.width = deviceWidth - 50
        binding.sampleCard.isVisible = card
        binding.sampleCard.setBackgroundColor(cardColor)
        binding.sampleCard.setTextColor(fontColor)
        binding.sampleRabbit.isVisible = overlay
        binding.swipeOrientation.isVisible = swipe
        binding.swipeFinger.isVisible = swipe
        binding.tapFinger.isVisible = tap
    }

    private fun guideWhenShowMainFirstTime() {
        Log.d(TAG, "guideWhenShowMainFirstTime() is Running")
        FirstTimeGuidanceAppearing.current = FirstTimeGuidanceAppearing.TRUE
        binding.guidanceBase.setOnClickListener {}
        val message1 = requireContext().getString(R.string.ftg_showmainfirsttime_1)
        val message2 = requireContext().getString(R.string.ftg_showmainfirsttime_2)
        val message3 = requireContext().getString(R.string.ftg_showmainfirsttime_3)
        val message4 = requireContext().getString(R.string.ftg_showmainfirsttime_4)
        val message5 = requireContext().getString(R.string.ftg_showmainfirsttime_5)
        val message6 = requireContext().getString(R.string.ftg_showmainfirsttime_6)
        val message7 = requireContext().getString(R.string.ftg_showmainfirsttime_7)
        val message8 = requireContext().getString(R.string.ftg_showmainfirsttime_8)
        val message9 = requireContext().getString(R.string.ftg_showmainfirsttime_9)
        val message10 = requireContext().getString(R.string.ftg_showmainfirsttime_10)
        val message11 = requireContext().getString(R.string.ftg_showmainfirsttime_11)
        val message12 = requireContext().getString(R.string.ftg_showmainfirsttime_12)
        val message13 = requireContext().getString(R.string.ftg_showmainfirsttime_13)
        // TODO change marginfromtop to relative margin
        val cvp1 = CustomViewPresenter(this, ViewWelcomeToCVSentence(requireContext()), message1, 300)
        val cvp2 = CustomViewPresenter(this, ViewPressStartButton(requireContext()),message2, 300)
        val cvp3 = CustomViewPresenter(this, ViewOverlayAppear(requireContext()),message3,400)
        val cvp4 = CustomViewPresenter(this, ViewOverlayAppear(requireContext()),message4,400)
        val cvp5 = CustomViewPresenter(this, ViewOverlayAppear(requireContext()),message5,400)
        val cvp6 = CustomViewPresenter(this, ViewPressStopButton(requireContext()),message6,400)
        val cvp7 = CustomViewPresenter(this, ViewYouCanDeleteCardBySwipe(requireContext()), message7, 600)
        val cvp8 = CustomViewPresenter(this, ViewYouCanSeeWordMeaningByTap(requireContext()), message8, 600)
        val cvp9 = CustomViewPresenter(this, ViewCardTintTurnsGreenWhenRRTArrived(requireContext()),message9,600)
        val cvp10 = CustomViewPresenter(this, ViewCardTintTurnsGreenWhenRRTArrived(requireContext()),message10,600)
        val cvp11 = CustomViewPresenter(this, ViewYouCanSortOnSortSettings(requireContext()),message11,300)
        val cvp12 = CustomViewPresenter(this, ViewYouCanSetSoundOnBaseSettings(requireContext()),message12,300)
        val cvp13 = CustomViewPresenter(this, ViewWelcomeToCVSentence(requireContext()), message13, 300)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            cvp1.next = cvp2; cvp2.next = cvp3; cvp3.next = cvp4; cvp4.next = cvp5; cvp5.next = cvp6; cvp6.next = cvp7
            cvp7.next = cvp8; cvp8.next = cvp9; cvp9.next = cvp10; cvp10.next = cvp11; cvp11.next = cvp12; cvp12.next = cvp13
        } else {
            cvp1.next = cvp2; cvp2.next = cvp3; cvp3.next = cvp5; cvp5.next = cvp6; cvp6.next = cvp7
            cvp7.next = cvp8; cvp8.next = cvp9; cvp9.next = cvp10; cvp10.next = cvp11; cvp11.next = cvp12; cvp12.next = cvp13
        }
        cvp13.ifNextExists = false
        cvp1.show()
        mainActivityViewModel.setIfShowMainFirstTime()
    }

    fun onRemoveGuidance() {
        binding.guidanceBase.removeAllViews()
        this.dismiss()
        mainActivityViewModel.backToList()
    }
    /**
     * classes below are custom views to darken the background
     * and highlight the certain position where guidance is explaining.
     */
    internal inner class ViewWelcomeToCVSentence(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true, false, Color.parseColor("#ffffff"), Color.parseColor("#4D4D4D"),false, false,false)
            drawRect(canvas)
        }
    }
    internal inner class ViewPressStartButton(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true, false, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),false,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat()/2,
                deviceHeight.toFloat() - 100f,
                deviceWidth.toFloat()/4)
        }
    }
    internal inner class ViewOverlayAppear(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(false,false, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),true,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat() - 150f,
                (300-deviceWidth/2).toFloat(),
                deviceWidth.toFloat()/2)
        }
    }
    internal inner class ViewPressStopButton(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(false, false, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),true,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat()/2,
                deviceHeight.toFloat() - 100f,
                deviceWidth.toFloat()/4)
        }
    }
    internal inner class ViewYouCanDeleteCardBySwipe(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true,true, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),false,true,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat() /2,
                (-deviceWidth + 400).toFloat(),
                deviceWidth.toFloat())
        }
    }
    internal inner class ViewYouCanSeeWordMeaningByTap(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true,true, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),false,false,true)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat() /2,
                (-deviceWidth + 400).toFloat(),
                deviceWidth.toFloat())
        }
    }
    internal inner class ViewCardTintTurnsGreenWhenRRTArrived(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true,true, resources.getColor(R.color.theme_color, requireActivity().theme),Color.parseColor("#ffffff"),false,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat() /2,
                (-deviceWidth + 400).toFloat(),
                deviceWidth.toFloat())
        }
    }
    internal inner class ViewYouCanSortOnSortSettings(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true,false, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),false,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                0f,
                (deviceHeight * 1.2).toFloat(),
                (deviceWidth/1.5).toFloat())
        }
    }
    internal inner class ViewYouCanSetSoundOnBaseSettings(context: Context) : View(context) {
        init {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
        override fun onDraw(canvas: Canvas){
            setVisibility(true,false, Color.parseColor("#ffffff"),Color.parseColor("#4D4D4D"),false,false,false)
            drawRect(canvas)
            drawCircle(canvas,
                deviceWidth.toFloat(),
                (deviceHeight * 1.2).toFloat(),
                (deviceWidth/1.5).toFloat())
        }
    }

    class CustomViewPresenter(
        private val ftg: FirstTimeGuidance,
        private val customView: View,
        private val message: String,
        private val marginFromTop: Int,
    )
    {
        lateinit var next: CustomViewPresenter
        var ifNextExists = true
        fun show() {
            Log.d(TAG, "CustomViewPresenter's show() method is running")
            ftg.binding.guidanceBase.addView(customView)
            val text = setTextGuidance(
                message,
                marginFromTop
            )
            ftg.binding.guidanceBase.setOnClickListener {
                ftg.binding.guidanceBase.removeView(customView)
                ftg.binding.guidanceBase.removeView(text)
                if (ifNextExists) {
                    next.show()
                } else {
                    ftg.onRemoveGuidance()
                    FirstTimeGuidanceAppearing.current = FirstTimeGuidanceAppearing.FALSE
                }
            }
        }
        private fun setTextGuidance(guide:String, pxFromTop:Int): TextView {
            val guideText = TextView(ftg.requireContext()).apply { id = View.generateViewId() }
            val str = guide + ftg.requireContext().getString(R.string.ftg_ok_button)
            guideText.text = str
            guideText.textSize = 24f
            guideText.setTextColor(Color.WHITE)
            guideText.setPadding(20,20,20,20)
            guideText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            ftg.binding.guidanceBase.addView(guideText)
            val set = ConstraintSet()
            set.constrainHeight(guideText.id, ViewGroup.LayoutParams.WRAP_CONTENT)
            set.constrainWidth(guideText.id, ftg.deviceWidth)
            set.connect(guideText.id, ConstraintSet.LEFT, ftg.binding.guidanceBase.id, ConstraintSet.LEFT, 0)
            set.connect(guideText.id, ConstraintSet.RIGHT, ftg.binding.guidanceBase.id, ConstraintSet.RIGHT, 0)
            set.connect(guideText.id, ConstraintSet.TOP, ftg.binding.guidanceBase.id, ConstraintSet.TOP, pxFromTop)
            set.applyTo(ftg.binding.guidanceBase)
            return guideText
        }
    }
}

enum class FirstTimeGuidanceAppearing {
    TRUE,FALSE;
    companion object {
        var current = FALSE
    }
}