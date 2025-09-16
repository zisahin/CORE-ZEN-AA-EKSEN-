# AA News Portal

A modern, modular news portal built with Next.js 14, TypeScript, and TailwindCSS. This project follows a feature-based architecture with reusable design system components.

## 🚀 Quick Start

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test

# Build for production
npm run build
```

## 📁 Project Structure

```
src/
├── app/                    # Next.js App Router
│   ├── api/               # API routes
│   ├── layout.tsx         # Root layout
│   └── page.tsx          # Home page
├── components/            # Shared UI components
│   └── ui/               # Design system components
├── modules/              # Feature-based modules
│   ├── news/            # News functionality
│   ├── sidebar/         # Sidebar navigation
│   ├── account/         # User account & gamification
│   ├── reels/           # Video content (lazy-loaded)
│   ├── timeline/        # Event timeline (lazy-loaded)
│   ├── ilanlar/         # Classifieds module
│   └── extensions/      # Third-party integrations
├── lib/                 # Utilities and shared logic
├── styles/              # Global styles and tokens
├── types/               # TypeScript type definitions
└── hooks/               # Shared React hooks
```

## 🎨 Design System

The project uses a custom design system built on TailwindCSS with CSS custom properties for theming:

- **Colors**: AA Navy (#1e3a8a), White (#ffffff), Accent Blue (#3b82f6)
- **Components**: Button, Input, Avatar, Badge, Modal, Tabs, Card, Chip
- **Icons**: Lucide React icons
- **Typography**: Inter font family

## 🧩 Modules

### News Module (`/modules/news`)
- News cards with image, title, excerpt, and metadata
- Feedback system (like, dislike, report, comment)
- Trending news section
- Search and filtering capabilities

### Sidebar Module (`/modules/sidebar`)
- Account header with XP, level, league, and badge
- Navigation menu with icons and badges
- Following section with live indicators
- Responsive design

### Account Module (`/modules/account`)
- User profile management
- Gamification system (XP, levels, leagues)
- Achievement badges
- Account settings

## 🔧 Configuration

### Environment Variables
Create a `.env.local` file with:

```env
NEXT_PUBLIC_API_URL=http://localhost:3000/api
```

### Placeholder Values
The following placeholders can be customized:

- `{{NEXT_JS_VERSION}}`: Next.js version (default: 14)
- `{{COMPONENT_LIB}}`: Component library (default: shadcn)
- `{{ICON_LIB}}`: Icon library (default: lucide-react)
- `{{LOGO_URL}}`: Logo image URL
- `{{AA_NAVY}}`, `{{AA_WHITE}}`, `{{AA_ACCENT}}`: Brand colors
- `{{ACCOUNT_NAME}}`, `{{ACCOUNT_AVATAR}}`, `{{ACCOUNT_BADGE}}`, `{{ACCOUNT_XP}}`, `{{ACCOUNT_LEAGUE}}`: User data

## 🧪 Testing

The project includes Jest and React Testing Library for unit testing:

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm test -- --coverage
```

## 📱 Features

- **Responsive Design**: Mobile-first approach with TailwindCSS
- **Accessibility**: WAI-ARIA compliant components
- **Modular Architecture**: Feature-based modules with barrel exports
- **Type Safety**: Full TypeScript coverage
- **Performance**: Lazy loading for heavy modules
- **Feedback System**: User engagement through likes, comments, and reports
- **Gamification**: XP system, levels, leagues, and badges

## 🚀 Deployment

The project is ready for deployment on Vercel, Netlify, or any Node.js hosting platform:

```bash
npm run build
npm start
```

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📞 Support

For questions or support, please open an issue on GitHub.



