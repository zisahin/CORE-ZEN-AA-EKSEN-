'use client'
import { TickerItem } from '@/app/api/ticker/route'
import { useEffect, useMemo, useState } from 'react'

type ApiResponse = {
  updatedAt: string
  items: TickerItem[]
}

export default function FinanceTicker() {
  const [data, setData] = useState<ApiResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  const fetchData = async () => {
    try {
      setError(null)
      const res = await fetch('/api/ticker', { cache: 'no-store' })
      if (!res.ok) throw new Error('Veri alınamadı')
      const json = (await res.json()) as ApiResponse
      setData(json)
    } catch (e: any) {
      setError(e?.message || 'Hata')
    }
  }

  useEffect(() => {
    fetchData()
    const id = setInterval(fetchData, 60_000)
    return () => clearInterval(id)
  }, [])

  const items = useMemo(() => data?.items ?? [], [data])

  return (
    <div className="w-full bg-blue-900 text-white border-b-2 border-blue-800">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center h-10 overflow-hidden">
          <div className="flex items-center gap-6 whitespace-nowrap animate-[ticker_40s_linear_infinite] will-change-transform">
            {items.map((it) => {
              const isUp = (it.changePercent ?? 0) >= 0
              return (
                <div key={it.key} className="flex items-center gap-2 text-sm">
                  <span className="font-semibold tracking-wide">{it.label}</span>
                  {typeof it.changePercent === 'number' && (
                    <span className={isUp ? 'text-green-400' : 'text-red-400'}>
                      {isUp ? '▲' : '▼'}
                    </span>
                  )}
                  <span className="font-medium">
                    {typeof it.value === 'number' ? it.value.toLocaleString('tr-TR', { maximumFractionDigits: 2 }) : '-'}
                  </span>
                  {typeof it.changePercent === 'number' && (
                    <span className={isUp ? 'text-green-400' : 'text-red-400'}>
                      {Math.abs(it.changePercent).toFixed(2)}%
                    </span>
                  )}
                </div>
              )
            })}
            {/* duplicate for smooth infinite scroll */}
            {items.map((it) => {
              const isUp = (it.changePercent ?? 0) >= 0
              return (
                <div key={it.key + '-dup'} className="flex items-center gap-2 text-sm">
                  <span className="font-semibold tracking-wide">{it.label}</span>
                  {typeof it.changePercent === 'number' && (
                    <span className={isUp ? 'text-green-400' : 'text-red-400'}>
                      {isUp ? '▲' : '▼'}
                    </span>
                  )}
                  <span className="font-medium">
                    {typeof it.value === 'number' ? it.value.toLocaleString('tr-TR', { maximumFractionDigits: 2 }) : '-'}
                  </span>
                  {typeof it.changePercent === 'number' && (
                    <span className={isUp ? 'text-green-400' : 'text-red-400'}>
                      {Math.abs(it.changePercent).toFixed(2)}%
                    </span>
                  )}
                </div>
              )
            })}
          </div>
        </div>
      </div>
      <style jsx global>{`
        @keyframes ticker {
          0% { transform: translateX(0); }
          100% { transform: translateX(-50%); }
        }
      `}</style>
      {error && (
        <div className="max-w-7xl mx-auto px-4 py-1 text-xs text-red-200">{error}</div>
      )}
    </div>
  )
}