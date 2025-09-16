// Extensions module - Third-party integrations
export interface ExtensionConfig {
  boycott: boolean
  ocr: boolean
  analytics: boolean
}

export interface ExtensionsProps {
  className?: string
}

// Placeholder component - will be implemented with extension management
export const Extensions: React.FC<ExtensionsProps> = ({ className }) => {
  return (
    <div className={className}>
      <div className="text-center py-12 text-aa-gray-500">
        <h2 className="text-xl font-semibold mb-2">Extensions Module</h2>
        <p>Third-party integrations will be managed here.</p>
        <p className="text-sm mt-2">Boycott lists, OCR, analytics wrappers.</p>
      </div>
    </div>
  )
}

export default Extensions



