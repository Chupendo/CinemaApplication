// Obtener el botón para alternar el modo oscuro
const toggleButton = document.getElementById('darkModeToggle');

// Obtener el elemento del sidebar que tiene el offcanvas
const offcanvas = document.querySelector('.offcanvas');

// Obtener el ícono del botón que cambia el modo (sol/luna)
const iconMode = document.getElementById('iconMode');

// Obtener todos los íconos de la barra lateral para el modo claro
const sidebarIconsLight = document.querySelectorAll('.sidebar-icon-light');

// Obtener todos los íconos de la barra lateral para el modo oscuro
const sidebarIconsDark = document.querySelectorAll('.sidebar-icon-dark');

// Obtener el ícono de cerrar del offcanvas
const iconClose = document.getElementById('iconClose');

// Obtener la tabla del lisatdo de usuairo
const table = document.getElementById('userTable');

// Obtener el botón de cerrar del offcanvas
const dropdownSidebarFilms = document.getElementById('dropdownSidebarFilms');
const dropdownSidebarUsers = document.getElementById('dropdownSidebarUsers');

// Función para actualizar el botón y los elementos según el modo actual
function actualizarBoton() {
    // Verificar si la página está en modo oscuro (si la clase 'dark-mode' está activa en el body)
    if (document.body.classList.contains('dark-mode')) {
        // toggleButton.innerHTML = '<i class="bi bi-brightness-high-fill"></i> Modo Claro';
        // toggleButton.innerHTML = '<i class="bi bi-brightness-high-fill"></i>';
        //toggleButton.classList.remove('btn-dark');
        //toggleButton.classList.add('btn-light');

        // Cambiar el ícono del sol a luna (modo oscuro)
        iconMode.classList.remove('bi-brightness-high-fill'); // Elimina el ícono del sol
        iconMode.classList.add('bi-moon-fill'); // Añade el ícono de la luna

        // Cambiar el fondo del offcanvas de claro a oscuro
        offcanvas.classList.remove('bg-light');
        offcanvas.classList.add('bg-dark');

        // Mostrar los íconos del modo oscuro y ocultar los del modo claro
        sidebarIconsLight.forEach(icon => icon.style.display = 'none');
        sidebarIconsDark.forEach(icon => icon.style.display = 'block');

        // Cambiar ícono del botón de cerrar al modo claro (blanco)
        iconClose.classList.remove('bi-x');
        iconClose.classList.add('bi-x-circle'); // o cualquier otro ícono de Bootstrap Icons

        // Cambiar el fondo de la tabla
        table?.classList.remove('table-light');
        table?.classList.add('table-dark');

        // Cambiar el fondo de las tarjetas
        document.querySelectorAll('.card').forEach(card => {
            card.classList.remove('bg-light', 'text-dark');
            card.classList.add('bg-dark', 'text-white');
        });

        // Cambiar el fondo de la tabla
        // dropdownSidebarFilms?.classList.remove('dropdown-menu');
        dropdownSidebarFilms?.classList.add('dropdown-menu-dark');
        // dropdownSidebarUsers?.classList.remove('dropdown-menu');
        dropdownSidebarUsers?.classList.add('dropdown-menu-dark');

    } else {
        // toggleButton.innerHTML = '<i class="bi bi-moon-fill"></i> Modo Oscuro';
        // toggleButton.innerHTML = '<i class="bi bi-moon-fill"></i>';
        //toggleButton.classList.remove('btn-light');
        //toggleButton.classList.add('btn-dark');

        // Cambiar el ícono de la luna a sol (modo claro)
        iconMode.classList.remove('bi-moon-fill'); // Elimina el ícono de la luna
        iconMode.classList.add('bi-brightness-high-fill'); // Añade el ícono del sol

        // Cambiar el fondo del offcanvas de oscuro a claro
        offcanvas.classList.remove('bg-dark');
        offcanvas.classList.add('bg-light');

        // Mostrar los íconos del modo claro y ocultar los del modo oscuro
        sidebarIconsLight.forEach(icon => icon.style.display = 'block');
        sidebarIconsDark.forEach(icon => icon.style.display = 'none');

        // Cambiar ícono del botón de cerrar al modo oscuro (negro)
        iconClose.classList.remove('bi-x-circle');
        iconClose.classList.add('bi-x');

        // Cambiar el fondo de la tabla
        table?.classList.remove('table-dark');
        table?.classList.add('table-light');

        // Cambiar el fondo de las tarjetas
        document.querySelectorAll('.card').forEach(card => {
            card.classList.remove('bg-dark', 'text-white');
            card.classList.add('bg-light', 'text-dark');
        });

        // Cambiar el fondo de la tabla
        dropdownSidebarFilms?.classList.remove('dropdown-menu-dark');
        // dropdownSidebarFilms?.classList.add('dropdown-menu');
        dropdownSidebarUsers?.classList.remove('dropdown-menu-dark');
        // dropdownSidebarUsers?.classList.add('dropdown-menu');
    }
}

// Leer la preferencia guardada al cargar la página
// Verifica si la preferencia del modo oscuro ha sido guardada en localStorage
if (localStorage.getItem('modo') === 'oscuro') {
    document.body.classList.add('dark-mode'); // Si se guardó "oscuro", aplica el modo oscuro
}

// // Llamar a la función para inicializar el modo según la preferencia almacenada o el estado actual
actualizarBoton();

// Agregar un event listener al botón para escuchar el clic
toggleButton.addEventListener('click', () => {
    // Alternar la clase 'dark-mode' en el body para cambiar entre modo oscuro y claro
    document.body.classList.toggle('dark-mode');

    // Guardar la preferencia del modo en localStorage
    if (document.body.classList.contains('dark-mode')) {
        // Si está activado el modo oscuro, guardar la preferencia como 'oscuro'
        localStorage.setItem('modo', 'oscuro');
    } else {
        // Si está desactivado el modo oscuro, guardar la preferencia como 'claro'
        localStorage.setItem('modo', 'claro');
    }

    // Llamar a la función para actualizar el ícono y otros elementos visuales según el nuevo modo
    actualizarBoton();
});