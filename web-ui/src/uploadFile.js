import './style.css';

const app = document.querySelector('#app');

app.innerHTML = `
  <div class="upload-card">
    <div class="upload-title">上传文件</div>
    <form id="uploadForm">
      <input type="file" id="fileInput" name="file" class="upload-input" accept="image/*" required />
      <br />
      <img id="previewImg" style="max-width: 200px; display: none; margin: 10px 0;" />
      <br />
      <button type="submit" class="upload-btn">上传</button>
    </form>
    <div id="result" class="upload-result"></div>
  </div>
`;

// 图片预览功能
const fileInput = document.getElementById('fileInput');
const previewImg = document.getElementById('previewImg');
fileInput.addEventListener('change', function () {
  const file = this.files[0];
  if (file && file.type.startsWith('image/')) {
    const reader = new FileReader();
    reader.onload = function (e) {
      previewImg.src = e.target.result;
      previewImg.style.display = 'block';
    };
    reader.readAsDataURL(file);
  } else {
    previewImg.src = '';
    previewImg.style.display = 'none';
  }
});

document.getElementById('uploadForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const fileInput = document.getElementById('fileInput');
  const resultDiv = document.getElementById('result');
  if (!fileInput.files.length) {
    resultDiv.textContent = '请选择文件';
    return;
  }
  const formData = new FormData();
  formData.append('multipartFile', fileInput.files[0]);
  resultDiv.innerHTML = '<span class="upload-loading"></span>上传中...';
  try {
    const res = await fetch('/api/uploadFile', {
      method: 'POST',
      body: formData,
    });
    if (!res.ok) throw new Error('上传失败');
    const text = await res.text();
    resultDiv.innerHTML = '✅ 上传成功: ' + text;
  } catch (err) {
    resultDiv.innerHTML = '❌ 上传失败: ' + err.message;
  }
}); 