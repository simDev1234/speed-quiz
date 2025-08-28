document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname;
    const menuItems = document.querySelectorAll('.menu-item');

    menuItems.forEach(item => {
        const href = item.getAttribute('href');
        // 정확히 일치 또는 하위 경로도 포함되도록 startsWith 사용
        if (currentPath === href || currentPath.startsWith(href + "/")) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
});
