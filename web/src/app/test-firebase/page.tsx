'use client';

import { useEffect, useState } from 'react';
import { newsService } from '@/services/newsService';

export default function TestFirebase() {
  const [status, setStatus] = useState('Bağlanıyor...');
  const [newsCount, setNewsCount] = useState(0);
  const [error, setError] = useState('');

  useEffect(() => {
    testConnection();
  }, []);

  const testConnection = async () => {
    try {
      const news = await newsService.getNews(5);
      setNewsCount(news.length);
      setStatus('✅ Firebase bağlantısı BAŞARILI!');
    } catch (err: any) {
      setStatus('❌ Firebase bağlantısı BAŞARISIZ!');
      setError(err.message);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-8">
        <h1 className="text-3xl font-bold mb-6">🔥 Firebase Test</h1>
        
        <div className="mb-4">
          <h2 className="text-xl font-semibold">Bağlantı Durumu:</h2>
          <p className="text-2xl mt-2">{status}</p>
        </div>

        {newsCount > 0 && (
          <div className="mb-4 p-4 bg-green-100 rounded">
            <h2 className="text-xl font-semibold">📰 Haber Sayısı:</h2>
            <p className="text-3xl font-bold">{newsCount} haber bulundu!</p>
          </div>
        )}

        {error && (
          <div className="mb-4 p-4 bg-red-100 rounded">
            <h2 className="text-xl font-semibold text-red-600">Hata:</h2>
            <p className="text-sm">{error}</p>
          </div>
        )}

        <div className="mt-6">
          <a href="/" className="text-blue-600 underline">← Ana sayfaya dön</a>
        </div>
      </div>
    </div>
  );
}
