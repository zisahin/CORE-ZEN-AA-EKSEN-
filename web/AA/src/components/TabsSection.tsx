import React from 'react'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/Tabs'
import { cn } from '@/lib/utils'
import * as LucideIcons from 'lucide-react'

export interface TabItem {
  id: string
  label: string
  icon?: string
  content: React.ReactNode
}

export interface TabsSectionProps {
  tabs: TabItem[]
  defaultTab?: string
  className?: string
}

export const TabsSection: React.FC<TabsSectionProps> = ({
  tabs,
  defaultTab,
  className
}) => {
  const defaultTabId = defaultTab || tabs[0]?.id

  return (
    <Tabs defaultValue={defaultTabId} className={cn('w-full', className)}>
      <TabsList className="grid w-full grid-cols-3 mb-6">
        {tabs.map((tab) => {
          const IconComponent = tab.icon 
            ? LucideIcons[tab.icon as keyof typeof LucideIcons] as React.ComponentType<{
                className?: string
                size?: number
              }>
            : null

          return (
            <TabsTrigger key={tab.id} value={tab.id} className="flex items-center gap-2">
              {IconComponent && <IconComponent className="h-4 w-4" />}
              {tab.label}
            </TabsTrigger>
          )
        })}
      </TabsList>

      {tabs.map((tab) => (
        <TabsContent key={tab.id} value={tab.id}>
          {tab.content}
        </TabsContent>
      ))}
    </Tabs>
  )
}



