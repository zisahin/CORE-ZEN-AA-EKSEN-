'use client';

import { useEffect, useState } from 'react';
import TopNav from '@/components/TopNav';
import LeftSidebar from '@/components/LeftSidebar';
import Timeline from '@/components/Timeline';
import { timelineService } from '@/services/timelineService';
import { TimeTunnelCategory } from '@/types/firestore';

export default function TimelinePage() {
  const [categories, setCategories] = useState<TimeTunnelCategory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const data = await timelineService.getActiveCategories();
      setCategories(data);
      console.log(`✅ ${data.length} timeline kategorisi yüklendi`);
    } catch (error) {
      console.error('❌ Timeline kategorileri yüklenemedi:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950 relative overflow-hidden">
      {/* Dekoratif Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-20 w-96 h-96 bg-purple-600/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '700ms' }}></div>
      </div>
      
      <div className="relative z-10">
        <TopNav />
        <div className="flex">
          <LeftSidebar currentPath="/timeline" />
          <main className="flex-1">
            {loading ? (
              <div className="flex items-center justify-center h-screen">
                <div className="text-lg text-white">🔄 Zaman Tüneli yükleniyor...</div>
              </div>
            ) : categories.length === 0 ? (
              <div className="flex items-center justify-center h-screen">
                <div className="text-center">
                  <p className="text-xl mb-2 text-white">📭 Henüz timeline kategorisi yok</p>
                  <p className="text-white/60">Firebase'de time_tunnel_categories koleksiyonunu kontrol edin</p>
                </div>
              </div>
            ) : (
              <Timeline categories={categories} />
            )}
          </main>
        </div>
      </div>
    </div>
  );
}