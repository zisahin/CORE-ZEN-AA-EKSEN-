import React, { useState } from 'react'
import { Modal } from '@/components/ui/Modal'
import { Button } from '@/components/ui/Button'
import { FeedbackModalProps, FeedbackPayload } from '../types'
import * as LucideIcons from 'lucide-react'

export const FeedbackModal: React.FC<FeedbackModalProps> = ({
  isOpen,
  onClose,
  newsId,
  onSubmit
}) => {
  const [feedbackType, setFeedbackType] = useState<'like' | 'dislike' | 'report' | 'comment'>('like')
  const [comment, setComment] = useState('')
  const [rating, setRating] = useState(5)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    setIsSubmitting(true)
    
    const payload: FeedbackPayload = {
      newsId,
      type: feedbackType,
      comment: feedbackType === 'comment' ? comment : undefined,
      rating: feedbackType === 'like' ? rating : undefined
    }

    try {
      await onSubmit(payload)
      onClose()
      setComment('')
      setRating(5)
    } catch (error) {
      console.error('Feedback submission failed:', error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const feedbackOptions = [
    { type: 'like' as const, label: 'Like', icon: 'ThumbsUp', color: 'text-green-600' },
    { type: 'dislike' as const, label: 'Dislike', icon: 'ThumbsDown', color: 'text-red-600' },
    { type: 'report' as const, label: 'Report', icon: 'Flag', color: 'text-orange-600' },
    { type: 'comment' as const, label: 'Comment', icon: 'MessageCircle', color: 'text-blue-600' }
  ]

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Give Feedback">
      <div className="space-y-6">
        {/* Feedback Type Selection */}
        <div>
          <label className="block text-sm font-medium text-aa-gray-700 mb-3">
            What would you like to do?
          </label>
          <div className="grid grid-cols-2 gap-3">
            {feedbackOptions.map((option) => {
              const IconComponent = LucideIcons[option.icon as keyof typeof LucideIcons] as React.ComponentType<{
                className?: string
                size?: number
              }>
              
              return (
                <button
                  key={option.type}
                  onClick={() => setFeedbackType(option.type)}
                  className={`p-3 rounded-lg border-2 transition-colors ${
                    feedbackType === option.type
                      ? 'border-aa-accent bg-aa-accent/5'
                      : 'border-aa-gray-200 hover:border-aa-gray-300'
                  }`}
                >
                  <div className="flex items-center gap-2">
                    <IconComponent className={`h-5 w-5 ${option.color}`} />
                    <span className="text-sm font-medium">{option.label}</span>
                  </div>
                </button>
              )
            })}
          </div>
        </div>

        {/* Rating (for likes) */}
        {feedbackType === 'like' && (
          <div>
            <label className="block text-sm font-medium text-aa-gray-700 mb-2">
              Rating (1-5)
            </label>
            <div className="flex gap-1">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  onClick={() => setRating(star)}
                  className={`text-2xl transition-colors ${
                    star <= rating ? 'text-yellow-400' : 'text-aa-gray-300'
                  }`}
                >
                  ★
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Comment (for comments) */}
        {feedbackType === 'comment' && (
          <div>
            <label className="block text-sm font-medium text-aa-gray-700 mb-2">
              Your Comment
            </label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Share your thoughts..."
              className="w-full p-3 border border-aa-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-aa-accent focus:border-transparent"
              rows={4}
            />
          </div>
        )}

        {/* Report Reason (for reports) */}
        {feedbackType === 'report' && (
          <div>
            <label className="block text-sm font-medium text-aa-gray-700 mb-2">
              Reason for Report
            </label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Please explain why you're reporting this content..."
              className="w-full p-3 border border-aa-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-aa-accent focus:border-transparent"
              rows={3}
            />
          </div>
        )}

        {/* Submit Button */}
        <div className="flex gap-3 pt-4">
          <Button
            variant="secondary"
            onClick={onClose}
            className="flex-1"
          >
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            loading={isSubmitting}
            className="flex-1"
          >
            Submit Feedback
          </Button>
        </div>
      </div>
    </Modal>
  )
}



