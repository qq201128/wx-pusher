import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 9091,
    proxy: {
      '/api': {
        target: 'http://localhost:19090',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
    allowedHosts: ['3751016qc9ar.vicp.fun'],
  },
}); 