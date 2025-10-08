'use client'

import { useState } from 'react'

interface NewsDetailProps {
  newsId: string
  onClose: () => void
}

const mockNewsData = {
  id: '1',
  title: 'Yapay Zeka Teknolojileri Türkiye\'de Hızla Gelişiyor',
  content: `Türkiye'de yapay zeka teknolojileri alanında son dönemde önemli gelişmeler yaşanıyor. Özellikle teknoloji şirketlerinin bu alandaki yatırımları %150 oranında artış gösterdi.

Teknoloji Bakanı Mehmet Cahit Turhan, yaptığı açıklamada "Yapay zeka alanında Türkiye'nin bölgesel bir hub olma hedefimiz var. Bu kapsamda 2024 yılında 50 milyar TL'lik yatırım planımızı hayata geçiriyoruz" dedi.

Sektör temsilcileri, bu gelişmelerin Türkiye'nin teknoloji ihracatını önemli ölçüde artıracağını belirtiyor. Özellikle finans, sağlık ve eğitim sektörlerinde yapay zeka uygulamalarının yaygınlaştırılması planlanıyor.

İstanbul Teknik Üniversitesi'nden Prof. Dr. Ahmet Yılmaz, "Bu yatırımlar sayesinde gelecek 5 yıl içinde 100 bin yeni istihdam yaratılması bekleniyor" şeklinde konuştu.

Ayrıca, yapay zeka araştırma merkezlerinin sayısının da hızla artması dikkat çekiyor. Şu anda Türkiye'de 25 farklı üniversitede yapay zeka bölümleri bulunuyor.`,
  image: '/images/istanbul.jpg',
  category: 'Teknoloji',
  author: 'Mehmet Özkan',
  publishedAt: new Date('2024-01-15T10:30:00Z'),
  readTime: '4 dk',
  likes: 6700,
  comments: 12000,
  views: 45000
}

