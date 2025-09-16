# News Module

The news module handles all news-related functionality including display, feedback, and content management.

## 📁 Structure

```
news/
├── components/
│   ├── NewsCard.tsx      # Individual news card component
│   ├── NewsList.tsx      # List of news cards
│   ├── TrendingNews.tsx  # Trending news section
│   └── FeedbackModal.tsx # Feedback submission modal
├── hooks/
│   └── useNews.ts        # Custom hook for news data
├── services/
│   └── api.ts           # API service for news operations
├── data.ts              # Mock data for development
├── types.ts             # TypeScript interfaces
└── index.ts             # Barrel exports
```

## 🎯 Features

- **News Cards**: Display news with image, title, excerpt, source, and timestamp
- **Feedback System**: Like, dislike, report, and comment functionality
- **Trending News**: Curated trending content section
- **Search & Filter**: Category-based filtering and search
- **Responsive Design**: Mobile-optimized layout

## 🔧 Usage

```tsx
import { NewsCard, TrendingNews, useNews } from '@/modules/news'

// Use news hook
const { news, loading, submitFeedback } = useNews({
  category: 'politics',
  page: 1
})

// Render trending news
<TrendingNews />

// Render individual news card
<NewsCard 
  news={newsItem} 
  onFeedback={submitFeedback} 
/>
```

## 📊 Data Flow

1. **Fetch**: `useNews` hook fetches data from `NewsApiService`
2. **Display**: Components render news cards with proper formatting
3. **Interaction**: Users can provide feedback through modal
4. **Submit**: Feedback is sent to `/api/feedback` endpoint

## 🧪 Testing

```bash
# Run news module tests
npm test tests/modules/news/

# Test specific component
npm test NewsCard.test.tsx
```

## 🔄 API Integration

The module integrates with the following API endpoints:

- `GET /api/news` - Fetch news list
- `GET /api/news/:id` - Fetch single news item
- `POST /api/feedback` - Submit feedback
- `GET /api/search` - Search news

## 📝 Placeholder Data

Mock data is provided in `data.ts` for development. Replace with real API calls in production.

## 🎨 Styling

News cards use TailwindCSS classes with custom AA design tokens:
- Card shadows and borders
- Category badges with color coding
- Responsive image handling
- Hover effects and transitions



