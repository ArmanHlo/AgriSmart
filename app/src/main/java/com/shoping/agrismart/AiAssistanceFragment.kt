package com.shoping.agrismart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class AiAssistanceFragment : Fragment() {

    private lateinit var aiResponseText: TextView
    private lateinit var userInputEditText: EditText
    private lateinit var sendButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_assistance, container, false)

        aiResponseText = view.findViewById(R.id.aiResponseText)
        userInputEditText = view.findViewById(R.id.userInputEditText)
        sendButton = view.findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val query = userInputEditText.text.toString()
            if (query.isNotEmpty()) {
                generateAiResponse(query)
                userInputEditText.text.clear()
            }
        }

        view.findViewById<View>(R.id.closeButton).setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }

    private fun generateAiResponse(query: String) {
        val q = query.lowercase()
        val response = when {
            q.contains("apple") && (q.contains("bihar") || q.contains("warm") || q.contains("plain")) -> 
                "Growing traditional apples in Bihar is difficult due to the high temperature. However, HRMN-99 is a special 'low-chill' variety that can grow in warmer climates like Bihar."
            
            q.contains("apple") -> 
                "Apples typically require cool climates like Himachal Pradesh or Kashmir. They need a 'chilling period' to fruit well."
            
            q.contains("mango") -> 
                "Mangoes thrive in most parts of India. For Bihar, varieties like Langra and Dudhiya Malda are famous and very productive."
            
            q.contains("rice") || q.contains("paddy") -> 
                "Rice needs lots of water and clayey soil. It's the main Kharif crop in states like Bihar, West Bengal, and Punjab."
            
            q.contains("wheat") -> 
                "Wheat is a Rabi crop. It needs cool weather during growth and bright sunshine at ripening. Bihar's plains are excellent for wheat."
            
            q.contains("soil") -> 
                "For most Indian crops, well-drained loamy soil is ideal. Black soil is best for cotton, while Alluvial soil (common in Bihar) is great for grains and vegetables."
            
            q.contains("water") || q.contains("rain") -> 
                "Crops like Rice and Sugarcane need high water. If you have limited water, consider Drip Irrigation for vegetables or growing Mustard/Maize."
            
            q.contains("pest") -> 
                "Common pests include aphids, borers, and whiteflies. You can use Neem oil as a natural pesticide or consult a local Krishi Kendra for specific chemicals."
            
            q.contains("fertilizer") -> 
                "NPK (Nitrogen, Phosphorus, Potassium) is essential. Use Urea for Nitrogen, DAP for Phosphorus, and MOP for Potassium. Always add organic manure for soil health."

            q.contains("bihar") -> 
                "Bihar has very fertile Alluvial soil. Top crops include Rice, Wheat, Maize, Sugarcane, and Litchi (especially in Muzaffarpur)."
            
            else -> "That's an interesting question! In India, cropping depends heavily on the specific region and season. For $query, I recommend checking local soil health and water availability."
        }
        
        val currentText = aiResponseText.text.toString()
        aiResponseText.text = "$currentText\n\nYou: $query\nAI: $response"
    }
}
