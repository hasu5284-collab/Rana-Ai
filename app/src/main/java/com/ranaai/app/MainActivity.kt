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
    private lateinit var scroll: ScrollView
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
        scroll = ScrollView(this).apply { addView(messages) }
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
        addMessage("Rana AI", "আসসালামু আলাইকুম! আমি Rana AI। লিখে অথবা মাইক্রোফোন চেপে কথা বলুন।\n\nআপনি আমাকে সাধারণ প্রশ্ন করতে পারেন।")
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
        scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun makeReply(original: String): String {
        val q = original.lowercase(Locale.ROOT)
            .replace(" ", "")
            .replace("।", "")
            .replace("?", "")
            .replace("!", "")
            .replace(".", "")

        return when {
            listOf("হ্যালো", "হালো", "হেলো", "হাই", "hello", "hi", "আসসালামু").any { q.contains(it) } ->
                "ওয়ালাইকুম আসসালাম! 😊 কেমন আছেন? কী বিষয়ে সাহায্য লাগবে?"
            listOf("তোমারনাম", "তোমাকিকলে", "নামকি", "তুমিকে", "whoareyou").any { q.contains(it) } ->
                "আমার নাম Rana AI। আমি আপনার বাংলা ভাষার সহকারী।"
            listOf("কিকরবো", "কিকরব", "কিকরবেতাহলে", "এখনকিকরবো").any { q.contains(it) } ->
                "আপনি যে বিষয়ে সাহায্য চান, সেটি সহজ করে লিখুন। যেমন: হিসাব, পড়াশোনা, ফোনের সমস্যা, কাজ বা সাধারণ তথ্য।"
            listOf("সাহায্য", "help", "কিপার", "কিকরতে").any { q.contains(it) } ->
                "আমি সাধারণ প্রশ্নের উত্তর, সহজ হিসাব, বাংলা লেখা ও ফোনের সাধারণ সমস্যা বুঝতে সাহায্য করতে পারি। আপনার প্রশ্নটি লিখুন।"
            listOf("ধন্যবাদ", "থ্যাংক", "thanks").any { q.contains(it) } ->
                "আপনাকে স্বাগতম! আর কিছু জানতে চাইলে বলুন।"
            listOf("সময়", "কয়টাবাজে", "time").any { q.contains(it) } ->
                "বর্তমান সময় জানতে আপনার ফোনের ওপরের স্ট্যাটাস বার বা Clock অ্যাপ দেখুন।"
            listOf("যোগ", "বিয়োগ", "গুণ", "ভাগ", "হিসাব", "ক্যালকুলেটর").any { q.contains(it) } ->
                "হিসাবটি সংখ্যাসহ লিখুন। যেমন: ১২৫০ টাকা ৫ জনে ভাগ করলে কত?"
            listOf("তুমি কি", "পারবে").any { q.contains(it) } ->
                "আমি এই অ্যাপের মধ্যে থাকা সাধারণ সহায়তাগুলো দিতে পারি। তবে এখনো ইন্টারনেটভিত্তিক পূর্ণ AI-এর সঙ্গে যুক্ত নই।"
            else ->
                "আপনার প্রশ্নটি পেয়েছি: “$original”। এই বিনামূল্যের সংস্করণে আমার উত্তর দেওয়ার ক্ষমতা সীমিত, কারণ এখনো কোনো AI সার্ভারের সঙ্গে সংযোগ নেই। প্রশ্নটি আরেকটু সহজ করে বা বিস্তারিত লিখে দেখুন।"
        }
    }

    private fun submitText() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        input.setText("")
        addMessage("আপনি", text)
        val reply = makeReply(text)
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
        try {
            startActivityForResult(intent, 42)
        } catch (_: Exception) {
            addMessage("Rana AI", "ভয়েস ইনপুট চালু করা যায়নি।")
        }
    }

    @Deprecated("Legacy activity result for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                input.setText(spoken)
                submitText()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
