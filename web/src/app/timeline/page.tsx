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
    <div className="min-h-screen">
      <TopNav />
      <div className="flex">
        <LeftSidebar currentPath="/timeline" />
        <main className="flex-1 bg-gray-50">
          {loading ? (
            <div className="flex items-center justify-center h-screen">
              <div className="text-lg">🔄 Zaman Tüneli yükleniyor...</div>
            </div>
          ) : categories.length === 0 ? (
            <div className="flex items-center justify-center h-screen">
              <div className="text-center">
                <p className="text-xl mb-2">📭 Henüz timeline kategorisi yok</p>
                <p className="text-gray-600">Firebase'de time_tunnel_categories koleksiyonunu kontrol edin</p>
              </div>
            </div>
          ) : (
            <Timeline categories={categories} />
          )}
        </main>
      </div>
    </div>
  );
}