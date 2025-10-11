import type { Config } from 'tailwindcss'

const config: Config = {
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  theme: {
    extend: {
      colors: {
        // Classic Mode Colors
        'brand-blue': '#02215c',
        'cream': '#f2efe6',
        'cream-strong': '#e8e0cc',
        
        // Gradient Mode Colors (already in default Tailwind but explicit)
        'gradient-dark': '#0f172a',
        'gradient-blue': '#1e3a8a',
        'gradient-purple': '#7c3aed',
      },
    },
  },
  plugins: [],
}
export default config


