// Ilanlar module - Classifieds and marketplace
export interface IlanItem {
  id: string
  title: string
  description: string
  price?: number
  currency: string
  category: 'need' | 'organ' | 'animal' | 'other'
  location: string
  contact: string
  images: string[]
  publishedAt: Date
  author: string
}

export interface IlanlarProps {
  className?: string
}

// Placeholder component - will be implemented with classifieds functionality
export const Ilanlar: React.FC<IlanlarProps> = ({ className }) => {
  return (
    <div className={className}>
      <div className="text-center py-12 text-aa-gray-500">
        <h2 className="text-xl font-semibold mb-2">Ilanlar Module</h2>
        <p>Classifieds and marketplace will be displayed here.</p>
        <p className="text-sm mt-2">Need line, organ donation, lost animals, etc.</p>
      </div>
    </div>
  )
}

export default Ilanlar



