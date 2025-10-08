'use client'

import { useState, useEffect } from 'react'
import aiService from '@/services/aiService'

interface DailyQuestion {
  id: string
  text: string
  category: string
  options: string[]
}

interface AIDailyQuestionsProps {
  maxShow?: number // Ana sayfada 1, detay sayfasında 1
}

export default function AIDailyQuestions({ maxShow = 1 }: AIDailyQuestionsProps) {
  const [questions, setQuestions] = useState<DailyQuestion[]>([])
  const [answeredQuestions, setAnsweredQuestions] = useState<string[]>([])
  const [selectedAnswers, setSelectedAnswers] = useState<Record<string, number>>({})
  const [loading, setLoading] = useState(true)

  // LocalStorage'dan cevaplanan soruları yükle
  useEffect(() => {
    const today = new Date().toLocaleDateString('tr-TR')
    const stored = localStorage.getItem(`ai_questions_${today}`)
    if (stored) {
      const data = JSON.parse(stored)
      setAnsweredQuestions(data.answeredIds || [])
    }
    loadDailyQuestions()
  }, [])

  const loadDailyQuestions = async () => {
    try {
      setLoading(true)
      const response = await fetch('http://localhost:3001/api/questions/daily')
      const data = await response.json()
      
      if (data.success && data.questions) {
        setQuestions(data.questions)
      }
    } catch (error) {
      console.error('❌ Günlük sorular yüklenemedi:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleOptionSelect = (questionId: string, optionIndex: number) => {
    // Seçimi kaydet
    setSelectedAnswers(prev => ({
      ...prev,
      [questionId]: optionIndex
    }))

    // 2 saniye sonra soruyu cevaplandı olarak işaretle
    setTimeout(() => {
      const newAnswered = [...answeredQuestions, questionId]
      setAnsweredQuestions(newAnswered)
      
      // LocalStorage'a kaydet
      const today = new Date().toLocaleDateString('tr-TR')
      localStorage.setItem(`ai_questions_${today}`, JSON.stringify({
        answeredIds: newAnswered,
        date: today
      }))
    }, 2000)
  }

  // Cevaplanmamış soruları filtrele
  const unansweredQuestions = questions.filter(q => !answeredQuestions.includes(q.id))
  
  // Gösterilecek soruları belirle (max sayıda)
  const questionsToShow = unansweredQuestions.slice(0, maxShow)
  
  // Tüm sorular cevaplandı mı?
  const allAnswered = questions.length > 0 && unansweredQuestions.length === 0

  if (loading) {
    return (
      <div className="bg-white rounded-2xl shadow-lg p-6 border-2 border-purple-100">
        <div className="animate-pulse">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
            <div className="h-6 bg-gray-200 rounded w-32"></div>
          </div>
          <div className="space-y-3">
            <div className="h-4 bg-gray-200 rounded"></div>
            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
          </div>
        </div>
      </div>
    )
  }

  if (questions.length === 0) {
    return null
  }

  // Tüm sorular bittiğinde tamamlandı kartı göster
  if (allAnswered) {
    return (
      <div className="bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl shadow-lg p-6 text-white text-center animate-in">
        <div className="mb-4">
          <div className="w-24 h-24 bg-white/20 backdrop-blur rounded-full flex items-center justify-center mx-auto mb-4 animate-bounce">
            <span className="text-5xl">✅</span>
          </div>
          <h3 className="font-bold text-2xl mb-3">Harikasın! 🎉</h3>
          <p className="text-base text-white/95 mb-4 font-medium">
            AI Soruyor için bugünlük bu kadar!
          </p>
          <div className="bg-white/20 backdrop-blur rounded-xl p-4 mb-4">
            <p className="text-lg font-bold mb-2">
              3/3 Soru Tamamlandı! ✨
            </p>
            <p className="text-sm text-white/90">
              Yarın yeni sorular için tekrar bekleriz
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur rounded-xl px-4 py-3 inline-flex items-center justify-center gap-2">
            <span className="text-2xl">⭐</span>
            <span className="font-bold text-lg">+30 XP Kazandın!</span>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-500 to-blue-500 rounded-2xl shadow-lg p-4 text-white">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-white/20 backdrop-blur rounded-full flex items-center justify-center">
            <img 
              src="/images/ai-assistant.png" 
              alt="AI Asistan" 
              className="w-8 h-8 object-contain"
            />
          </div>
          <div className="flex-1">
            <h3 className="font-bold text-lg">🎯 AI Soruyor</h3>
            <p className="text-xs text-white/80">
              {answeredQuestions.length}/{questions.length} cevaplandı
            </p>
          </div>
          {/* Progress Circle */}
          <div className="relative w-12 h-12">
            <svg className="w-12 h-12 transform -rotate-90">
              <circle
                cx="24"
                cy="24"
                r="20"
                stroke="rgba(255,255,255,0.2)"
                strokeWidth="4"
                fill="none"
              />
              <circle
                cx="24"
                cy="24"
                r="20"
                stroke="white"
                strokeWidth="4"
                fill="none"
                strokeDasharray={`${(answeredQuestions.length / questions.length) * 125} 125`}
                className="transition-all duration-500"
              />
            </svg>
            <div className="absolute inset-0 flex items-center justify-center text-xs font-bold">
              {answeredQuestions.length}
            </div>
          </div>
        </div>
      </div>

      {/* Questions */}
      {questionsToShow.map((question, qIndex) => {
        const isAnswered = selectedAnswers[question.id] !== undefined
        
        return (
        <div 
          key={question.id}
          className={`bg-white rounded-2xl shadow-lg p-5 border-2 hover:border-purple-200 transition-all duration-500 ${
            isAnswered ? 'border-green-300 bg-green-50 animate-pulse' : 'border-purple-50'
          }`}
        >
          {/* Question Text */}
          <div className="mb-4">
            <div className="flex items-start gap-2 mb-2">
              <span className="text-2xl">💭</span>
              <p className="text-gray-800 font-medium text-sm leading-relaxed flex-1">
                {question.text}
              </p>
            </div>
            <span className="inline-block px-3 py-1 bg-purple-100 text-purple-700 text-xs font-semibold rounded-full">
              {question.category}
            </span>
          </div>

          {/* Options */}
          <div className="space-y-2">
            {question.options.map((option, index) => {
              const isSelected = selectedAnswers[question.id] === index
              
              return (
                <button
                  key={index}
                  onClick={() => handleOptionSelect(question.id, index)}
                  className={`w-full text-left px-4 py-3 rounded-xl font-medium text-sm transition-all duration-200 ${
                    isSelected
                      ? 'bg-gradient-to-r from-purple-500 to-blue-500 text-white shadow-lg transform scale-[1.02]'
                      : 'bg-gray-50 text-gray-700 hover:bg-purple-50 hover:text-purple-700 hover:shadow-md'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className={`w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 ${
                      isSelected 
                        ? 'border-white bg-white/20' 
                        : 'border-gray-300'
                    }`}>
                      {isSelected && <span className="text-white text-xs">✓</span>}
                    </span>
                    <span>{option}</span>
                  </div>
                </button>
              )
            })}
          </div>

          {/* Stats */}
          {selectedAnswers[question.id] !== undefined && (
            <div className="mt-4 pt-4 border-t border-gray-100">
              <div className="flex items-center justify-between text-xs text-gray-500">
                <span className="flex items-center gap-1">
                  <span>👥</span>
                  <span>1,234 kişi cevapladı</span>
                </span>
                <span className="flex items-center gap-1">
                  <span>⭐</span>
                  <span>+10 XP kazandın!</span>
                </span>
              </div>
            </div>
          )}
        </div>
        )
      })}


    </div>
  )
}


