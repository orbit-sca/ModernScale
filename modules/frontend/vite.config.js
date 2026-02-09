import { defineConfig } from 'vite'

export default defineConfig({
  root: '.',
  publicDir: 'public',
  build: {
    outDir: 'dist',
    emptyOutDir: false
  },
  server: {
    port: 3000,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/analytics': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  // Enable SPA fallback - serve index.html for all routes
  appType: 'spa'
})
