import React, { Suspense } from 'react'
import { cn } from '@/lib/utils'

interface LazyModuleProps {
  moduleName: string
  fallback?: React.ReactNode
  className?: string
}

const LoadingFallback: React.FC<{ moduleName: string }> = ({ moduleName }) => (
  <div className="flex items-center justify-center p-8">
    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-aa-accent"></div>
    <span className="ml-3 text-aa-gray-600">Loading {moduleName}...</span>
  </div>
)

export const LazyModule: React.FC<LazyModuleProps> = ({
  moduleName,
  fallback,
  className
}) => {
  const LazyComponent = React.lazy(() => {
    switch (moduleName) {
      case 'reels':
        return import('@/modules/reels')
      case 'timeline':
        return import('@/modules/timeline')
      case 'account':
        return import('@/modules/account')
      case 'ilanlar':
        return import('@/modules/ilanlar')
      case 'extensions':
        return import('@/modules/extensions')
      default:
        return Promise.reject(new Error(`Unknown module: ${moduleName}`))
    }
  })

  return (
    <div className={cn('w-full', className)}>
      <Suspense fallback={fallback || <LoadingFallback moduleName={moduleName} />}>
        <LazyComponent />
      </Suspense>
    </div>
  )
}



