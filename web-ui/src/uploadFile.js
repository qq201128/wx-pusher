import './style.css';

const app = document.querySelector('#app');

app.innerHTML = `
  <div class="upload-card">
    <div class="upload-title">批量上传图片</div>
    <form id="uploadForm">
      <label for="fileInput" class="upload-input-label">选择文件</label>
      <span class="selected-count" id="selectedCount">0 个文件</span>
      <input type="file" id="fileInput" name="file" class="upload-input" accept="image/*" multiple required />
      <br />
      <div id="previewList" class="preview-list-grid"></div>
      <br />
      <button type="submit" class="upload-btn">上传全部</button>
    </form>
    <div id="result" class="upload-result"></div>
  </div>
`;

const fileInput = document.getElementById('fileInput');
const previewList = document.getElementById('previewList');
const selectedCount = document.getElementById('selectedCount');
let files = [];

fileInput.addEventListener('change', function () {
  files = Array.from(this.files);
  selectedCount.textContent = files.length + ' 个文件';
  previewList.innerHTML = '';
  files.forEach((file, idx) => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      const previewItem = document.createElement('div');
      previewItem.className = 'preview-item';
      previewItem.innerHTML = `
        <img style="display: none;" />
        <div class="file-name">${file.name}</div>
        <div class="progress-bar-bg"><div class="progress-bar" id="progress-${idx}"></div></div>
        <span class="status" id="status-${idx}"></span>
        <span class="link" id="link-${idx}"></span>
      `;
      previewList.appendChild(previewItem);
      const img = previewItem.querySelector('img');
      reader.onload = function (e) {
        img.src = e.target.result;
        img.style.display = 'inline-block';
      };
      reader.readAsDataURL(file);
    }
  });
});

document.getElementById('uploadForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  if (!files.length) {
    document.getElementById('result').textContent = '请选择图片';
    return;
  }
  document.getElementById('result').textContent = '';
  await Promise.all(files.map((file, idx) => uploadSingleFile(file, idx)));
});

function uploadSingleFile(file, idx) {
  return new Promise((resolve) => {
    const formData = new FormData();
    formData.append('multipartFile', file);
    const xhr = new XMLHttpRequest();
    const progressBar = document.getElementById(`progress-${idx}`);
    const statusSpan = document.getElementById(`status-${idx}`);
    const linkSpan = document.getElementById(`link-${idx}`);
    progressBar.style.width = '0%';
    statusSpan.textContent = '上传中...';
    statusSpan.className = 'status';
    xhr.upload.onprogress = function (e) {
      if (e.lengthComputable) {
        const percent = Math.round((e.loaded / e.total) * 100);
        progressBar.style.width = percent + '%';
      }
    };
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          progressBar.style.width = '100%';
          statusSpan.textContent = '上传成功';
          statusSpan.className = 'status success';
          linkSpan.innerHTML = `<a href="${xhr.responseText}" target="_blank">访问链接</a>`;
        } else {
          statusSpan.textContent = '上传失败';
          statusSpan.className = 'status error';
        }
        resolve();
      }
    };
    xhr.open('POST', '/api/uploadFile', true);
    xhr.send(formData);
  });
} 