export default function IntensityLegend() {
  return (
    <div className="absolute top-4 right-4 bg-white/95 backdrop-blur rounded-xl shadow-lg p-4 z-10">
      <h3 className="text-sm font-bold text-gray-800 mb-3">
        Haber Yoğunluğu
      </h3>
      
      {/* Renk çubuğu */}
      <div className="flex h-3 rounded-full overflow-hidden mb-2">
        <div className="flex-1" style={{ backgroundColor: '#4FC3F7' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#29B6F6' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#FFB74D' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#E53935' }}></div>
      </div>
      
      {/* Etiketler */}
      <div className="flex justify-between text-xs text-gray-600 mb-3">
        <span>Az</span>
        <span>Çok</span>
      </div>
      
      {/* Detaylı açıklama */}
      <div className="text-xs text-gray-500 space-y-1 pt-3 border-t">
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#4FC3F7' }}></div>
          <span>0-25 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#29B6F6' }}></div>
          <span>26-60 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#FFB74D' }}></div>
          <span>61-100 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#E53935' }}></div>
          <span>100+ haber</span>
        </div>
      </div>
    </div>
  )
}

