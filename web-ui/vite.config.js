import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    port: 9091,
    proxy: {
      '/api': {
        target: 'http://localhost:19090',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
}); 