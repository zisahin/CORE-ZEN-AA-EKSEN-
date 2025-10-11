'use client'

import { useState, useEffect } from 'react'
import { quizService } from '@/services/quizService'
import { QuizQuestion } from '@/types/firestore'
import aiService from '@/services/aiService'
import { newsService } from '@/services/newsService'

interface NewsQuizProps {
  newsId?: string
  category?: string
}

export default function NewsQuiz({ newsId, category }: NewsQuizProps) {
  const [questions, setQuestions] = useState<QuizQuestion[]>([])
  const [currentQuestion, setCurrentQuestion] = useState(0)
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null)
  const [showResult, setShowResult] = useState(false)
  const [score, setScore] = useState(0)
  const [answeredQuestions, setAnsweredQuestions] = useState<number[]>([])
  const [loading, setLoading] = useState(true)
  const [quizCompleted, setQuizCompleted] = useState(false)
  const [generatingQuiz, setGeneratingQuiz] = useState(false)

  useEffect(() => {
    loadQuiz()
  }, [newsId, category])

  const loadQuiz = async () => {
    try {
      setLoading(true)
      let data: QuizQuestion[] = []
      
      if (newsId) {
        // Habere özel quiz
        data = await quizService.getQuizByNewsId(newsId)
      } else if (category) {
        // Kategoriye özel quiz
        data = await quizService.getQuizzesByCategory(category)
      } else {
        // Random quiz
        data = await quizService.getRandomQuiz()
      }
      
      // Eğer Firebase'de quiz yoksa, AI ile oluştur
      if (data.length === 0 && newsId) {
        console.log('⚠️ Firebase\'de quiz yok, AI ile oluşturuluyor...')
        // Bu özellik şu an pasif - Firebase'e veri ekleyin
      }
      
      setQuestions(data)
      console.log(`✅ ${data.length} quiz sorusu Firebase'den yüklendi`)
    } catch (error) {
      console.error('❌ Quiz soruları yüklenemedi:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleAnswerSelect = (answerIndex: number) => {
    if (showResult) return
    
    setSelectedAnswer(answerIndex)
    setShowResult(true)

    // Doğru cevap kontrolü
    const currentQ = questions[currentQuestion]
    if (answerIndex === currentQ.correctAnswer) {
      setScore(score + currentQ.points)
    }
  }

  const handleNextQuestion = () => {
    setAnsweredQuestions([...answeredQuestions, currentQuestion])
    
    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1)
      setSelectedAnswer(null)
      setShowResult(false)
    } else {
      setQuizCompleted(true)
    }
  }

  const restartQuiz = () => {
    setCurrentQuestion(0)
    setSelectedAnswer(null)
    setShowResult(false)
    setScore(0)
    setAnsweredQuestions([])
    setQuizCompleted(false)
  }

  if (loading) {
    return (
      <div className="bg-white rounded-2xl shadow-lg p-6 border-2 border-blue-100">
        <div className="animate-pulse">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-12 h-12 bg-gray-200 rounded-full"></div>
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
    return (
      <div className="bg-white rounded-2xl shadow-lg p-8 border-2 border-blue-100 text-center">
        <div className="mb-4">
          <div className="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-5xl">📝</span>
          </div>
          <h3 className="font-bold text-xl text-gray-800 mb-2">Quiz Bulunamadı</h3>
          <p className="text-gray-600 mb-4">
            Bu haber için henüz quiz sorusu oluşturulmamış.
          </p>
          <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 text-left">
            <h4 className="font-semibold text-gray-800 mb-2">📌 Firebase'e Quiz Eklemek İçin:</h4>
            <ol className="text-sm text-gray-700 space-y-1 list-decimal list-inside">
              <li>Firebase Console açın</li>
              <li><code className="bg-gray-100 px-1 rounded">quiz_questions</code> koleksiyonuna gidin</li>
              <li>Yeni döküman ekleyin</li>
              <li>Şu alanları doldurun:
                <ul className="ml-6 mt-1 space-y-0.5">
                  <li><code>newsId</code>: "{newsId || 'haber_id'}"</li>
                  <li><code>category</code>: "{category || 'Genel'}"</li>
                  <li><code>difficulty</code>: "easy", "medium", "hard", "very_hard"</li>
                  <li><code>question</code>: "Soru metni"</li>
                  <li><code>options</code>: ["A", "B", "C", "D"]</li>
                  <li><code>correctAnswer</code>: 0-3 arası (doğru şıkkın index'i)</li>
                  <li><code>explanation</code>: "Açıklama"</li>
                  <li><code>points</code>: 10-50 arası</li>
                </ul>
              </li>
            </ol>
          </div>
        </div>
      </div>
    )
  }

  if (quizCompleted) {
    const totalPoints = questions.reduce((sum, q) => sum + q.points, 0)
    const percentage = Math.round((score / totalPoints) * 100)
    
    return (
      <div className="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl shadow-lg p-6 text-white">
        <div className="text-center">
          <div className="w-24 h-24 bg-white/20 backdrop-blur rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-5xl">
              {percentage >= 75 ? '🏆' : percentage >= 50 ? '🎉' : '💪'}
            </span>
          </div>
          <h3 className="font-bold text-2xl mb-2">Quiz Tamamlandı!</h3>
          <div className="bg-white/20 backdrop-blur rounded-xl p-4 mb-4">
            <p className="text-4xl font-bold mb-2">{score}/{totalPoints}</p>
            <p className="text-sm text-white/90">%{percentage} başarı</p>
          </div>
          <p className="text-white/90 mb-4">
            {percentage >= 75 
              ? 'Mükemmel! Konuyu çok iyi biliyorsun! 🌟'
              : percentage >= 50
              ? 'İyi iş! Bilgini geliştirmeye devam et 📚'
              : 'Daha fazla okuyarak gelişebilirsin 💡'}
          </p>
          <div className="flex gap-3 justify-center">
            <button
              onClick={restartQuiz}
              className="bg-white text-blue-600 px-6 py-3 rounded-xl font-bold hover:bg-blue-50 transition-colors"
            >
              🔄 Tekrar Dene
            </button>
          </div>
        </div>
      </div>
    )
  }

  const currentQ = questions[currentQuestion]
  const isCorrect = selectedAnswer === currentQ.correctAnswer

  return (
    <div className="bg-white rounded-2xl shadow-lg border-2 border-blue-100 overflow-hidden">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-500 to-purple-600 p-4 text-white">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-white/20 backdrop-blur rounded-full flex items-center justify-center">
              <img src="/images/quiz.png" alt="Quiz" className="w-6 h-6 brightness-0 invert" />
            </div>
            <h3 className="font-bold text-lg">Haber Quizi</h3>
          </div>
          <div className="text-sm font-medium">
            {currentQuestion + 1}/{questions.length}
          </div>
        </div>
        
        {/* Progress Bar */}
        <div className="w-full bg-white/20 rounded-full h-2 overflow-hidden">
          <div 
            className="bg-white h-full transition-all duration-500"
            style={{ width: `${((currentQuestion + 1) / questions.length) * 100}%` }}
          />
        </div>
      </div>

      {/* Content */}
      <div className="p-6">
        {/* Difficulty Badge */}
        <div className="flex items-center gap-2 mb-4">
          <span className={`px-3 py-1 rounded-full text-xs font-bold ${
            currentQ.difficulty === 'easy' ? 'bg-green-100 text-green-700' :
            currentQ.difficulty === 'medium' ? 'bg-yellow-100 text-yellow-700' :
            currentQ.difficulty === 'hard' ? 'bg-orange-100 text-orange-700' :
            'bg-red-100 text-red-700'
          }`}>
            {currentQ.difficulty === 'easy' ? '🟢 Kolay' :
             currentQ.difficulty === 'medium' ? '🟡 Orta' :
             currentQ.difficulty === 'hard' ? '🟠 Zor' :
             '🔴 Çok Zor'}
          </span>
          <span className="text-xs text-gray-500">+{currentQ.points} Puan</span>
        </div>

        {/* Question */}
        <h4 className="text-lg font-bold text-gray-800 mb-4">{currentQ.question}</h4>

        {/* Options */}
        <div className="space-y-3 mb-4">
          {currentQ.options.map((option, index) => {
            const isSelected = selectedAnswer === index
            const isCorrectAnswer = index === currentQ.correctAnswer
            const showCorrect = showResult && isCorrectAnswer
            const showWrong = showResult && isSelected && !isCorrect

            return (
              <button
                key={index}
                onClick={() => handleAnswerSelect(index)}
                disabled={showResult}
                className={`w-full text-left px-4 py-3 rounded-xl font-medium transition-all duration-200 ${
                  showCorrect
                    ? 'bg-green-500 text-white shadow-lg'
                    : showWrong
                    ? 'bg-red-500 text-white shadow-lg'
                    : isSelected
                    ? 'bg-blue-500 text-white shadow-lg'
                    : 'bg-gray-100 text-gray-700 hover:bg-blue-50 hover:text-blue-700'
                }`}
              >
                <div className="flex items-center gap-3">
                  <span className={`w-6 h-6 rounded-full border-2 flex items-center justify-center ${
                    showCorrect || (isSelected && !showWrong)
                      ? 'border-white bg-white/20'
                      : 'border-gray-400'
                  }`}>
                    {showCorrect && '✓'}
                    {showWrong && '✗'}
                  </span>
                  <span>{option}</span>
                </div>
              </button>
            )
          })}
        </div>

        {/* Explanation */}
        {showResult && currentQ.explanation && (
          <div className={`p-4 rounded-xl mb-4 ${
            isCorrect ? 'bg-green-50 border border-green-200' : 'bg-blue-50 border border-blue-200'
          }`}>
            <p className="text-sm font-medium text-gray-800 mb-1">
              {isCorrect ? '✅ Doğru!' : '💡 Açıklama:'}
            </p>
            <p className="text-sm text-gray-700">{currentQ.explanation}</p>
          </div>
        )}

        {/* Source */}
        {currentQ.sourceNewsTitle && (
          <p className="text-xs text-gray-500 mb-4">
            📰 Kaynak: {currentQ.sourceNewsTitle}
          </p>
        )}

        {/* Next Button */}
        {showResult && (
          <button
            onClick={handleNextQuestion}
            className="w-full bg-gradient-to-r from-blue-500 to-purple-600 text-white px-6 py-3 rounded-xl font-bold hover:shadow-xl transition-all"
          >
            {currentQuestion < questions.length - 1 ? 'Sonraki Soru →' : 'Sonuçları Gör 🎯'}
          </button>
        )}

        {/* Score */}
        <div className="mt-4 pt-4 border-t border-gray-100 flex items-center justify-between text-sm">
          <span className="text-gray-600">Toplam Puan</span>
          <span className="font-bold text-blue-600">{score} / {questions.reduce((sum, q) => sum + q.points, 0)}</span>
        </div>
      </div>
    </div>
  )
}

