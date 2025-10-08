package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.json.JSONObject

class NOdDefinition : Fragment() {

    private lateinit var lessonTitleText: TextView
    private lateinit var wordText: TextView
    private lateinit var wordDefinitionText: TextView
    private lateinit var message1Text: TextView
    private lateinit var message2Text: TextView
    private lateinit var message3Text: TextView
    private lateinit var nextWordButton: Button

    private var vocabularyList: List<NOdVocabularyItem> = listOf()
    private var messagesJson: JSONObject? = null
    private var currentIndex = 0

    companion object {
        private const val ARG_VOCAB = "vocab"
        private const val ARG_MESSAGES = "messages"

        fun newInstance(
            list: List<NOdVocabularyItem>,
            messages: JSONObject
        ): NOdDefinition {
            val fragment = NOdDefinition()
            val bundle = Bundle()
            bundle.putSerializable(ARG_VOCAB, ArrayList(list))
            bundle.putString(ARG_MESSAGES, messages.toString())
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vocabularyList = arguments?.getSerializable(ARG_VOCAB) as? List<NOdVocabularyItem> ?: listOf()
        arguments?.getString(ARG_MESSAGES)?.let {
            messagesJson = JSONObject(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_n_od_definition, container, false)

        lessonTitleText = view.findViewById(R.id.lessonTitleText)
        wordText = view.findViewById(R.id.wordText)
        wordDefinitionText = view.findViewById(R.id.wordDefinitionText)
        message1Text = view.findViewById(R.id.message1Text)
        message2Text = view.findViewById(R.id.message2Text)
        message3Text = view.findViewById(R.id.message3Text)
        nextWordButton = view.findViewById(R.id.nextWordButton)

        if (vocabularyList.isNotEmpty()) {
            lessonTitleText.text = vocabularyList[0].lessonTitle
            showWord(currentIndex)
        }

        nextWordButton.setOnClickListener {
            if (currentIndex + 1 < vocabularyList.size) {
                currentIndex++
                showWord(currentIndex)
            }
        }

        return view
    }

    private fun showWord(index: Int) {
        val item = vocabularyList[index]
        wordText.text = item.word
        wordDefinitionText.text = item.definition

        // show messages
        val messagesForWord = messagesJson?.optJSONObject(item.word)
        if (messagesForWord != null) {
            message1Text.text = messagesForWord.optString("Message1", "")
            message2Text.text = messagesForWord.optString("Message2", "")
            message3Text.text = messagesForWord.optString("Message3", "")
        } else {
            message1Text.text = ""
            message2Text.text = ""
            message3Text.text = ""
        }
    }
}
