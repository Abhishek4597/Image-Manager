# IMAGE MANAGER
📷 IRICEN Image Management & Retrieval System with Role-Based Access Control

## 📸 Application Screenshots

| **Login Interface** | **Dashboard View** |
|:---:|:---:|
| ![Login Page](https://github.com/Abhishek4597/Image-Manager/blob/33e3af76366fa775b82d9406167b4f603fc5522a/Screenshot%202026-01-09%20124313.png) | ![Dashboard](https://github.com/Abhishek4597/Image-Manager/blob/e54a148a673fa9f640b3d690f70401dbae76c705/Screenshot%202026-01-09%20123959.png) |
| *Modern gradient login with password toggle* | *Main gallery with role-based color coding* |

| **Upload Interface** | **Search Functionality** |
|:---:|:---:|
| ![Upload Page](https://github.com/Abhishek4597/Image-Manager/blob/137f43b8b4922d71987412a0a51a2a75757abf86/Screenshot%202026-01-09%20124010.png) | ![Search Page](https://github.com/Abhishek4597/Image-Manager/blob/436d7f634b36424d7b04c7740f4ca2b2f3ccee79/Screenshot%202026-01-09%20124032.png) |
| *File upload with preview and validation* | *Advanced search with filters and results* |

| **User Management** | **Image Viewer** |
|:---:|:---:|
| ![Create User](https://github.com/Abhishek4597/Image-Manager/blob/137f43b8b4922d71987412a0a51a2a75757abf86/Screenshot%202026-01-09%20124041.png) | ![Image Viewer](https://github.com/Abhishek4597/Image-Manager/blob/436d7f634b36424d7b04c7740f4ca2b2f3ccee79/Screenshot%202026-01-09%20124100.png) |
| *Admin interface for creating users with roles* | *Full-screen viewer with zoom and navigation* |

---

## 🔗 Live Demo & Repository Links

- **📁 GitHub Repository:** [github.com/Abhishek4597/Image-Manager](https://github.com/Abhishek4597/Image-Manager)
- **🔐 Demo Credentials:**
  - Admin: `admin` / `admin123`
  - Viewer: `viewer` / `viewer123`
  - Uploader: `uploader` / `uploader123`

---

## 🎯 Complete Features List

### 🔐 **User Management System**
| Feature | Description | Roles |
|---------|-------------|--------|
| **Multi-Role Authentication** | 5 distinct user roles with hierarchical permissions | ALL |
| **Password Security** | BCrypt encryption, strength validation, reset functionality | ALL |
| **Role-Based UI** | Visual color coding for each role (Gray/Blue/Green/Yellow/Red) | ALL |
| **User Creation** | Admin-only interface for creating new users with role assignment | ADMIN |
| **Session Management** | Secure session handling with timeout configuration | ALL |

### 🖼️ **Media Management**
| Feature | Description | Supported Formats |
|---------|-------------|-------------------|
| **Image Upload** | Drag & drop, batch upload, progress indicators | JPG, PNG, GIF, WebP |
| **Video Support** | Video upload and playback with thumbnail generation | MP4, AVI, MOV, WEBM |
| **Gallery View** | Grid/List toggle, pagination, sorting by date/size | ALL |
| **Advanced Viewer** | Zoom (mouse wheel), pan, fullscreen, slideshow mode | ALL |
| **Tag Management** | Add/remove tags, tag hierarchy, tag cloud visualization | TAGGER+ |

### 🔍 **Search & Discovery**
| Feature | Description | Search Options |
|---------|-------------|----------------|
| **Full-Text Search** | Search across titles, descriptions, tags | Text queries |
| **Advanced Filters** | Date range, file type, size, uploader filters | Multiple criteria |
| **Boolean Operators** | AND, OR, NOT operations with grouping | Complex queries |
| **Saved Searches** | Save frequent search queries for quick access | User-specific |
| **Visual Search** | Color-based filtering, similar image finder | Image analysis |

### 🏷️ **Tagging System**
| Feature | Description | Operations |
|---------|-------------|------------|
| **Tag Management** | Create, edit, delete, merge tags | CRUD operations |
| **Bulk Tagging** | Apply/remove tags to multiple images simultaneously | Batch processing |
| **Smart Tagging** | Auto-tag suggestions based on content and patterns | AI-assisted |
| **Tag Hierarchy** | Parent-child relationships, nested tags | Organization |
| **Tag Cloud** | Visual representation of tag popularity | Analytics |

### 📊 **Administration Features**
| Feature | Description | Admin Tools |
|---------|-------------|-------------|
| **File System Sync** | Sync existing files from server directories to database | ADMIN |
| **Bulk Operations** | Mass delete, mass tag, mass description updates | MODERATOR+ |
| **User Management** | Create, edit, disable users, role assignment | ADMIN |
| **System Analytics** | Storage usage, user activity, popular content reports | ADMIN |
| **Configuration** | Upload limits, allowed formats, security settings | ADMIN |

### 📱 **Responsive Design**
| Feature | Description | Device Support |
|---------|-------------|---------------|
| **Mobile Optimization** | Touch-friendly interface, gesture support | All mobile devices |
| **Responsive Layout** | Adaptive design for all screen sizes | Desktop, Tablet, Mobile |
| **Progressive Web App** | Installable on mobile, offline capability | Mobile browsers |
| **Accessibility** | Keyboard navigation, screen reader support | Accessibility tools |

---

## 📊 Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Backend Framework** | Spring Boot 3.x, Spring Security | Application core & security |
| **Frontend** | Thymeleaf, Bootstrap 5, JavaScript | User interface & templates |
| **Database** | MySQL 8.0 / H2 Database | Data persistence |
| **File Storage** | Local filesystem, Spring Multipart | Media file storage |
| **Build Tool** | Maven | Dependency management |
| **Server** | Embedded Tomcat 10 | Application server |
| **Security** | Spring Security, BCrypt | Authentication & encryption |
| **Version Control** | Git, GitHub | Source code management |

---

