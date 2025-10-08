'use client'
import { useState, useRef, useEffect } from 'react'

interface PodcastPlayerProps {
  podcastId: string
  title: string
  coverImage: string
  audioUrl: string
  duration: number
}

export default function PodcastPlayer({ 
  podcastId, 
  title, 
  coverImage, 
  audioUrl, 
  duration 
}: PodcastPlayerProps) {
  const [isPlaying, setIsPlaying] = useState(false)
  const [currentTime, setCurrentTime] = useState(0)
  const [totalDuration, setTotalDuration] = useState(duration)
  const [volume, setVolume] = useState(1)
  const [playbackRate, setPlaybackRate] = useState(1)
  const audioRef = useRef<HTMLAudioElement>(null)

  // Play/Pause toggle
  const togglePlayPause = () => {
    if (audioRef.current) {
      if (isPlaying) {
        audioRef.current.pause()
      } else {
        audioRef.current.play()
      }
      setIsPlaying(!isPlaying)
    }
  }

  // Zaman güncelleme
  useEffect(() => {
    const audio = audioRef.current
    if (!audio) return

    const updateTime = () => setCurrentTime(audio.currentTime)
    const updateDuration = () => setTotalDuration(audio.duration)
    const handleEnded = () => setIsPlaying(false)

    audio.addEventListener('timeupdate', updateTime)
    audio.addEventListener('loadedmetadata', updateDuration)
    audio.addEventListener('ended', handleEnded)

    return () => {
      audio.removeEventListener('timeupdate', updateTime)
      audio.removeEventListener('loadedmetadata', updateDuration)
      audio.removeEventListener('ended', handleEnded)
    }
  }, [])

  // Progress bar tıklama
  const handleProgressClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!audioRef.current) return
    
    const rect = e.currentTarget.getBoundingClientRect()
    const x = e.clientX - rect.left
    const percentage = x / rect.width
    const newTime = percentage * totalDuration
    
    audioRef.current.currentTime = newTime
    setCurrentTime(newTime)
  }

  // Volume değiştir
  const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newVolume = parseFloat(e.target.value)
    setVolume(newVolume)
    if (audioRef.current) {
      audioRef.current.volume = newVolume
    }
  }

  // Playback rate değiştir
  const changePlaybackRate = () => {
    const rates = [0.75, 1, 1.25, 1.5, 2]
    const currentIndex = rates.indexOf(playbackRate)
    const nextIndex = (currentIndex + 1) % rates.length
    const newRate = rates[nextIndex]
    
    setPlaybackRate(newRate)
    if (audioRef.current) {
      audioRef.current.playbackRate = newRate
    }
  }

  // Zaman formatlama
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  return (
    <div className="w-full max-w-2xl mx-auto bg-gradient-to-br from-gray-900 to-black rounded-3xl shadow-2xl overflow-hidden">
      {/* Hidden Audio Element */}
      <audio ref={audioRef} src={audioUrl} />

      {/* Cover Image with Blur */}
      <div className="relative h-64 overflow-hidden">
        <img 
          src={coverImage} 
          alt={title}
          className="absolute inset-0 w-full h-full object-cover blur-2xl brightness-50 scale-110"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/50 to-transparent" />
        
        {/* Play/Pause Button Overlay */}
        <div className="absolute inset-0 flex items-center justify-center">
          <button
            onClick={togglePlayPause}
            className="w-24 h-24 rounded-full bg-white/20 backdrop-blur-md hover:bg-white/30 flex items-center justify-center transition-all duration-300 hover:scale-110 border-4 border-white/30"
          >
            {isPlaying ? (
              <span className="text-6xl text-white">⏸️</span>
            ) : (
              <span className="text-6xl text-white ml-2">▶️</span>
            )}
          </button>
        </div>
      </div>

      {/* Player Controls */}
      <div className="p-6 space-y-4">
        {/* Title */}
        <div>
          <h3 className="text-xl font-bold text-white mb-1">🎙️ {title}</h3>
          <p className="text-sm text-gray-400">Podcast · AA Eksen</p>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <div 
            className="h-2 bg-gray-700 rounded-full cursor-pointer group"
            onClick={handleProgressClick}
          >
            <div 
              className="h-full bg-gradient-to-r from-blue-500 to-purple-500 rounded-full relative group-hover:h-3 transition-all duration-200"
              style={{ width: `${(currentTime / totalDuration) * 100}%` }}
            >
              <div className="absolute right-0 top-1/2 -translate-y-1/2 w-4 h-4 bg-white rounded-full shadow-lg opacity-0 group-hover:opacity-100 transition-opacity" />
            </div>
          </div>
          
          {/* Time Display */}
          <div className="flex justify-between text-sm text-gray-400">
            <span>{formatTime(currentTime)}</span>
            <span>{formatTime(totalDuration)}</span>
          </div>
        </div>

        {/* Bottom Controls */}
        <div className="flex items-center justify-between">
          {/* Volume */}
          <div className="flex items-center gap-2">
            <span className="text-xl">🔊</span>
            <input
              type="range"
              min="0"
              max="1"
              step="0.1"
              value={volume}
              onChange={handleVolumeChange}
              className="w-24 h-1 bg-gray-700 rounded-full appearance-none cursor-pointer"
            />
          </div>

          {/* Playback Speed */}
          <button
            onClick={changePlaybackRate}
            className="px-4 py-2 bg-gray-800 hover:bg-gray-700 rounded-full text-sm font-bold text-white transition-colors"
          >
            ⚡ {playbackRate}x
          </button>
        </div>
      </div>
    </div>
  )
}