// 书签管理类
export class BookmarkManager {
    constructor() {
        // 存储默认书签文件夹ID
        this.defaultFolderId = '1';
        
        // 默认排序选项
        this.defaultSortOptions = {
            by: 'dateAdded',  // title, url, dateAdded
            order: 'desc'     // asc, desc
        };
    }

    // 创建书签
    async createBookmark(bookmark) {
        try {
            const newBookmark = await chrome.bookmarks.create({
                parentId: bookmark.parentId || this.defaultFolderId,
                title: bookmark.title,
                url: bookmark.url
            });
            console.log('Bookmark created:', newBookmark);
            return newBookmark;
        } catch (error) {
            console.error('Error creating bookmark:', error);
            throw error;
        }
    }

    // 创建书签文件夹
    async createFolder(folderName, parentId = this.defaultFolderId) {
        try {
            const newFolder = await chrome.bookmarks.create({
                parentId: parentId,
                title: folderName
            });
            console.log('Folder created:', newFolder);
            return newFolder;
        } catch (error) {
            console.error('Error creating folder:', error);
            throw error;
        }
    }

    // 获取所有书签
    async getAllBookmarks() {
        try {
            const tree = await chrome.bookmarks.getTree();
            return this.flattenBookmarkTree(tree);
        } catch (error) {
            console.error('Error getting bookmarks:', error);
            throw error;
        }
    }

    // 搜索书签
    async searchBookmarks(query) {
        try {
            const results = await chrome.bookmarks.search(query);
            console.log('Search results:', results);
            return results;
        } catch (error) {
            console.error('Error searching bookmarks:', error);
            throw error;
        }
    }

    // 获取最近添加的书签
    async getRecentBookmarks(maxResults = 10) {
        try {
            const allBookmarks = await this.getAllBookmarks();
            const sortedBookmarks = allBookmarks
                .filter(bookmark => bookmark.url) // 只获取书签，不包括文件夹
                .sort((a, b) => b.dateAdded - a.dateAdded)
                .slice(0, maxResults);
            
            console.log('Recent bookmarks:', sortedBookmarks);
            return sortedBookmarks;
        } catch (error) {
            console.error('Error getting recent bookmarks:', error);
            throw error;
        }
    }

    // 更新书签
    async updateBookmark(id, changes) {
        try {
            const updated = await chrome.bookmarks.update(id, changes);
            console.log('Bookmark updated:', updated);
            return updated;
        } catch (error) {
            console.error('Error updating bookmark:', error);
            throw error;
        }
    }

    // 移动书签
    async moveBookmark(id, destination) {
        try {
            const moved = await chrome.bookmarks.move(id, destination);
            console.log('Bookmark moved:', moved);
            return moved;
        } catch (error) {
            console.error('Error moving bookmark:', error);
            throw error;
        }
    }

    // 删除书签
    async deleteBookmark(id) {
        try {
            await chrome.bookmarks.remove(id);
            console.log('Bookmark deleted:', id);
            return true;
        } catch (error) {
            console.error('Error deleting bookmark:', error);
            throw error;
        }
    }

    // 删除书签文件夹（包括其中的所有书签）
    async deleteFolder(id) {
        try {
            await chrome.bookmarks.removeTree(id);
            console.log('Folder and its contents deleted:', id);
            return true;
        } catch (error) {
            console.error('Error deleting folder:', error);
            throw error;
        }
    }

    // 获取书签树
    async getBookmarkTree() {
        try {
            const tree = await chrome.bookmarks.getTree();
            console.log('Bookmark tree:', tree);
            return tree;
        } catch (error) {
            console.error('Error getting bookmark tree:', error);
            throw error;
        }
    }

    // 导出书签
    async exportBookmarks() {
        try {
            const bookmarks = await this.getAllBookmarks();
            const bookmarksJson = JSON.stringify(bookmarks, null, 2);
            
            // 创建并下载文件
            const blob = new Blob([bookmarksJson], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            
            await chrome.downloads.download({
                url: url,
                filename: `bookmarks_${new Date().toISOString()}.json`,
                saveAs: true
            });
            
            URL.revokeObjectURL(url);
            console.log('Bookmarks exported successfully');
            return true;
        } catch (error) {
            console.error('Error exporting bookmarks:', error);
            throw error;
        }
    }

    // 导入书签
    async importBookmarks(jsonFile, parentId = this.defaultFolderId) {
        try {
            const bookmarks = JSON.parse(jsonFile);
            const importFolder = await this.createFolder('Imported Bookmarks', parentId);
            
            for (const bookmark of bookmarks) {
                if (bookmark.url) {
                    await this.createBookmark({
                        parentId: importFolder.id,
                        title: bookmark.title,
                        url: bookmark.url
                    });
                }
            }
            
            console.log('Bookmarks imported successfully');
            return true;
        } catch (error) {
            console.error('Error importing bookmarks:', error);
            throw error;
        }
    }

    // 辅助方法：展平书签树
    flattenBookmarkTree(tree) {
        const bookmarks = [];
        
        function traverse(nodes) {
            for (const node of nodes) {
                bookmarks.push(node);
                if (node.children) {
                    traverse(node.children);
                }
            }
        }
        
        traverse(tree);
        return bookmarks;
    }

    // 获取书签统计信息
    async getBookmarkStats() {
        try {
            const allBookmarks = await this.getAllBookmarks();
            const bookmarks = allBookmarks.filter(b => b.url);
            const folders = allBookmarks.filter(b => !b.url);
            
            return {
                totalBookmarks: bookmarks.length,
                totalFolders: folders.length,
                mostRecentBookmark: bookmarks.sort((a, b) => b.dateAdded - a.dateAdded)[0],
                oldestBookmark: bookmarks.sort((a, b) => a.dateAdded - b.dateAdded)[0],
                averageDepth: this.calculateAverageDepth(allBookmarks)
            };
        } catch (error) {
            console.error('Error getting bookmark stats:', error);
            throw error;
        }
    }

    // 计算平均深度
    calculateAverageDepth(bookmarks) {
        let totalDepth = 0;
        let count = 0;
        
        bookmarks.forEach(bookmark => {
            if (bookmark.url) {
                let depth = 0;
                let current = bookmark;
                while (current.parentId) {
                    depth++;
                    current = bookmarks.find(b => b.id === current.parentId);
                }
                totalDepth += depth;
                count++;
            }
        });
        
        return count > 0 ? (totalDepth / count).toFixed(2) : 0;
    }
}

// 创建单例实例
const bookmarkManager = new BookmarkManager();
export default bookmarkManager; 