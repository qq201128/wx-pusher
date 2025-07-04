import './style.css'
import javascriptLogo from './javascript.svg'
import viteLogo from '/vite.svg'
import { setupCounter } from './counter.js'

function getOpenIdFromUrl() {
  const params = new URLSearchParams(window.location.search);
  return params.get('openid');
}

function getPageFromUrl() {
  const params = new URLSearchParams(window.location.search);
  return params.get('page');
}

async function renderHtmlCard() {
  const openid = getOpenIdFromUrl();
  if (!openid) {
    document.querySelector('#app').innerHTML = '缺少 openid 参数';
    return;
  }
  const res = await fetch(`/api/getHtml?openId=${encodeURIComponent(openid)}`);
  const data = await res.json();
  document.querySelector('#app').innerHTML = data.information;
}

// 新增：支持 /uploadFile 路由
if (window.location.pathname === '/uploadFile') {
  import('./uploadFile.js');
} else {
  const page = getPageFromUrl();
  if (page === 'uploadFile') {
    import('./uploadFile.js');
  } else {
    renderHtmlCard();
  }
}
