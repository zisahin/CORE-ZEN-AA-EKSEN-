'use client'

import { useState, useEffect, useRef } from 'react'
import aiService, { type ChatMessage, type QuickPrompt } from '@/services/aiService'

interface Message {
  id: string
  text: string
  isUser: boolean
  timestamp: Date
}

interface AIChatProps {
  isOpen: boolean
  onClose: () => void
}

const defaultQuickPrompts: QuickPrompt[] = [
  { title: "Günün Özeti", prompt: "Bugünkü önemli haberleri özetle", category: "daily" },
  { title: "Haftalık Ekonomi", prompt: "Bu haftaki ekonomi haberlerini özetle", category: "weekly" },
  { title: "Haftalık Siyaset", prompt: "Bu haftaki siyasi gelişmeleri özetle", category: "weekly" },
  { title: "Genel Haberler", prompt: "Güncel önemli haberleri anlat", category: "general" }
]

export default function AIChat({ isOpen, onClose }: AIChatProps) {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Merhaba! Ben AA AI asistanınız. Size nasıl yardımcı olabilirim? Haber özetleri, analiz ve daha fazlası için buradayım! 🤖',
      isUser: false,
      timestamp: new Date()
    }
  ])
  const [inputText, setInputText] = useState('')
  const [isTyping, setIsTyping] = useState(false)
  const [isSpeaking, setIsSpeaking] = useState(false)
  const [quickPrompts, setQuickPrompts] = useState<QuickPrompt[]>(defaultQuickPrompts)
  const [isServiceOnline, setIsServiceOnline] = useState(true)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // Auto scroll to bottom
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  // Handle ESC key to close
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleEsc)
    return () => document.removeEventListener('keydown', handleEsc)
  }, [onClose])

  // Hızlı önerileri ve servis durumunu yükle
  useEffect(() => {
    const loadSuggestions = async () => {
      try {
        const isOnline = await aiService.healthCheck()
        setIsServiceOnline(isOnline)
        
        if (isOnline) {
          const suggestions = await aiService.getQuickSuggestions()
          setQuickPrompts(suggestions)
        }
      } catch (error) {
        console.error('Öneriler yüklenemedi:', error)
        setIsServiceOnline(false)
      }
    }
    
    if (isOpen) {
      loadSuggestions()
    }
  }, [isOpen])

  const sendMessage = async (text: string) => {
    if (!text.trim()) return

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      text: text.trim(),
      isUser: true,
      timestamp: new Date()
    }
    setMessages(prev => [...prev, userMessage])
    setInputText('')
    setIsTyping(true)

    try {
      // Gerçek AI servisi çağrısı
      const conversationHistory: ChatMessage[] = messages.map(msg => ({
        text: msg.text,
        isUser: msg.isUser
      }))
      
      const aiResponseText = await aiService.sendChatMessage(text.trim(), conversationHistory)
      
      const aiResponse: Message = {
        id: (Date.now() + 1).toString(),
        text: aiResponseText,
        isUser: false,
        timestamp: new Date()
      }
      setMessages(prev => [...prev, aiResponse])
      setIsServiceOnline(true)
    } catch (error) {
      console.error('AI yanıt hatası:', error)
      
      // Hata durumunda kullanıcıya bilgi ver
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: '😔 Üzgünüm, şu anda AI servisine ulaşamıyorum. Lütfen:\n\n• AI servisinin çalıştığından emin olun (http://localhost:3001)\n• İnternet bağlantınızı kontrol edin\n• Birkaç saniye sonra tekrar deneyin\n\nSorun devam ederse lütfen sistem yöneticisiyle iletişime geçin.',
        isUser: false,
        timestamp: new Date()
      }
      setMessages(prev => [...prev, errorMessage])
      setIsServiceOnline(false)
    } finally {
      setIsTyping(false)
    }
  }

  const handleQuickPrompt = (prompt: string) => {
    sendMessage(prompt)
  }

  const toggleTTS = (message: Message) => {
    if (isSpeaking) {
      speechSynthesis.cancel()
      setIsSpeaking(false)
    } else {
      const utterance = new SpeechSynthesisUtterance(message.text)
      utterance.lang = 'tr-TR'
      utterance.rate = 0.9
      utterance.onend = () => setIsSpeaking(false)
      speechSynthesis.speak(utterance)
      setIsSpeaking(true)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />
      
      {/* Modal */}
      <div className="relative w-full max-w-4xl h-full max-h-[90vh] mx-4 bg-white rounded-2xl shadow-2xl flex flex-col overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b border-gray-200 bg-gradient-to-r from-brand-blue to-purple-600">
          <div className="flex items-center gap-3">
            <div className="relative">
              <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-white">
                <img src="/images/ai-assistant.png" alt="AI" className="w-full h-full object-cover" />
              </div>
              {/* Servis durumu göstergesi */}
              <div className={`absolute -bottom-1 -right-1 w-4 h-4 rounded-full border-2 border-white ${isServiceOnline ? 'bg-green-500' : 'bg-red-500'}`} 
                   title={isServiceOnline ? 'AI Servisi Aktif' : 'AI Servisi Çevrimdışı'}>
              </div>
            </div>
            <div>
              <h2 className="text-lg font-bold text-white">AA AI Asistan</h2>
              <p className="text-sm text-white/80">
                {isServiceOnline ? 'Her zaman buradayım!' : 'Servis çevrimdışı'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center text-white transition-colors"
          >
            ✕
          </button>
        </div>

        {/* Quick Prompts */}
        <div className="p-4 border-b border-gray-100 bg-gray-50">
          <div className="flex flex-wrap gap-2">
            {quickPrompts.map((prompt, index) => (
              <button
                key={index}
                onClick={() => handleQuickPrompt(prompt.prompt)}
                className="px-3 py-1.5 bg-white border border-gray-200 rounded-full text-sm hover:bg-brand-blue hover:text-white transition-colors"
              >
                {prompt.title}
              </button>
            ))}
          </div>
        </div>

        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex gap-3 ${message.isUser ? 'justify-end' : 'justify-start'}`}
            >
              {!message.isUser && (
                <div className="w-8 h-8 rounded-full overflow-hidden flex-shrink-0">
                  <img src="/images/ai-assistant.png" alt="AI" className="w-full h-full object-cover" />
                </div>
              )}
              
              <div className={`max-w-[70%] ${message.isUser ? 'order-first' : ''}`}>
                <div
                  className={`p-3 rounded-2xl ${
                    message.isUser
                      ? 'bg-brand-blue text-white ml-auto'
                      : 'bg-gray-100 text-gray-800'
                  }`}
                >
                  <p className="text-sm leading-relaxed whitespace-pre-line">
                    {message.text}
                  </p>
                </div>
                
                <div className="flex items-center gap-2 mt-1 text-xs text-gray-500">
                  <span>{message.timestamp.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}</span>
                  {!message.isUser && (
                    <button
                      onClick={() => toggleTTS(message)}
                      className="flex items-center gap-1 hover:text-brand-blue transition-colors"
                    >
                      <img 
                        src="/images/voice.png" 
                        alt={isSpeaking ? 'Durdur' : 'Dinle'} 
                        className={`w-4 h-4 ${isSpeaking ? 'opacity-50' : ''}`} 
                        />
                      <span>{isSpeaking ? 'Durdur' : 'Dinle'}</span>
                    </button>
                  )}
                </div>
              </div>

              {message.isUser && (
                <div className="w-8 h-8 rounded-full bg-brand-blue flex items-center justify-center flex-shrink-0">
                  <span className="text-white text-sm font-bold">K</span>
                </div>
              )}
            </div>
          ))}

          {/* Typing Indicator */}
          {isTyping && (
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full overflow-hidden">
                <img src="/images/ai-assistant.png" alt="AI" className="w-full h-full object-cover" />
              </div>
              <div className="bg-gray-100 rounded-2xl p-3">
                <div className="flex gap-1">
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce delay-100"></div>
                  <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce delay-200"></div>
                </div>
              </div>
            </div>
          )}
          
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="p-4 border-t border-gray-200 bg-white">
          <form 
            onSubmit={(e) => { e.preventDefault(); sendMessage(inputText); }}
            className="flex gap-3"
          >
            <input
              type="text"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder="Mesajınızı yazın..."
              className="flex-1 px-4 py-3 border border-gray-200 rounded-full focus:outline-none focus:border-brand-blue transition-colors"
              disabled={isTyping}
            />
            <button
              type="submit"
              disabled={!inputText.trim() || isTyping}
              className="px-6 py-3 bg-brand-blue text-white rounded-full hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Gönder
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}