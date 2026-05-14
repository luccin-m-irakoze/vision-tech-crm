/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js}'],
  theme: {
    extend: {
      colors: {
        // Vision Tech CRM brand palette - vibrant edition
        vtc: {
          primary:   '#0F2A4A', // deep navy (kept for brand continuity)
          primary2:  '#1E3A6C', // lighter navy for gradients
          accent:    '#1E88E5', // electric blue
          accent2:   '#2563EB', // deeper blue for gradients
          purple:    '#7C3AED', // vibrant purple accent
          teal:      '#0EA5A4', // teal accent for tertiary actions
          success:   '#10B981',
          warning:   '#F59E0B',
          danger:    '#EF4444',
          surface:   '#F8FAFC', // off-white page surface
          surface2:  '#F1F5F9', // alternate surface
          ink:       '#0F172A', // headings & primary text
          muted:     '#64748B'  // secondary text
        }
      },
      fontFamily: {
        sans: ['"Inter Variable"', 'Inter', 'system-ui', '-apple-system', '"Segoe UI"', 'Roboto', 'sans-serif'],
        display: ['"Plus Jakarta Sans Variable"', '"Plus Jakarta Sans"', 'Inter', 'system-ui', 'sans-serif']
      },
      boxShadow: {
        // Soft, modern multi-layer shadows
        soft:    '0 1px 2px rgba(15, 23, 42, 0.04), 0 1px 3px rgba(15, 23, 42, 0.06)',
        card:    '0 1px 3px rgba(15, 23, 42, 0.06), 0 4px 12px -2px rgba(15, 23, 42, 0.05)',
        lift:    '0 4px 6px rgba(15, 23, 42, 0.04), 0 12px 24px -6px rgba(15, 23, 42, 0.10)',
        ring:    '0 0 0 4px rgba(30, 136, 229, 0.18)',
        glow:    '0 8px 32px -8px rgba(30, 136, 229, 0.45)',
        glowPurple: '0 8px 32px -8px rgba(124, 58, 237, 0.45)'
      },
      backgroundImage: {
        'gradient-brand':   'linear-gradient(135deg, #1E88E5 0%, #2563EB 50%, #7C3AED 100%)',
        'gradient-cool':    'linear-gradient(135deg, #1E3A6C 0%, #0F2A4A 100%)',
        'gradient-surface': 'radial-gradient(1200px 600px at 20% -10%, rgba(124,58,237,0.08), transparent 60%), radial-gradient(900px 500px at 100% 0%, rgba(30,136,229,0.10), transparent 55%), linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%)',
        'gradient-sidebar': 'linear-gradient(180deg, #0F2A4A 0%, #0B1F3A 100%)'
      },
      borderRadius: {
        xl2: '1rem',
        '3xl': '1.5rem'
      },
      keyframes: {
        'fade-in':    { '0%': { opacity: 0 }, '100%': { opacity: 1 } },
        'slide-up':   { '0%': { opacity: 0, transform: 'translateY(8px)' }, '100%': { opacity: 1, transform: 'translateY(0)' } },
        'pop-in':     { '0%': { opacity: 0, transform: 'scale(0.96)' }, '100%': { opacity: 1, transform: 'scale(1)' } },
        'shimmer':    { '0%': { backgroundPosition: '-200% 0' }, '100%': { backgroundPosition: '200% 0' } }
      },
      animation: {
        'fade-in':  'fade-in 200ms ease-out both',
        'slide-up': 'slide-up 240ms ease-out both',
        'pop-in':   'pop-in 180ms ease-out both',
        'shimmer':  'shimmer 1.4s linear infinite'
      }
    }
  },
  plugins: []
}
