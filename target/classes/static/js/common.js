let toastInstance = null;

document.addEventListener('DOMContentLoaded', function() {
    const toastEl = document.getElementById('toast');
    if (toastEl) {
        toastInstance = new bootstrap.Toast(toastEl);
    }

    initMenuToggle();
    initActiveMenu();
});

function initMenuToggle() {
    document.querySelectorAll('.menu-item[onclick]').forEach(function(item) {
        item.removeAttribute('onclick');
        item.addEventListener('click', function(e) {
            if (!this.hasAttribute('data-route')) {
                e.preventDefault();
                toggleMenu(this);
            }
        });
    });
}

function initActiveMenu() {
    const currentPath = window.location.pathname;
    
    document.querySelectorAll('.menu-item[data-route]').forEach(function(item) {
        const route = item.getAttribute('data-route');
        const routePath = getRoutePath(route);
        
        if (currentPath === routePath || currentPath.startsWith(routePath + '/')) {
            item.classList.add('active');
            
            const parentSubmenu = item.closest('.submenu');
            if (parentSubmenu) {
                parentSubmenu.classList.add('show');
                const parentMenuItem = parentSubmenu.previousElementSibling;
                if (parentMenuItem) {
                    parentMenuItem.classList.add('expanded');
                }
            }
        }
    });
}

function getRoutePath(route) {
    const routes = {
        'dashboard': '/',
        'system-user': '/system/user',
        'system-role': '/system/role',
        'system-permission': '/system/permission',
        'agent': '/agent',
        'merchant': '/merchant',
        'product': '/product',
        'machine': '/machine',
        'agent-account': '/agent-account',
        'profit': '/profit',
        'account-detail': '/account-detail',
        'statement': '/statement',
        'channel': '/channel',
        'transaction': '/transaction'
    };
    return routes[route] || '/';
}

function toggleMenu(menuItem) {
    const submenu = menuItem.nextElementSibling;
    if (submenu && submenu.classList.contains('submenu')) {
        menuItem.classList.toggle('expanded');
        submenu.classList.toggle('show');
    }
}

function showLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.classList.add('show');
    }
}

function hideLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.classList.remove('show');
    }
}

function showToast(type, message) {
    const toastTitle = document.getElementById('toast-title');
    const toastMessage = document.getElementById('toast-message');
    const toastEl = document.getElementById('toast');

    if (!toastEl) return;

    const titles = {
        'success': '成功',
        'error': '错误',
        'warning': '警告',
        'info': '提示'
    };

    if (toastTitle) {
        toastTitle.textContent = titles[type] || '提示';
    }
    if (toastMessage) {
        toastMessage.textContent = message;
    }

    toastEl.className = 'toast';
    if (type === 'success') {
        toastEl.classList.add('bg-success', 'text-white');
    } else if (type === 'error') {
        toastEl.classList.add('bg-danger', 'text-white');
    } else if (type === 'warning') {
        toastEl.classList.add('bg-warning');
    } else {
        toastEl.classList.add('bg-info', 'text-white');
    }

    if (toastInstance) {
        toastInstance.show();
    }
}

function getLevelLabel(level) {
    const labels = { 1: '一级代理', 2: '二级代理', 3: '三级代理', 4: '四级代理' };
    return labels[level] || `第${level}级代理`;
}

function getStatusBadge(status) {
    if (status === 1) return '<span class="badge bg-success">启用</span>';
    return '<span class="badge bg-danger">禁用</span>';
}

function getMachineStatusBadge(status) {
    const badges = {
        0: '<span class="badge bg-secondary">库存</span>',
        1: '<span class="badge bg-info">已出库</span>',
        2: '<span class="badge bg-success">已绑定</span>',
        3: '<span class="badge bg-danger">故障</span>'
    };
    return badges[status] || '<span class="badge bg-secondary">未知</span>';
}

function getMachineStatusLabel(status) {
    const labels = { 0: '库存', 1: '已出库', 2: '已绑定', 3: '故障' };
    return labels[status] || '未知';
}

function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return num.toLocaleString('zh-CN', { maximumFractionDigits: 2 });
}

function formatDateTime(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN');
}

function renderPagination(total, currentPage, pageSize, callback) {
    const totalPages = Math.ceil(total / pageSize);
    let html = '';
    
    html += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="${callback}(${currentPage - 1})">上一页</a>
    </li>`;
    
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
            <a class="page-link" href="javascript:void(0)" onclick="${callback}(${i})">${i}</a>
        </li>`;
    }
    
    html += `<li class="page-item ${currentPage === totalPages || totalPages === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="${callback}(${currentPage + 1})">下一页</a>
    </li>`;
    
    return html;
}
