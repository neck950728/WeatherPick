import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

import path from 'node:path'

/*
  vite.config.js는 브라우저가 아닌 Node.js 환경에서 실행되므로,
  console.log 출력은 브라우저가 아닌 터미널에 표시된다.
*/
console.log(`========== ${path.resolve(__dirname, 'src')} ==========`);

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})