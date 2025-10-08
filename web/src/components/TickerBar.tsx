'use client'

import { useState, useEffect } from 'react'

interface FinanceData {
  symbol: string
  price: number
  change: number
  changePercent: number
  isUp: boolean
}

const symbols = ['DOLAR', 'EURO', 'ALTIN', 'ETH/USDT', 'BTC/USDT', 'BIST 100', 'XAU/USD', 'GBP/TRY']

// Simüle edilmiş başlangıç verileri
const generateInitialData = (): FinanceData[] => [
  { symbol: 'DOLAR', price: 41.57, change: -0.23, changePercent: -0.55, isUp: false },
  { symbol: 'EURO', price: 48.77, change: 0.45, changePercent: 0.93, isUp: true },
  { symbol: 'ALTIN', price: 3816.77, change: 25.30, changePercent: 0.67, isUp: true },
  { symbol: 'ETH/USDT', price: 4097.00, change: 87.50, changePercent: 2.18, isUp: true },
  { symbol: 'BTC/USDT', price: 111741.00, change: 1250.00, changePercent: 1.13, isUp: true },
  { symbol: 'BIST 100', price: 11151.20, change: -89.30, changePercent: -0.79, isUp: false },
  { symbol: 'XAU/USD', price: 2456.80, change: 12.40, changePercent: 0.51, isUp: true },
  { symbol: 'GBP/TRY', price: 52.34, change: -0.67, changePercent: -1.26, isUp: false }
]

export default function FinanceTicker() {
  const [financeData, setFinanceData] = useState<FinanceData[]>(generateInitialData())
  const [isLoading, setIsLoading] = useState(false)

  // Verileri düzenli olarak güncelle (gerçek API çağrısı simülasyonu)
  useEffect(() => {
    const updateData = () => {
      setIsLoading(true)
      
      // API çağrısı simülasyonu
      setTimeout(() => {
        setFinanceData(prevData => 
          prevData.map(item => {
            // Rastgele fiyat değişimi (-3% ile +3% arasında)
            const randomChange = (Math.random() - 0.5) * 6
            const newPrice = item.price * (1 + randomChange / 100)
            const change = newPrice - item.price
            const changePercent = (change / item.price) * 100
            
            return {
              ...item,
              price: newPrice,
              change: change,
              changePercent: changePercent,
              isUp: change >= 0
            }
          })
        )
        setIsLoading(false)
      }, 500)
    }

    // İlk yüklemeden sonra her 5 saniyede güncelle
    const interval = setInterval(updateData, 10000)
    
    return () => clearInterval(interval)
  }, [])

  // Fiyat formatlaması
  const formatPrice = (price: number, symbol: string) => {
    if (symbol.includes('USDT') || symbol.includes('USD')) {
      return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(price)
    }
    return new Intl.NumberFormat('tr-TR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price)
  }

  // Değişim formatlaması
  const formatChange = (change: number, isPercent: boolean = false) => {
    const value = Math.abs(change)
    const sign = change >= 0 ? '+' : '-'
    if (isPercent) {
      return `${sign}${value.toFixed(2)}%`
    }
    return `${sign}${value.toFixed(2)}`
  }

  // Sonsuz kaydırma için verileri ikiye katla
  const duplicatedData = [...financeData, ...financeData]

  return (
    <div className="relative w-full bg-[var(--brand-blue)] text-white overflow-hidden">
      {/* Loading indicator */}
      {isLoading && (
        <div className="absolute top-0 left-0 w-full h-1 bg-blue-400/30">
          <div className="h-full bg-blue-300 animate-pulse"></div>
        </div>
      )}

      {/* Ticker container */}
      <div className="relative h-12 flex items-center">
        <div className="flex animate-scroll-right">
          {duplicatedData.map((item, index) => (
            <div 
              key={`${item.symbol}-${index}`}
              className="flex items-center gap-3 px-6 whitespace-nowrap border-r border-blue-400/20 last:border-r-0"
            >
              {/* Symbol */}
              <span className="font-mono text-sm text-brand-blue  px-2 py-1 rounded">
                {item.symbol}
              </span>

              {/* Price */}
              <span  className="font-mono text-sm text-brand-blue  px-2 py-1 rounded">
               {formatPrice(item.price, item.symbol)}
              </span>

              {/* Arrow and change */}
              <div className="flex items-center gap-1">
                <span className={`text-lg ${item.isUp ? 'text-green-400' : 'text-red-400'}`}>
                  {item.isUp ? '↗' : '↘'}
                </span>
                <span className={`text-xs font-medium ${
                  item.isUp ? 'text-green-300' : 'text-red-300'
                }`}>
                  {formatChange(item.changePercent, true)}
                </span>
              </div>

              {/* Real-time pulse indicator */}
              <div className={`w-2 h-2 rounded-full ${
                item.isUp ? 'bg-green-400' : 'bg-red-400'
              } animate-pulse`}></div>
            </div>
          ))}
        </div>
      </div>

      {/* Bottom gradient line */}
      <div className="h-[2px] bg-gradient-to-r from-green-400 via-yellow-400 to-red-400"></div>

      {/* CSS Animations */}
      <style jsx>{`
        @keyframes scroll-right {
          0% {
            transform: translateX(0);
          }
          100% {
            transform: translateX(-50%);
          }
        }
        
        .animate-scroll-right {
          animation: scroll-right 45s linear infinite;
          display: flex;
          width: max-content;
        }
        
        .animate-scroll-right:hover {
          animation-play-state: paused;
        }

        @keyframes pulse {
          0%, 100% {
            opacity: 1;
          }
          50% {
            opacity: 0.5;
          }
        }
      `}</style>
    </div>
  )
}


