'use client'
import { useState } from 'react'

export default function NewsInteraction({ newsId = 1 }) {
  const [likes, setLikes] = useState(Math.floor(Math.random() * 100) + 20)
  const [dislikes, setDislikes] = useState(Math.floor(Math.random() * 10) + 2)
  const [userVote, setUserVote] = useState(null)

  const handleLike = () => {
    if (userVote === 'like') {
      setLikes(likes - 1)
      setUserVote(null)
    } else {
      if (userVote === 'dislike') setDislikes(dislikes - 1)
      setLikes(likes + 1)
      setUserVote('like')
    }
  }

  const handleDislike = () => {
    if (userVote === 'dislike') {
      setDislikes(dislikes - 1)
      setUserVote(null)
    } else {
      if (userVote === 'like') setLikes(likes - 1)
      setDislikes(dislikes + 1)
      setUserVote('dislike')
    }
  }

  return (
    <div className="flex items-center space-x-4">
      {/* Like Button */}
      <button 
        onClick={handleLike}
        className={`flex items-center space-x-2 transition-all hover:scale-105 ${
          userVote === 'like' 
            ? 'text-green-600' 
            : 'text-gray-500 hover:text-green-600'
        }`}
      >
        <img 
          src="/images/like-icon.png" 
          alt="Like" 
          className={`w-6 h-6 transition-all ${
            userVote === 'like' ? 'opacity-100' : 'opacity-70 hover:opacity-100'
          }`}
        />
        <span className="text-sm font-medium">{likes}</span>
      </button>

      {/* Dislike Button */}
      <button 
        onClick={handleDislike}
        className={`flex items-center space-x-2 transition-all hover:scale-105 ${
          userVote === 'dislike' 
            ? 'text-red-600' 
            : 'text-gray-500 hover:text-red-600'
        }`}
      >
        <img 
          src="/images/dislike-icon.png" 
          alt="Dislike" 
          className={`w-6 h-6 transition-all ${
            userVote === 'dislike' ? 'opacity-100' : 'opacity-70 hover:opacity-100'
          }`}
        />
        <span className="text-sm font-medium">{dislikes}</span>
      </button>

      {/* Paylaş Button */}
      <button className="flex items-center space-x-2 text-gray-600 hover:text-blue-800 transition-all hover:scale-105">
  <img 
    src="/images/share-icon.png" 
    alt="Paylaş" 
    className="w-6 h-6 opacity-70 hover:opacity-100 transition-all"
  />
  <span className="text-sm font-medium">Paylaş</span>
      </button>
    </div>
  )
}