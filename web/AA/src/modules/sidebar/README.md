# Sidebar Module

The sidebar module provides navigation, account information, and following functionality for the news portal.

## 📁 Structure

```
sidebar/
├── components/
│   ├── Sidebar.tsx           # Main sidebar component
│   ├── AccountHeader.tsx     # User account section
│   ├── NavigationItem.tsx    # Individual nav item
│   └── FollowingSection.tsx  # Following sources section
├── data.ts                   # Navigation and following data
├── types.ts                  # TypeScript interfaces
└── index.ts                  # Barrel exports
```

## 🎯 Features

- **Account Header**: User profile with XP, level, league, and badge
- **Navigation Menu**: Icon-based navigation with active states
- **Following Section**: List of followed news sources with live indicators
- **Gamification**: XP progress bar and league display
- **Responsive Design**: Collapsible on mobile devices

## 🔧 Usage

```tsx
import { Sidebar, SIDEBAR_ITEMS, FOLLOWING_ITEMS } from '@/modules/sidebar'

// Render sidebar
<Sidebar
  user={userProfile}
  sidebarItems={SIDEBAR_ITEMS}
  followingItems={FOLLOWING_ITEMS}
/>
```

## 👤 Account Header

Displays user information including:
- Avatar with fallback initials
- Name and current level
- XP progress bar
- League badge
- Achievement badge

## 🧭 Navigation

Navigation items include:
- Home, Breaking News, Trending News
- Forum, Categories, History
- Saved, Collections, Settings
- Account management

## 👥 Following Section

Shows followed sources with:
- Source avatars
- Live indicators for streaming content
- Expandable/collapsible list
- Quick access to followed content

## 🎮 Gamification

The sidebar includes gamification elements:
- **XP System**: Experience points for user engagement
- **Levels**: Automatic level calculation based on XP
- **Leagues**: Bronze, Silver, Gold, Platinum, Diamond
- **Badges**: Achievement badges for milestones

## 📱 Responsive Behavior

- **Desktop**: Fixed sidebar with full functionality
- **Tablet**: Collapsible sidebar with hamburger menu
- **Mobile**: Overlay sidebar with backdrop

## 🎨 Styling

Uses AA design tokens:
- Navy blue (#1e3a8a) for branding
- Gray scale for text hierarchy
- Accent blue (#3b82f6) for active states
- Smooth transitions and hover effects

## 🔄 State Management

- Active navigation item tracking
- Following list expansion state
- User profile data from context
- Responsive breakpoint handling

## 📝 Configuration

Navigation items are configured in `data.ts`:

```typescript
export const SIDEBAR_ITEMS: SidebarItem[] = [
  {
    id: 'home',
    label: 'Home',
    icon: 'Home',
    href: '/'
  },
  // ... more items
]
```

## 🧪 Testing

```bash
# Run sidebar tests
npm test tests/modules/sidebar/
```



