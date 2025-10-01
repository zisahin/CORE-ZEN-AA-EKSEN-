import type { Config } from 'tailwindcss'

const config: Config = {
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  theme: {
    extend: {
      colors: {
        'brand-blue': '#02215c',
        'cream': '#f2efe6',
        'cream-strong': '#e8e0cc',
      },
    },
  },
  plugins: [],
}
export default config