import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { NewsCard } from '@/modules/news'
import { NewsItem } from '@/types'

const mockNews: NewsItem = {
  id: '1',
  title: 'Test News Title',
  content: 'Test news content',
  excerpt: 'Test excerpt',
  imageUrl: 'https://example.com/image.jpg',
  source: 'Test Source',
  category: 'Test Category',
  publishedAt: new Date(),
  author: 'Test Author',
  url: '/test',
  tags: ['test']
}

describe('NewsCard Component', () => {
  it('renders news title', () => {
    render(<NewsCard news={mockNews} />)
    expect(screen.getByText('Test News Title')).toBeInTheDocument()
  })

  it('renders news excerpt', () => {
    render(<NewsCard news={mockNews} />)
    expect(screen.getByText('Test excerpt')).toBeInTheDocument()
  })

  it('renders source and category', () => {
    render(<NewsCard news={mockNews} />)
    expect(screen.getByText('Test Source')).toBeInTheDocument()
    expect(screen.getByText('Test Category')).toBeInTheDocument()
  })

  it('opens feedback modal when feedback button is clicked', () => {
    render(<NewsCard news={mockNews} />)
    const feedbackButton = screen.getByText('Feedback')
    fireEvent.click(feedbackButton)
    
    // Check if modal content is rendered
    expect(screen.getByText('Give Feedback')).toBeInTheDocument()
  })

  it('calls onFeedback callback when feedback is submitted', () => {
    const mockOnFeedback = jest.fn()
    render(<NewsCard news={mockNews} onFeedback={mockOnFeedback} />)
    
    const feedbackButton = screen.getByText('Feedback')
    fireEvent.click(feedbackButton)
    
    // Submit feedback
    const submitButton = screen.getByText('Submit Feedback')
    fireEvent.click(submitButton)
    
    expect(mockOnFeedback).toHaveBeenCalled()
  })
})



