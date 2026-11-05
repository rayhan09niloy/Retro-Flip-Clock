const http = require('http');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const PORT = parseInt(process.env.DEFAULT_APP_PORT || process.env.PORT || '3000', 10);
const PUBLIC_DIR = path.join(__dirname, 'public');
const APK_PATH = path.join(__dirname, 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk');

const MIME_TYPES = {
  '.html': 'text/html; charset=UTF-8',
  '.js': 'application/javascript; charset=UTF-8',
  '.css': 'text/css; charset=UTF-8',
  '.json': 'application/json; charset=UTF-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.ttf': 'font/ttf',
  '.apk': 'application/vnd.android.package-archive'
};

const server = http.createServer((req, res) => {
  const urlPath = req.url.split('?')[0];

  // APK direct download
  if (urlPath === '/download-apk' || urlPath === '/api/apk') {
    if (fs.existsSync(APK_PATH)) {
      const stat = fs.statSync(APK_PATH);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="retro-flip-clock-debug.apk"'
      });
      fs.createReadStream(APK_PATH).pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK build not found yet.');
      return;
    }
  }

  // Health check
  if (urlPath === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'healthy', timestamp: new Date().toISOString() }));
    return;
  }

  // File serving
  let targetFile = urlPath === '/' ? path.join(PUBLIC_DIR, 'index.html') : path.join(PUBLIC_DIR, urlPath);

  if (!targetFile.startsWith(PUBLIC_DIR)) {
    res.writeHead(403, { 'Content-Type': 'text/plain' });
    res.end('Forbidden');
    return;
  }

  if (!fs.existsSync(targetFile) || fs.statSync(targetFile).isDirectory()) {
    targetFile = path.join(PUBLIC_DIR, 'index.html');
  }

  const ext = path.extname(targetFile).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';

  fs.readFile(targetFile, (err, data) => {
    if (err) {
      res.writeHead(500, { 'Content-Type': 'text/plain' });
      res.end('Internal Server Error');
      return;
    }
    res.writeHead(200, {
      'Content-Type': contentType,
      'Cache-Control': ext === '.html' ? 'no-cache, no-store, must-revalidate' : 'public, max-age=3600'
    });
    res.end(data);
  });
});

let retryCount = 0;
server.on('error', (err) => {
  if (err.code === 'EADDRINUSE' && retryCount < 3) {
    retryCount++;
    console.log(`[Dev Server] Port ${PORT} busy, clearing stale processes (attempt ${retryCount})...`);
    try {
      const output = execSync(`ss -tlpn | grep :${PORT} || true`, { encoding: 'utf8' });
      const matches = output.match(/pid=(\d+)/g) || [];
      for (const m of matches) {
        const pid = parseInt(m.replace('pid=', ''), 10);
        if (pid && pid !== process.pid) {
          try { process.kill(pid, 'SIGKILL'); } catch (e) {}
        }
      }
    } catch (e) {}
    setTimeout(() => {
      server.listen(PORT, '0.0.0.0');
    }, 600);
    return;
  }
  console.error('[Dev Server] Server error:', err);
  process.exit(1);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Dev Server] Retro Flip Clock active on http://0.0.0.0:${PORT}`);
});