export default function NewsDetail({ newsId, onClose }: NewsDetailProps) {
  const [isLiked, setIsLiked] = useState(false)
  const [showBubbleMenu, setShowBubbleMenu] = useState(false)
  const [showSummary, setShowSummary] = useState(false)
  const [isPlaying, setIsPlaying] = useState(false)
  const [showQuiz, setShowQuiz] = useState(false)

  const handleLike = () => {
    setIsLiked(!isLiked)
  }

  const handleSummarize = () => {
    setShowSummary(true)
    setShowBubbleMenu(false)
  }

  const handlePlayAudio = () => {
    if (isPlaying) {
      speechSynthesis.cancel()
      setIsPlaying(false)
    } else {
      const utterance = new SpeechSynthesisUtterance(mockNewsData.content)
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
            <span className="text-sm text-gray-500">👁️ {mockNewsData.views.toLocaleString()} görüntüleme</span>
            <button className="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors">
              📤
            </button>
          </div>
        </div>
      </div>

      {/* Content */}
      <article className="max-w-4xl mx-auto px-4 py-6">
        {/* Category & Meta */}
        <div className="flex items-center gap-3 mb-4">
          <span className="bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
            {mockNewsData.category}
          </span>
          <span className="text-gray-500 text-sm">
            {mockNewsData.publishedAt.toLocaleDateString('tr-TR')}
          </span>
          <span className="text-gray-500 text-sm">📖 {mockNewsData.readTime} okuma</span>
        </div>

        {/* Title */}
        <h1 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 leading-tight">
          {mockNewsData.title}
        </h1>

        {/* Author */}
        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 bg-brand-blue rounded-full flex items-center justify-center">
            <span className="text-white font-bold text-sm">
              {mockNewsData.author.charAt(0)}
            </span>
          </div>
          <div>
            <div className="font-medium text-gray-900">{mockNewsData.author}</div>
            <div className="text-sm text-gray-500">Anadolu Ajansı Muhabiri</div>
          </div>
        </div>

        {/* Main Image */}
        <div className="relative mb-8">
          <img
            src={mockNewsData.image}
            alt={mockNewsData.title}
            className="w-full h-64 md:h-96 object-cover rounded-2xl"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent rounded-2xl"></div>
        </div>

        {/* Content */}
        <div className="prose prose-lg max-w-none mb-8">
          {mockNewsData.content.split('\n\n').map((paragraph, index) => (
            <p key={index} className="mb-4 text-gray-700 leading-relaxed">
              {paragraph}
            </p>
          ))}
        </div>

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
                <img src={isLiked ? "/images/like-filled.png" : "/images/like-empty.png"} 
                alt="Like" 
                className="w-5 h-5" />
                
                <span className="font-medium">
                  {(mockNewsData.likes + (isLiked ? 1 : 0)).toLocaleString()}
                </span>
              </button>
            </div>
            
            <div className="flex items-center gap-2 text-gray-600">
              <span className="text-lg">💬</span>
              <span className="font-medium">{mockNewsData.comments.toLocaleString()}</span>
            </div>
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
              {/* <div className="absolute top-2 right-20 flex flex-row-reverse gap-3">
                <button
                  onClick={handleSummarize}
                  className="w-12 h-12 rounded-full bg-purple-500 text-white hover:bg-purple-600 transition-all flex items-center justify-center animate-fadeInUp"
                  style={{ animationDelay: '0.2s' }}
                >
                  <img src="/images/ai-assistant.png" alt="AI Summary" className="w-6 h-6 rounded-full" />
                </button>
                <button
                  onClick={() => {setIsLiked(!isLiked); setShowBubbleMenu(false)}}
                  className="w-12 h-12 rounded-full bg-red-500 text-white hover:bg-red-600 transition-all flex items-center justify-center animate-fadeInUp"
                  style={{ animationDelay: '0.1s' }}
                >
                  <img src={isLiked ? "/images/like-filled.png" : "/images/like-empty.png"} 
                  alt="Favorite" 
                  className="w-6 h-6" 
                  />
                </button>
              </div> */}

              {/* Right Buttons */}
              {/* <div className="absolute top-2 left-20 flex flex-row gap-3">
                <button
                  onClick={handlePlayAudio}
                  className="w-12 h-12 rounded-full bg-blue-500 text-white hover:bg-blue-600 transition-all flex items-center justify-center animate-fadeInUp"
                  style={{ animationDelay: '0.1s' }}
                >
                  <img src="/images/voice.png" alt="Voice" className={`w-6 h-6 ${isPlaying ? 'opacity-75' : ''}`} />
                </button>
                <button
                  onClick={handleQuiz}
                  className="w-12 h-12 rounded-full bg-green-500 text-white hover:bg-green-600 transition-all flex items-center justify-center animate-fadeInUp"
                  style={{ animationDelay: '0.2s' }}
                >
                  <img src="/images/quiz.png" alt="Quiz" className="w-6 h-6" />
                </button>
              </div> */}
            </>
          )}
        </div>
      </div>

      {/* AI Summary Modal */}
      {showSummary && (
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
            
            <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-4 rounded-xl mb-4">
              <h4 className="font-semibold text-gray-800 mb-2">📝 Özet:</h4>
              <p className="text-gray-700 leading-relaxed mb-3">
                Türkiye'de yapay zeka teknolojileri alanında büyük gelişmeler yaşanıyor. Şirketlerin yatırımları %150 artarken, Teknoloji Bakanlığı 50 milyar TL'lik yatırım planı açıkladı. Bu gelişmelerin 100 bin yeni istihdam yaratması ve Türkiye'nin teknoloji ihracatını artırması bekleniyor.
              </p>
              
              <h4 className="font-semibold text-gray-800 mb-2">🔑 Anahtar Noktalar:</h4>
              <ul className="list-disc list-inside text-gray-700 space-y-1 text-sm">
                <li>Yapay zeka yatırımları %150 arttı</li>
                <li>50 milyar TL'lik yatırım planı</li>
                <li>100 bin yeni istihdam hedefi</li>
                <li>25 üniversitede yapay zeka bölümü</li>
              </ul>
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
                  const summary = "Türkiye'de yapay zeka teknolojileri alanında büyük gelişmeler yaşanıyor..."
                  navigator.share?.({
                    title: 'Haber Özeti',
                    text: summary
                  })
                }}
                className="flex-1 bg-brand-blue text-white py-3 rounded-xl font-medium hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
              >
                <img src="/images/share.png" alt="Paylaş" className="w-5 h-5" />
                Paylaş
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Quiz Modal */}
      {showQuiz && (
        <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl p-6 max-w-2xl w-full">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-2">
                <span className="text-2xl">🧠</span>
                <h3 className="text-xl font-bold text-gray-800">Haber Quiz'i</h3>
              </div>
              <button
                onClick={() => setShowQuiz(false)}
                className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
              <div className="bg-gradient-to-r from-green-50 to-blue-50 p-4 rounded-xl">
                <h4 className="font-semibold text-gray-800 mb-3">
                  Türkiye'de yapay zeka yatırımları ne kadar artış gösterdi?
                </h4>
                
                <div className="space-y-2">
                  {['%100', '%125', '%150', '%200'].map((option, index) => (
                    <button
                      key={index}
                      className="w-full text-left p-3 rounded-lg border border-gray-200 hover:border-brand-blue hover:bg-brand-blue/5 transition-colors"
                    >
                      {String.fromCharCode(65 + index)}) {option}
                    </button>
                  ))}
                </div>
              </div>
              
              <div className="flex gap-3">
                <button 
                  onClick={() => setShowQuiz(false)}
                  className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-xl font-medium hover:bg-gray-200 transition-colors"
                >
                  İptal
                </button>
                <button className="flex-1 bg-green-500 text-white py-3 rounded-xl font-medium hover:bg-green-600 transition-colors">
                  Cevabı Gönder
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* CSS Animations */}
      <style jsx>{`
        @keyframes fadeInUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
        
        .animate-fadeInUp {
          animation: fadeInUp 0.3s ease-out forwards;
        }
      `}</style>
    </div>
  )
}