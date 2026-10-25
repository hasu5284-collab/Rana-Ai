package com.ranaai.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.*
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.text.InputType
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(10, 20, 40)
        window.navigationBarColor = Color.rgb(10, 20, 40)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 16)
            setBackgroundColor(Color.rgb(10, 20, 40))
        }
        val title = TextView(this).apply {
            text = "✦  Rana AI"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 18)
        }
        val subtitle = TextView(this).apply {
            text = "আপনার ব্যক্তিগত AI সহকারী"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(4, 0, 4, 18)
        }
        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 14, 14, 14)
            setBackgroundColor(Color.rgb(20, 35, 60))
        }
        val scroll = ScrollView(this).apply {
            addView(messages)
        }
        val inputRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        input = EditText(this).apply {
            hint = "এখানে লিখুন…"
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
            textSize = 16f
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(14, 10, 14, 10)
            setBackgroundColor(Color.rgb(35, 52, 78))
        }
        val send = Button(this).apply { text = "পাঠান" }
        val mic = Button(this).apply { text = "🎙" }
        inputRow.addView(input, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        inputRow.addView(mic)
        inputRow.addView(send)
        root.addView(title)
        root.addView(subtitle)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(inputRow)
        setContentView(root)
        addMessage("Rana AI", "আসসালামু আলাইকুম! আমি Rana AI। লিখে অথবা মাইক্রোফোন চেপে কথা বলুন।")
        send.setOnClickListener { submitText() }
        input.setOnEditorActionListener { _, _, _ -> submitText(); true }
        mic.setOnClickListener { startVoiceInput() }
    }

    private fun addMessage(who: String, text: String) {
        val view = TextView(this).apply {
            this.text = "$who\n$text"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(12, 10, 12, 10)
            setBackgroundColor(if (who == "আপনি") Color.rgb(38, 75, 110) else Color.rgb(28, 48, 76))
        }
        val lp = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.bottomMargin = 12
        messages.addView(view, lp)
    }

    private fun submitText() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        input.setText("")
        addMessage("আপনি", text)
        val reply = when {
            text.contains("হ্যালো") || text.contains("হাই") -> "হ্যালো! কীভাবে সাহায্য করতে পারি?"
            text.contains("সময়") -> "আপনার ফোনের ঘড়িতে বর্তমান সময় দেখতে পারবেন।"
            text.contains("নাম") -> "আমার নাম Rana AI।"
            else -> "আপনার কথা বুঝেছি: “$text”। পূর্ণ AI উত্তর দিতে নিরাপদ AI সার্ভার/API সংযোগ পরের ধাপে যোগ করতে হবে।"
        }
        addMessage("Rana AI", reply)
        tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "rana_reply")
    }

    private fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            addMessage("Rana AI", "এই ফোনে speech recognition সুবিধা পাওয়া যাচ্ছে না।")
            return
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 41)
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "বলুন…")
        }
        try { startActivityForResult(intent, 42) } catch (_: Exception) {
            addMessage("Rana AI", "ভয়েস ইনপুট চালু করা যায়নি।")
        }
    }

    @Deprecated("Legacy activity result for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) { input.setText(spoken); submitText() }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts?.language = Locale("bn", "BD")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
