import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'AA HABER',
  description: 'AA Eksen',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="tr">
      <body className="antialiased">{children}</body>
    </html>
  )
}