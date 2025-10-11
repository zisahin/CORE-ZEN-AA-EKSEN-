'use client'

import { useState } from 'react'
import { useTheme } from '@/context/ThemeContext'
import AIChat from './AIChat'

export default function AIBubble() {
  const { isGradient } = useTheme()
  const [isOpen, setIsOpen] = useState(false)

  return (
    <>
      {/* AI Bubble Button - Sağ Alt Köşe */}
      <div className="fixed bottom-6 right-6 z-50">
        <button
          onClick={() => setIsOpen(true)}
          className="relative group"
        >
          {/* AI Bubble Image */}
          <div className={`w-16 h-16 rounded-full overflow-hidden shadow-2xl hover:shadow-3xl transform hover:scale-110 transition-all duration-300 border-4 ${
            isGradient
              ? 'border-purple-500/50 shadow-purple-900/50'
              : 'border-white'
          }`}>
            <img 
              src="/images/ai-bubble.png" 
              alt="AI Asistan" 
              className="w-full h-full object-cover"
            />
          </div>
          
          {/* Pulse Animation */}
          <div className={`absolute inset-0 w-16 h-16 rounded-full opacity-30 animate-ping ${
            isGradient ? 'bg-purple-600' : 'bg-brand-blue'
          }`}></div>
          
          {/* Online Indicator */}
          <div className={`absolute -top-1 -right-1 w-5 h-5 bg-green-500 rounded-full border-2 flex items-center justify-center ${
            isGradient ? 'border-purple-900' : 'border-white'
          }`}>
            <div className="w-2 h-2 bg-white rounded-full animate-pulse"></div>
          </div>
          
          {/* Hover Tooltip */}
          <div className={`absolute bottom-full right-0 mb-2 px-3 py-1 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap ${
            isGradient
              ? 'bg-purple-900/90 backdrop-blur'
              : 'bg-black/80'
          }`}>
            AI Asistan ile sohbet et ✨
          </div>
        </button>
      </div>

      {/* AI Chat Modal */}
      {isOpen && (
        <AIChat 
          isOpen={isOpen} 
          onClose={() => setIsOpen(false)} 
        />
      )}
    </>
  )
}