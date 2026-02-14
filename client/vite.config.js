import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

import path from 'node:path'

/*
  vite.config.js는 브라우저가 아닌 Node.js 환경에서 실행되므로,
  console.log 출력은 브라우저가 아닌 터미널에 표시된다.
*/
console.log(`========== ${path.resolve(__dirname, 'src')} ==========`);

export default defineConfig({
  plugins: [react()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    host: true, // 127.0.0.1 → 0.0.0.0 변경(https://naver.me/5If27Yth 참고)

    /*
      쉽게 말해, "아래 허용된 Host 주소로 들어온 요청만 받아들이겠다"는 의미다.
      - http://localhost:5173 : 허용(O)
      - https://○○○.trycloudflare.com : 허용(O)
      - http://192.168.○○○.○○○:5173 : 거부(X)
    */
    allowedHosts: [
      'localhost',
      '.trycloudflare.com' // ≈ *.trycloudflare.com
    ],

    port: 5173,
    strictPort: true // 포트 고정(5173 포트를 사용할 수 없는 상황이더라도, 다른 포트로 자동 변경 X)
  }
})