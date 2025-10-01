// components/AISurvey.js
'use client'
import { useState, useEffect } from 'react'

const surveys = [
  {
    id: 1,
    question: "Yaşam şartlarından memnun musunuz?",
    options: [
      "Evet, memnunum",
      "Kısmen memnunum", 
      "Hayır, memnun değilim"
    ]
  },
  {
    id: 2,
    question: "Türkiye'nin ekonomik durumu hakkında ne düşünüyorsunuz?",
    options: [
      "İyiye gidiyor",
      "Değişmiyor",
      "Kötüye gidiyor"
    ]
  },
  {
    id: 3,
    question: "Günde kaç saat haber takip ediyorsunuz?",
    options: [
      "30 dakikadan az",
      "30 dakika - 2 saat",
      "2 saatten fazla"
    ]
  }
];

export default function AISurvey() {
  const [currentSurvey, setCurrentSurvey] = useState(null)
  const [isVisible, setIsVisible] = useState(false)
  const [hasAnswered, setHasAnswered] = useState(false)
  const [results, setResults] = useState(null)

  useEffect(() => {
    // 5 saniye sonra anket göster
    const timer = setTimeout(() => {
      const randomSurvey = surveys[Math.floor(Math.random() * surveys.length)]
      setCurrentSurvey(randomSurvey)
      setIsVisible(true)
    }, 5000)

    return () => clearTimeout(timer)
  }, [])

  const handleAnswer = (answer) => {
    setHasAnswered(true)
    // Mock results
    setResults({
      [currentSurvey.options[0]]: "35%",
      [currentSurvey.options[1]]: "42%", 
      [currentSurvey.options[2]]: "23%"
    })

    // 3 saniye sonra kapat
    setTimeout(() => {
      setIsVisible(false)
    }, 3000)
  }

  if (!isVisible || !currentSurvey) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-6 max-w-md mx-4 shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2">
            <span className="text-2xl"></span>
            <h3 className="font-bold text-blue-800">AA AI Soruyor</h3>
          </div>
          <button 
            onClick={() => setIsVisible(false)}
            className="text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        <p className="text-gray-800 mb-6 text-lg">
          {currentSurvey.question}
        </p>

        {!hasAnswered ? (
          <div className="space-y-3">
            {currentSurvey.options.map((option, index) => (
              <button
                key={index}
                onClick={() => handleAnswer(option)}
                className="w-full p-3 text-left bg-[#E3E1D7] hover:bg-blue-50 rounded-lg transition-colors"
              >
                {option}
              </button>
            ))}
          </div>
        ) : (
          <div className="space-y-3">
            <p className="text-green-600 font-semibold mb-4">✅ Cevabınız kaydedildi!</p>
            <div className="text-sm text-gray-600">
              <p className="font-semibold mb-2">Sonuçlar:</p>
              {Object.entries(results).map(([option, percentage]) => (
                <div key={option} className="flex justify-between mb-1">
                  <span>{option}</span>
                  <span className="font-semibold">{percentage}</span>
                </div>
              ))}
            </div>
            <div className="mt-4 text-center">
              <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm">
                +15 XP Kazandınız!
              </span>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

// Inline kart olarak kullanılacak hafif sürüm
export function InlineAISurvey({ fetchSurvey }) {
  const [survey, setSurvey] = useState(null)
  const [answer, setAnswer] = useState(null)

  useEffect(() => {
    let isMounted = true
    const load = async () => {
      if (fetchSurvey) {
        try {
          const srv = await fetchSurvey()
          if (isMounted) setSurvey(srv)
          return
        } catch {}
      }
      const randomSurvey = surveys[Math.floor(Math.random() * surveys.length)]
      if (isMounted) setSurvey(randomSurvey)
    }
    load()
    return () => { isMounted = false }
  }, [fetchSurvey])

  if (!survey) return null

  const results = answer
    ? {
        [survey.options[0]]: '35%',
        [survey.options[1]]: '42%',
        [survey.options[2]]: '23%'
      }
    : null

  return (
    <div className="bg-white border rounded-xl p-4 md:p-5 shadow-sm">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center space-x-2">
          <span className="text-xl"></span>
          <h3 className="font-bold text-blue-800">AI Soruyor</h3>
        </div>
        <span className="bg-green-100 text-green-800 px-2 py-0.5 rounded-full text-xs">+10 XP</span>
      </div>

      <p className="text-gray-800 mb-4">{survey.question}</p>

      {!answer ? (
        <div className="grid gap-2 sm:grid-cols-3">
          {survey.options.map((opt) => (
            <button
              key={opt}
              onClick={() => setAnswer(opt)}
              className="text-left px-3 py-2  hover:bg-blue-50 rounded-lg transition-colors"
            >
              {opt}
            </button>
          ))}
        </div>
      ) : (
        <div className="space-y-2">
          <p className="text-green-600 font-semibold">✅ Cevabınız kaydedildi</p>
          {Object.entries(results).map(([opt, perc]) => (
            <div key={opt}>
              <div className="flex justify-between text-sm text-gray-600">
                <span>{opt}</span>
                <span className="font-medium text-gray-800">{perc}</span>
              </div>
              <div className="w-full h-2 bg-[#E3E1D7] rounded">
                <div
                  className="h-2 bg-blue-500 rounded"
                  style={{ width: perc }}
                />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}