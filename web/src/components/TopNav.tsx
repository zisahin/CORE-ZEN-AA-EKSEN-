export default function TopNav() {
  return (
    <header className="sticky top-0 z-50 bg-gradient-to-r from-slate-900 via-blue-900 to-slate-900 backdrop-blur border-b border-white/10 shadow-lg">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <img src="/images/aa-logo.png" alt="AA" className="h-8 w-auto brightness-0 invert" />
          <span className="text-lg font-extrabold tracking-tight text-white">AA HABER</span>
        </div>
        <nav className="hidden md:flex items-center gap-6">
          <a className="text-sm text-white/80 hover:text-purple-400 transition-colors" href="#">Anasayfa</a>
          <a className="text-sm text-white/80 hover:text-purple-400 transition-colors" href="#">Kategoriler</a>
          <a className="text-sm text-white/80 hover:text-purple-400 transition-colors" href="#">Oyunlar</a>
        </nav>
        <div className="flex items-center gap-4">
          <div className="hidden sm:flex items-center bg-white/10 border border-white/20 rounded-full shadow-lg px-4 h-9 backdrop-blur">
            <svg className="w-4 h-4 text-white/60" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-4.35-4.35M11 18a7 7 0 110-14 7 7 0 010 14z" />
            </svg>
            <input className="ml-2 text-sm outline-none placeholder-white/40 bg-transparent text-white" placeholder="Haber ara..." />
          </div>
          <div className="bg-gradient-to-r from-purple-600 to-purple-700 text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-lg shadow-purple-900/50">
            ⭐ 1,250 XP
          </div>
        </div>
      </div>
    </header>
  );
}