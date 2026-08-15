/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Sora', 'Inter', 'system-ui', 'sans-serif'],
      },
      colors: {
        brand: {
          50:  'hsl(220, 80%, 97%)',
          100: 'hsl(220, 75%, 92%)',
          200: 'hsl(220, 70%, 84%)',
          300: 'hsl(220, 65%, 72%)',
          400: 'hsl(220, 62%, 58%)',
          500: 'hsl(220, 72%, 46%)',
          600: 'hsl(220, 76%, 38%)',
          700: 'hsl(220, 80%, 30%)',
          800: 'hsl(220, 82%, 22%)',
          900: 'hsl(220, 84%, 14%)',
        },
        accent: {
          400: 'hsl(38, 92%, 55%)',
          500: 'hsl(38, 90%, 48%)',
          600: 'hsl(38, 88%, 40%)',
        },
      },
      animation: {
        'fade-in': 'fadeIn 0.4s ease-out',
        'slide-up': 'slideUp 0.4s ease-out',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4,0,0.6,1) infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
    },
  },
  plugins: [],
};
