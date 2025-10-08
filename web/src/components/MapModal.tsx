'use client'
import dynamic from 'next/dynamic'

const TurkeyNewsMap = dynamic(() => import('./TurkeyNewsMap'), {
  ssr: false
})

interface MapModalProps {
  onClose: () => void
}

export default function MapModal({ onClose }: MapModalProps) {
  return (
    <div className="fixed inset-0 z-[100]">
      {/* Arka plan (tıklanınca kapanır) */}
      <div 
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={onClose}
      />
      
      {/* Modal İçerik */}
      <div className="relative w-full h-full flex items-center justify-center p-8">
        {/* Beyaz Container */}
        <div className="relative bg-white rounded-2xl shadow-2xl w-full h-full overflow-hidden">
          {/* Kapat Butonu */}
          <button
            onClick={onClose}
            className="absolute top-4 right-4 z-[101] bg-white hover:bg-gray-100 p-3 rounded-full shadow-lg border-2 border-gray-200 transition-all duration-200"
          >
            <span className="text-2xl font-bold text-gray-700">✕</span>
          </button>
          
          {/* Harita */}
          <TurkeyNewsMap />
        </div>
      </div>
    </div>
  )
}