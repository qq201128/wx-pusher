import './style.css'
import javascriptLogo from './javascript.svg'
import viteLogo from '/vite.svg'
import { setupCounter } from './counter.js'

async function renderHtmlCard() {
  const res = await fetch('/api/getHtml', { method: 'POST' });
  const data = await res.json();
  // 直接渲染后端返回的HTML片段
  document.querySelector('#app').innerHTML = data.information;
}

renderHtmlCard();
