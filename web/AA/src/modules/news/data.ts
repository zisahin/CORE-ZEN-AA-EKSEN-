import { NewsItem } from '@/types'

export const MOCK_NEWS: NewsItem[] = [
  {
    id: '1',
    title: 'A man was arrested for stealing chickens from the mayor\'s house in West Wakanda',
    content: 'Police in West Wakanda have arrested a local man for allegedly stealing chickens from the mayor\'s residence. The incident occurred early this morning when security guards noticed several chickens missing from the mayor\'s private poultry farm.',
    excerpt: 'Police in West Wakanda have arrested a local man for allegedly stealing chickens from the mayor\'s residence.',
    imageUrl: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=400&h=250&fit=crop',
    source: 'Hayam uruk',
    category: 'Criminal',
    publishedAt: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2 hours ago
    author: 'John Doe',
    url: '/news/1',
    tags: ['crime', 'west-wakanda', 'mayor']
  },
  {
    id: '2',
    title: 'Poultry Pilfering Perpetrator Caught Red-Handed at West Wakanda Mayor\'s Residence',
    content: 'In a dramatic turn of events, local authorities have apprehended a suspect in connection with the recent chicken thefts at the West Wakanda mayor\'s home. The arrest was made following an intensive investigation.',
    excerpt: 'Local authorities have apprehended a suspect in connection with the recent chicken thefts.',
    imageUrl: 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=250&fit=crop',
    source: 'Jebakan Batman',
    category: 'Criminal',
    publishedAt: new Date(Date.now() - 1 * 60 * 60 * 1000), // 1 hour ago
    author: 'Jane Smith',
    url: '/news/2',
    tags: ['crime', 'arrest', 'west-wakanda']
  },
  {
    id: '3',
    title: 'West Wakanda Man Arrested for Stealing Mayor\'s Chickens',
    content: 'A West Wakanda resident has been taken into custody after being accused of stealing chickens from the mayor\'s property. The case has drawn significant attention from the local community.',
    excerpt: 'A West Wakanda resident has been taken into custody after being accused of stealing chickens.',
    imageUrl: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=250&fit=crop',
    source: 'Sekitar Depok',
    category: 'Criminal',
    publishedAt: new Date(Date.now() - 8 * 60 * 60 * 1000), // 8 hours ago
    author: 'Mike Johnson',
    url: '/news/3',
    tags: ['crime', 'west-wakanda', 'theft']
  },
  {
    id: '4',
    title: 'Thief Caught Stealing Chickens from West Wakanda Mayor\'s Home',
    content: 'Police have successfully captured a suspect who was allegedly stealing chickens from the West Wakanda mayor\'s residence. The investigation is ongoing.',
    excerpt: 'Police have successfully captured a suspect who was allegedly stealing chickens.',
    imageUrl: 'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=400&h=250&fit=crop',
    source: 'Hayam uruk',
    category: 'Criminal',
    publishedAt: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2 hours ago
    author: 'Sarah Wilson',
    url: '/news/4',
    tags: ['crime', 'theft', 'west-wakanda']
  },
  {
    id: '5',
    title: 'Scientists Unveil Breakthrough in Renewable Energy Storage Technology',
    content: 'A team of researchers has announced a major breakthrough in renewable energy storage technology that could revolutionize how we store and use clean energy. The new technology promises to be more efficient and cost-effective than current solutions.',
    excerpt: 'A team of researchers has announced a major breakthrough in renewable energy storage technology.',
    imageUrl: 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=400&h=250&fit=crop',
    source: 'H+',
    category: 'Global',
    publishedAt: new Date(Date.now() - 24 * 60 * 60 * 1000), // Yesterday
    author: 'Dr. Emily Chen',
    url: '/news/5',
    tags: ['technology', 'renewable-energy', 'breakthrough']
  }
]



