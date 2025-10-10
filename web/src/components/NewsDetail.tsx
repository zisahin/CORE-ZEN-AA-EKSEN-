'use client'

import { useState, useEffect } from 'react'
import { newsService } from '@/services/newsService'
import { NewsItem } from '@/types/firestore'
import NewsQuiz from './NewsQuiz'
import aiService from '@/services/aiService'

interface NewsDetailProps {
  newsId: string
  onClose: () => void
}

export default function NewsDetail({ newsId, onClose }: NewsDetailProps) {
  const [newsData, setNewsData] = useState<NewsItem | null>(null)
  const [loading, setLoading] = useState(true)
  const [isLiked, setIsLiked] = useState(false)
  const [showBubbleMenu, setShowBubbleMenu] = useState(true)
  const [showSummary, setShowSummary] = useState(false)
  const [isPlaying, setIsPlaying] = useState(false)
  const [showQuiz, setShowQuiz] = useState(false)
  const [aiSummary, setAiSummary] = useState<string>('')
  const [summaryLoading, setSummaryLoading] = useState(false)

  // Firebase'den haber detayını çek
  useEffect(() => {
    const loadNewsDetail = async () => {
      setLoading(true)
      try {
        const news = await newsService.getNewsById(newsId)
        if (news) {
          setNewsData(news)
          console.log('✅ Haber detayı yüklendi:', news.title)
        } else {
          console.error('❌ Haber bulunamadı:', newsId)
        }
      } catch (error) {
        console.error('❌ Haber detayı yüklenirken hata:', error)
      } finally {
        setLoading(false)
      }
    }

    loadNewsDetail()
  }, [newsId])

  const handleLike = () => {
    setIsLiked(!isLiked)
  }

  const handleSummarize = async () => {
    if (!newsData) return
    
    setShowSummary(true)
    setShowBubbleMenu(false)
    
    // Eğer daha önce özet çektiyse tekrar çekme
    if (aiSummary) return
    
    try {
      setSummaryLoading(true)
      const summary = await aiService.summarizeNews(
        newsData.content,
        newsData.title,
        newsData.category
      )
      setAiSummary(summary)
      console.log('✅ AI özeti oluşturuldu')
    } catch (error) {
      console.error('❌ Özet oluşturulamadı:', error)
      setAiSummary('Özet oluşturulurken bir hata oluştu. Lütfen daha sonra tekrar deneyin.')
    } finally {
      setSummaryLoading(false)
    }
  }

  const handlePlayAudio = () => {
    if (!newsData) return
    
    if (isPlaying) {
      speechSynthesis.cancel()
      setIsPlaying(false)
    } else {
      const utterance = new SpeechSynthesisUtterance(newsData.content)
      utterance.lang = 'tr-TR'
      utterance.rate = 0.9
      utterance.onend = () => setIsPlaying(false)
      speechSynthesis.speak(utterance)
      setIsPlaying(true)
    }
    setShowBubbleMenu(false)
  }

  const handleQuiz = () => {
    setShowQuiz(true)
    setShowBubbleMenu(false)
  }

  // Loading state
  if (loading) {
    return (
      <div className="fixed inset-0 z-50 bg-white flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-brand-blue mx-auto mb-4"></div>
          <p className="text-gray-600">Haber yükleniyor...</p>
        </div>
      </div>
    )
  }

  // Haber bulunamadı
  if (!newsData) {
    return (
      <div className="fixed inset-0 z-50 bg-white flex items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-gray-800 mb-4">❌ Haber bulunamadı</p>
          <button
            onClick={onClose}
            className="bg-brand-blue text-white px-6 py-2 rounded-lg hover:bg-blue-700"
          >
            Geri Dön
          </button>
        </div>
      </div>
    )
  }

  // Hesaplanan değerler
  const readTime = Math.ceil(newsData.content.length / 1000)
  const publishDate = newsData.publishedAt.toDate()

  return (
    <div className="fixed inset-0 z-50 bg-white overflow-y-auto">
      {/* Header */}
      <div className="sticky top-0 bg-white/95 backdrop-blur border-b border-gray-200 z-10">
        <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors"
          >
            ←
          </button>
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1 text-sm text-gray-500">
              <img src="/images/okuma.png" alt="Görüntüleme" className="w-5 h-5" />
              <span>{newsData.viewCount.toLocaleString()} görüntüleme</span>
            </div>
            <button className="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors">
              <img src="/images/share.png" alt="Paylaş" className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* Content */}
      <article className="max-w-4xl mx-auto px-4 py-6">
        {/* Category & Meta */}
        <div className="flex items-center flex-wrap gap-3 mb-4">
          <span className="bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
            {newsData.category}
          </span>
          {newsData.verified && (
            <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-sm font-medium flex items-center gap-1">
              ✓ Doğrulanmış
            </span>
          )}
          {newsData.breaking && (
            <span className="bg-red-100 text-red-700 px-3 py-1 rounded-full text-sm font-medium animate-pulse">
              🔴 Manşet
            </span>
          )}
          <span className="text-gray-500 text-sm">
            {publishDate.toLocaleDateString('tr-TR', { 
              day: 'numeric', 
              month: 'long', 
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })}
          </span>
          <div className="flex items-center gap-1 text-gray-500 text-sm">
            <img src="/images/kitap.png" alt="Okuma süresi" className="w-4 h-4" />
            <span>{readTime} dk okuma</span>
          </div>
          {newsData.location && (
            <div className="flex items-center gap-1 text-gray-500 text-sm">
              📍 {newsData.location}
            </div>
          )}
        </div>

        {/* Title */}
        <h1 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 leading-tight">
          {newsData.title}
        </h1>

        {/* Author */}
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 bg-brand-blue rounded-full flex items-center justify-center">
            <span className="text-white font-bold text-sm">
              {newsData.author.charAt(0)}
            </span>
          </div>
          <div>
            <div className="font-medium text-gray-900">{newsData.author}</div>
            <div className="text-sm text-gray-500">Anadolu Ajansı</div>
          </div>
        </div>

        {/* Main Image */}
        {newsData.imageUrl && (
          <div className="relative mb-8">
            <img
              src={newsData.imageUrl}
              alt={newsData.title}
              className="w-full h-64 md:h-96 object-cover rounded-2xl"
            />
            {newsData.imageCaption && (
              <p className="text-sm text-gray-600 mt-2 italic text-center">
                {newsData.imageCaption}
              </p>
            )}
            <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-2xl"></div>
          </div>
        )}

        {/* Content */}
        <div className="prose prose-lg max-w-none mb-8">
          {newsData.content.split('\n\n').map((paragraph, index) => (
            <p key={index} className="mb-4 text-gray-700 leading-relaxed">
              {paragraph}
            </p>
          ))}
        </div>

        {/* News URL Link */}
        {newsData.newsUrl && (
          <div className="mb-8 p-4 bg-blue-50 rounded-xl border border-blue-200">
            <p className="text-sm text-gray-600 mb-2">📰 Orijinal Haber:</p>
            <a 
              href={newsData.newsUrl} 
              target="_blank" 
              rel="noopener noreferrer"
              className="text-brand-blue hover:underline text-sm break-all"
            >
              {newsData.newsUrl}
            </a>
          </div>
        )}

        {/* Stats */}
        <div className="flex items-center justify-between py-4 border-t border-gray-200 mb-20">
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2">
              <button
                onClick={handleLike}
                className={`flex items-center gap-2 px-4 py-2 rounded-full transition-colors ${
                  isLiked 
                    ? 'bg-red-50 text-red-600' 
                    : 'bg-gray-100 text-gray-600 hover:bg-red-50 hover:text-red-600'
                }`}
              >
                <img src={isLiked ? "/images/like-filled.png" : "/images/like-empty.png"} alt="Like" className="w-5 h-5" />
                <span className="font-medium">
                  {(newsData.likeCount + (isLiked ? 1 : 0)).toLocaleString()}
                </span>
              </button>
            </div>
            <div className="flex items-center gap-2 text-gray-600">
              <span className="text-sm">👁️ {newsData.viewCount.toLocaleString()} görüntülenme</span>
            </div>
            <div className="flex items-center gap-2 text-gray-600">
              <span className="text-sm">💫 +{newsData.xpPoints} XP</span>
            </div>
            {newsData.shareCount > 0 && (
              <div className="flex items-center gap-2 text-gray-600">
                <span className="text-sm">🔗 {newsData.shareCount} paylaşım</span>
              </div>
            )}
          </div>
        </div>
      </article>

      {/* Floating Action Bubble */}
      <div className="fixed bottom-6 left-1/2 transform -translate-x-1/2 z-50">
        <div className="relative">
          {/* Main Bubble Button */}
          <button
            onClick={() => setShowBubbleMenu(!showBubbleMenu)}
            className={`w-16 h-16 rounded-full bg-brand-blue text-white shadow-2xl hover:shadow-3xl transition-all duration-300 flex items-center justify-center ${
              showBubbleMenu ? 'rotate-45' : 'hover:scale-110'
            }`}
          >
            <span className="text-2xl">+</span>
          </button>

          {/* Bubble Menu */}
          {showBubbleMenu && (
            <>
              {/* Left Buttons */}
              <div className="absolute top-2 right-20 flex flex-row-reverse gap-3">
                <button
                  onClick={handleSummarize}
                  className="w-12 h-12 rounded-full bg-brand-blue text-white hover:bg-blue-700 transition-all flex items-center justify-center animate-slide-out-left"
                  style={{ animationDelay: '0.2s' }}
                >
                  <img src="/images/ai-assistant.png" alt="AI Summary" className="w-6 h-6 rounded-full" />
                </button>
                <button
                  onClick={() => {setIsLiked(!isLiked); setShowBubbleMenu(false)}}
                  className="w-12 h-12 rounded-full bg-brand-blue text-white hover:bg-blue-700 transition-all flex items-center justify-center animate-slide-out-left"
                  style={{ animationDelay: '0.1s' }}
                >
                  <img src={isLiked ? "/images/like-filled-white.png" : "/images/like-empty-white.png"} 
                  alt="Favorite" 
                  className="w-6 h-6" 
                  />
                </button>
              </div>

              {/* Right Buttons */}
              <div className="absolute top-2 left-20 flex flex-row gap-3">
                <button
                  onClick={handlePlayAudio}
                  className="w-12 h-12 rounded-full bg-brand-blue text-white hover:bg-blue-700 transition-all flex items-center justify-center animate-slide-out-right"
                  style={{ animationDelay: '0.1s' }}
                >
                  <img src="/images/voice.png" alt="Voice" className={`w-6 h-6 ${isPlaying ? 'opacity-75' : ''}`} />
                </button>
                <button
                  onClick={handleQuiz}
                  className="w-12 h-12 rounded-full bg-brand-blue text-white hover:bg-blue-700 transition-all flex items-center justify-center animate-slide-out-right"
                  style={{ animationDelay: '0.2s' }}
                >
                  <img src="/images/quiz.png" alt="Quiz" className="w-6 h-6" />
                </button>
              </div>
            </>
          )}
        </div>
      </div>


      

      {/* AI Summary Modal */}
      {showSummary && newsData && (
        <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl p-6 max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <img src="/images/ai-assistant.png" alt="AI" className="w-8 h-8 rounded-full" />
                <h3 className="text-xl font-bold text-gray-800">AI ile Haber Özeti</h3>
              </div>
              <button
                onClick={() => setShowSummary(false)}
                className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center"
              >
                ✕
              </button>
            </div>
            
            {summaryLoading ? (
              <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-8 rounded-xl text-center">
                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mb-4"></div>
                <p className="text-gray-700">AI özet oluşturuyor...</p>
              </div>
            ) : (
              <>
                <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-4 rounded-xl mb-4">
                  <h4 className="font-semibold text-gray-800 mb-2 flex items-center gap-2">
                    <span>📝</span>
                    <span>Özet:</span>
                  </h4>
                  <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                    {aiSummary || 'Özet oluşturulurken bir hata oluştu.'}
                  </p>
                </div>
                
                <div className="flex gap-3">
                  <button 
                    onClick={() => setShowSummary(false)}
                    className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-xl font-medium hover:bg-gray-200 transition-colors"
                  >
                    Tamam
                  </button>
                  <button 
                    onClick={() => {
                      navigator.share?.({
                        title: newsData.title,
                        text: `${newsData.title}\n\n${aiSummary}\n\n${newsData.newsUrl}`
                      })
                    }}
                    className="flex-1 bg-brand-blue text-white py-3 rounded-xl font-medium hover:bg-blue-700 transition-colors"
                  >
                    📤 Paylaş
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* Quiz Modal */}
      {showQuiz && newsData && (
        <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="relative max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <button
              onClick={() => setShowQuiz(false)}
              className="absolute top-4 right-4 z-10 w-10 h-10 rounded-full bg-white shadow-lg hover:bg-gray-100 flex items-center justify-center transition-colors"
            >
              ✕
            </button>
            <NewsQuiz newsId={newsData.id} category={newsData.category} />
          </div>
        </div>
      )}

      {/* CSS Animations */}
      <style jsx>{`
        @keyframes slide-out-left {
          from {
            opacity: 0;
            transform: translateX(20px);
          }
          to {
            opacity: 1;
            transform: translateX(0);
          }
        }
        
        @keyframes slide-out-right {
          from {
            opacity: 0;
            transform: translateX(-20px);
          }
          to {
            opacity: 1;
            transform: translateX(0);
          }
        }
        
        .animate-slide-out-left {
          animation: slide-out-left 0.3s ease-out forwards;
        }

        .animate-slide-out-right {
          animation: slide-out-right 0.3s ease-out forwards;
        }
      `}</style>
    </div>
  )
}