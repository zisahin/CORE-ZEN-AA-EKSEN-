'use client'

import { useTheme } from '@/context/ThemeContext'

export default function ThemeToggle() {
  const { theme, toggleTheme, isGradient } = useTheme()

  return (
    <button
      onClick={toggleTheme}
      className={`relative flex items-center gap-2 px-4 py-2 rounded-xl font-medium text-sm transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-105 ${
        isGradient
          ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white hover:from-purple-700 hover:to-blue-700'
          : 'bg-brand-blue text-white hover:bg-blue-900'
      }`}
      title={isGradient ? 'Classic Mode\'a Geç' : 'Gradient Mode\'a Geç'}
    >
      {/* Icon */}
      <div className="relative w-5 h-5 flex items-center justify-center">
        {isGradient ? (
          // Gradient icon - sparkles
          <svg
            className="w-5 h-5 animate-pulse"
            fill="currentColor"
            viewBox="0 0 24 24"
          >
            <path d="M12 0L13.5 7.5L21 9L13.5 10.5L12 18L10.5 10.5L3 9L10.5 7.5L12 0Z" />
            <path d="M7 3L7.5 5.5L10 6L7.5 6.5L7 9L6.5 6.5L4 6L6.5 5.5L7 3Z" />
            <path d="M17 15L17.5 17.5L20 18L17.5 18.5L17 21L16.5 18.5L14 18L16.5 17.5L17 15Z" />
          </svg>
        ) : (
          // Classic icon - newspaper
          <svg
            className="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"
            />
          </svg>
        )}
      </div>

      {/* Text */}
      <span className="hidden sm:inline whitespace-nowrap">
        {isGradient ? 'Classic' : 'Gradient'}
      </span>

      {/* Animated indicator */}
      <div className={`absolute -top-1 -right-1 w-3 h-3 rounded-full ${
        isGradient ? 'bg-yellow-400' : 'bg-blue-300'
      } animate-ping opacity-75`}></div>
    </button>
  )
}

