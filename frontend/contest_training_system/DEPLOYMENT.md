# 🚀 竞赛集训系统 - 前端部署指南

## 📦 构建产物位置

**构建产物目录:** `dist/`

构建命令生成的所有静态文件都在 `dist` 目录中,包括:
- `index.html` - 入口 HTML 文件
- `assets/` - 所有的 JS、CSS、字体等静态资源

---

## 🔧 Nginx 部署步骤

### 方法 1: Docker 部署 (推荐)

#### 1. 创建 Dockerfile

在项目根目录已有构建产物时,创建 `Dockerfile`:

```dockerfile
FROM nginx:alpine

# 复制构建产物到 Nginx 目录
COPY dist/ /usr/share/nginx/html/

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 80 端口
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### 2. 构建并运行容器

```bash
# 构建 Docker 镜像
docker build -t contest-frontend:latest .

# 运行容器
docker run -d -p 80:80 --name contest-frontend contest-frontend:latest
```

---

### 方法 2: 直接部署到 Nginx

#### Windows 部署

**1. 下载安装 Nginx**
```powershell
# 下载地址: http://nginx.org/en/download.html
# 或使用 Chocolatey 安装
choco install nginx
```

**2. 复制构建产物**
```powershell
# 复制 dist 目录内容到 Nginx html 目录
Copy-Item -Path ".\dist\*" -Destination "C:\nginx\html\" -Recurse -Force
```

**3. 配置 Nginx**
```powershell
# 将 nginx.conf 复制到 Nginx 配置目录
Copy-Item -Path ".\nginx.conf" -Destination "C:\nginx\conf\conf.d\contest.conf"
```

**4. 启动 Nginx**
```powershell
cd C:\nginx
.\nginx.exe

# 重新加载配置
.\nginx.exe -s reload

# 停止服务
.\nginx.exe -s stop
```

**5. 访问应用**
```
http://localhost
```

---

#### Linux 部署

**1. 安装 Nginx**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

**2. 复制构建产物**
```bash
# 上传 dist 目录到服务器
scp -r dist/* user@your-server:/var/www/contest/

# 或在服务器上直接复制
sudo cp -r dist/* /var/www/contest/
```

**3. 配置 Nginx**
```bash
# 复制配置文件
sudo cp nginx.conf /etc/nginx/sites-available/contest

# 创建软链接
sudo ln -s /etc/nginx/sites-available/contest /etc/nginx/sites-enabled/

# 修改配置文件中的路径
sudo nano /etc/nginx/sites-available/contest
# 将 root 改为: /var/www/contest
```

**4. 测试并启动**
```bash
# 测试配置文件
sudo nginx -t

# 重启 Nginx
sudo systemctl restart nginx

# 设置开机自启
sudo systemctl enable nginx
```

**5. 配置防火墙**
```bash
# Ubuntu UFW
sudo ufw allow 'Nginx Full'

# CentOS firewalld
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

---

## ⚙️ 重要配置说明

### 1. 修改后端 API 地址

在 `nginx.conf` 中修改:
```nginx
location /api/ {
    proxy_pass http://your-backend-server:8080/api/;
    # 替换为你的实际后端地址
}
```

### 2. 配置域名

在 `nginx.conf` 中修改:
```nginx
server_name contest.example.com;  # 改成你的域名
```

### 3. 配置 HTTPS (生产环境必需)

使用 Let's Encrypt 免费证书:

```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书并自动配置
sudo certbot --nginx -d contest.example.com

# 自动续期
sudo certbot renew --dry-run
```

---

## 🔍 验证部署

部署完成后,访问以下 URL 进行验证:

1. **首页**: `http://your-domain/`
2. **登录页**: `http://your-domain/login`
3. **静态资源**: `http://your-domain/assets/` (检查是否能加载 JS/CSS)

### 常见问题排查

**问题 1: 刷新页面出现 404**
- 原因: SPA 路由配置不正确
- 解决: 确保 `try_files $uri $uri/ /index.html;` 配置存在

**问题 2: API 请求失败**
- 原因: 后端代理配置错误
- 解决: 检查 `proxy_pass` 地址是否正确,后端服务是否运行

**问题 3: 静态资源加载失败**
- 原因: 路径配置错误或权限问题
- 解决: 检查 `root` 路径,确保 Nginx 有读取权限

---

## 📊 性能优化建议

1. **启用 Gzip 压缩** (配置文件已包含)
2. **配置静态资源缓存** (配置文件已包含)
3. **使用 CDN** 加速静态资源
4. **启用 HTTP/2** 提升性能
5. **配置 Brotli 压缩** (比 Gzip 更高效)

---

## 🔒 安全建议

1. ✅ 使用 HTTPS (Let's Encrypt 免费证书)
2. ✅ 配置 CSP (Content Security Policy) 头
3. ✅ 禁用不必要的 HTTP 方法
4. ✅ 隐藏 Nginx 版本信息
5. ✅ 配置速率限制防止 DDoS

---

## 📝 快速部署命令总结

```powershell
# 1. 构建项目
pnpm build

# 2. 打包 dist 目录
Compress-Archive -Path .\dist\* -DestinationPath contest-frontend-dist.zip

# 3. 上传到服务器并解压
# scp contest-frontend-dist.zip user@server:/tmp/
# ssh user@server
# unzip /tmp/contest-frontend-dist.zip -d /var/www/contest/

# 4. 配置并重启 Nginx
# sudo cp nginx.conf /etc/nginx/sites-available/contest
# sudo ln -s /etc/nginx/sites-available/contest /etc/nginx/sites-enabled/
# sudo nginx -t && sudo systemctl restart nginx
```

---

## 🎯 部署检查清单

- [ ] 构建项目 (`pnpm build`)
- [ ] 检查 dist 目录内容
- [ ] 配置 Nginx 反向代理
- [ ] 修改后端 API 地址
- [ ] 配置域名和 HTTPS
- [ ] 测试所有路由是否正常
- [ ] 测试 API 请求是否成功
- [ ] 配置防火墙规则
- [ ] 设置 Nginx 开机自启
- [ ] 监控日志和错误

---

**部署完成!** 🎉

如有问题,请检查:
- Nginx 错误日志: `/var/log/nginx/error.log`
- Nginx 访问日志: `/var/log/nginx/access.log`
- 浏览器控制台错误信息
