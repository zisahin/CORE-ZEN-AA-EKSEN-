import { newsService } from '@/services/newsService';
import { useEffect, useState } from 'react';
import { City, NewsCategory } from '@/types/city';
import { getColorForIntensity } from '@/utils/intensityCalculator';

interface CityModalProps {
  city: City
  categories: NewsCategory[]
  onClose: () => void
}

export default function CityModal({ city, categories, onClose }: CityModalProps) {
  // Firebase'den gerçek haberleri çek
  const [realNews, setRealNews] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (city) {
      loadCityNews();
    }
  }, [city]);

  const loadCityNews = async () => {
    if (!city) return;
    
    setLoading(true);
    try {
      const news = await newsService.getNewsByCity(city.name, 100);
      setRealNews(news);
      console.log(`✅ ${city.name} için ${news.length} haber yüklendi`);
    } catch (error) {
      console.error(`❌ ${city.name} haberleri yüklenemedi:`, error);
    } finally {
      setLoading(false);
    }
  };

  // Şehrin kategorilerini güncelle ve sırala (en çok haberi olan üstte)
  const cityCategories = categories.map(cat => ({
    ...cat,
    count: city.newsByCategory?.[cat.id] || 0
  }))
  .filter(cat => cat.count > 0)
  .sort((a, b) => b.count - a.count)
  
  const cityColor = getColorForIntensity(city.intensityLevel)
  
  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />
      
      {/* Modal */}
      <div className="relative bg-white rounded-t-3xl sm:rounded-2xl shadow-2xl w-full sm:max-w-2xl max-h-[85vh] overflow-hidden animate-slide-up">
        {/* Header */}
        <div 
          className="p-6 pb-8 text-white relative overflow-hidden"
          style={{ 
            background: `linear-gradient(135deg, ${cityColor} 0%, ${cityColor}dd 100%)`
          }}
        >
          {/* Decorative circles */}
          <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-16 -mt-16" />
          <div className="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full -ml-12 -mb-12" />
          
          <button
            onClick={onClose}
            className="absolute top-4 right-4 w-10 h-10 rounded-full bg-white/90 hover:bg-white flex items-center justify-center transition-all shadow-lg hover:shadow-xl hover:scale-110 z-10"
          >
            <span className="text-2xl font-bold text-gray-700">✕</span>
          </button>
          
          <div className="relative z-10">
            <h2 className="text-3xl font-bold mb-2 flex items-center gap-2">
              📍 {city.name}
            </h2>
            <div className="flex items-center gap-4 text-sm">
              <span className="bg-white/20 px-3 py-1 rounded-full backdrop-blur">
                📊 {city.totalNewsCount} Haber
              </span>
              <span className="bg-white/20 px-3 py-1 rounded-full backdrop-blur">
                📁 {cityCategories.length} Kategori
              </span>
            </div>
          </div>
        </div>
        
        {/* Kategoriler */}
        <div className="p-6 overflow-y-auto max-h-[50vh]">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-bold text-gray-800">
              📂 Haber Kategorileri
            </h3>
            {cityCategories.length > 0 && (
              <span className="text-xs text-gray-500">
                En çoktan → aza
              </span>
            )}
          </div>
          
          {cityCategories.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              <div className="text-5xl mb-3 animate-pulse">📭</div>
              <p className="text-lg font-medium">Bu şehirde henüz haber bulunmuyor</p>
              <p className="text-sm mt-2">Yakında haberler eklenecek...</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {cityCategories.map((category, index) => (
                <div
                  key={category.id}
                  className="group bg-gradient-to-br from-white to-gray-50 border-2 border-gray-100 rounded-xl p-4 hover:border-blue-500 hover:shadow-xl hover:scale-[1.02] transition-all duration-200 cursor-pointer"
                  style={{
                    animationDelay: `${index * 50}ms`
                  }}
                  onClick={() => {
                    console.log(`🔗 Kategori tıklandı: ${city.name} - ${category.name}`)
                    // TODO: Kategori detay sayfasına yönlendir
                    alert(`${city.name} - ${category.name}\n${category.count} haber bulunuyor.\n\n(Backend entegrasyonu sonrası aktif olacak)`)
                  }}
                >
                  <div className="flex items-center justify-between mb-2">
                    <div 
                      className="w-12 h-12 rounded-xl flex items-center justify-center text-2xl group-hover:scale-110 group-hover:rotate-6 transition-all shadow-sm"
                      style={{ 
                        backgroundColor: `${category.color}20`,
                        border: `2px solid ${category.color}40`
                      }}
                    >
                      <span>{category.icon}</span>
                    </div>
                    <div className="text-right">
                      <div className="text-2xl font-bold" style={{ color: category.color }}>
                        {category.count}
                      </div>
                      <div className="text-xs text-gray-500">haber</div>
                    </div>
                  </div>
                  <div className="flex items-center justify-between">
                    <h4 className="font-bold text-base text-gray-800 group-hover:text-blue-600 transition-colors">
                      {category.name}
                    </h4>
                    <div className="text-gray-400 group-hover:text-blue-500 group-hover:translate-x-1 transition-all">
                      →
                    </div>
                  </div>
                  
                  {/* Progress bar */}
                  <div className="mt-3 h-1 bg-gray-200 rounded-full overflow-hidden">
                    <div 
                      className="h-full transition-all duration-500 rounded-full"
                      style={{ 
                        width: `${(category.count / city.totalNewsCount) * 100}%`,
                        backgroundColor: category.color
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
          
          {/* Alt bilgi */}
          {cityCategories.length > 0 && (
            <div className="mt-6 p-4 bg-blue-50 rounded-xl border border-blue-100">
              <p className="text-sm text-blue-800">
                💡 <strong>İpucu:</strong> Bir kategoriye tıklayarak o şehrin ilgili kategorideki haberlerini görüntüleyebilirsiniz.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

