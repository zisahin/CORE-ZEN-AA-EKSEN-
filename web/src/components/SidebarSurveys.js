'use client'
import { useState } from 'react'

const miniSurveys = [
  {
    id: 1,
    question: "Bugün nasıl hissediyorsun?",
    options: [
      "Harika hissediyorum",
      "İyi sayılır",
      "Ne iyi ne kötü",
      "Biraz yorgunum"
    ]
  },
  {
    id: 2,
    question: "En sevdiğin mevsim?",
    options: [
      "İlkbahar",
      "Yaz", 
      "Sonbahar",
      "Kış"
    ]
  },
  {
    id: 3,
    question: "En son ne zaman üzgün hissettin?",
    options: [
      "Bu hafta",
      "Geçen hafta",
      "Bu ay içinde",
      "Hatırlamıyorum"
    ]
  },
  {
    id: 4,
    question: "Hangi haber kategorisini daha çok okuyorsun?",
    options: [
      "Siyaset",
      "Spor",
      "Teknoloji",
      "Ekonomi"
    ]
  }
]

export default function SidebarSurveys() {
  const [answeredSurveys, setAnsweredSurveys] = useState(new Set())
  const [results, setResults] = useState({})

  const handleAnswer = (surveyId, answer) => {
    // Cevap verildi olarak işaretle
    setAnsweredSurveys(prev => new Set([...prev, surveyId]))
    
    // Mock sonuçlar ekle
    setResults(prev => ({
      ...prev,
      [surveyId]: {
        userAnswer: answer,
        totalVotes: Math.floor(Math.random() * 200) + 50,
        percentage: Math.floor(Math.random() * 40) + 30 + "%"
      }
    }))
  }

  return (
    <div className="mt-8 space-y-4">
      <h3 className="text-sm font-bold text-gray-700 px-3">AA AI Soruyor</h3>
      
      {miniSurveys.map((survey) => (
        <div key={survey.id} className="bg-blue-50 border-2 border-blue-200 rounded-lg p-3 mx-2 hover:border-blue-300 transition-colors">
          <div className="flex items-center space-x-2 mb-3">
            <span className="text-lg">{survey.emoji}</span>
            <p className="text-sm font-medium text-gray-800">
              {survey.question}
            </p>
          </div>

          {!answeredSurveys.has(survey.id) ? (
            // Henüz cevap verilmemiş
            <div className="space-y-2">
              {survey.options.map((option, index) => (
                <button
                  key={index}
                  onClick={() => handleAnswer(survey.id, option)}
                  className="w-full text-left text-xs bg-[#E3E1D7] hover:bg-blue-50 p-2 rounded border transition-colors"
                >
                  {option}
                </button>
              ))}
            </div>
          ) : (
            // Cevap verilmiş - sonuç göster
            <div className="space-y-2">
              <div className="text-xs text-green-600 font-semibold">
                ✅ Cevabınız: {results[survey.id]?.userAnswer}
              </div>
              <div className="text-xs text-gray-500">
                📊 {results[survey.id]?.totalVotes} kişi katıldı
              </div>
              <div className="text-xs bg-blue-100 text-blue-900 px-2 py-1 rounded-full inline-block">
                +5 XP
              </div>
            </div>
          )}
        </div>
      ))}
      
      <div className="text-center px-3">
        <div className="text-xs text-gray-500 bg-[#E3E1D7] p-2 rounded">
          💡 ChatGPT API bağlandığında daha akıllı sorular!
        </div>
      </div>
    </div>
  )
}