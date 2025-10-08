export default function TopNav() {
  return (
    <header className="sticky top-0 z-50 bg-cream/95 backdrop-blur border-b border-cream-strong">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <img src="/images/aa-logo.png" alt="AA" className="h-8 w-auto" />
          <span className="text-lg font-extrabold tracking-tight text-brand-blue">AA HABER</span>
        </div>
        <nav className="hidden md:flex items-center gap-6">
          <a className="text-sm text-slate-700 hover:text-brand-blue transition-colors" href="#">Anasayfa</a>
          <a className="text-sm text-slate-700 hover:text-brand-blue transition-colors" href="#">Kategoriler</a>
          <a className="text-sm text-slate-700 hover:text-brand-blue transition-colors" href="#">Oyunlar</a>
        </nav>
        <div className="flex items-center gap-4">
          <div className="hidden sm:flex items-center bg-white rounded-full shadow-lg px-4 h-9">
            <svg className="w-4 h-4 text-slate-400" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-4.35-4.35M11 18a7 7 0 110-14 7 7 0 010 14z" />
            </svg>
            <input className="ml-2 text-sm outline-none placeholder-slate-400" placeholder="Haber ara..." />
          </div>
          <div className="bg-brand-blue text-white px-3 py-1.5 rounded-full text-xs font-bold shadow-lg">
            ⭐ 1,250 XP
          </div>
        </div>
      </div>
    </header>
  );
}