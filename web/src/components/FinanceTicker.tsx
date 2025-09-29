const items = ['JRO','ALTIN','ETH/USDT','BTC/USDT','BIST 100','DOLAR','EURO','ALTIN','ETH/USDT','BTC/USDT','BIST 100'];

export default function TickerBar() {
  return (
    <>
      <div className="w-full bg-[var(--brand-blue)] text-white">
        <div className="max-w-7xl mx-auto px-4">
          <div className="h-10 flex items-center overflow-x-auto gap-6">
            {items.map((label) => (
              <span key={label} className="whitespace-nowrap text-xs tracking-wide">{label}</span>
            ))}
          </div>
        </div>
      </div>
      <div className="h-[2px] bg-[var(--brand-blue)]" />
    </>
  );
}