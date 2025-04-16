document.addEventListener('DOMContentLoaded', () => {
    localStorage.removeItem('carrito');
    const carrito = JSON.parse(localStorage.getItem('carrito')) || [];
    const contadorCarrito = document.getElementById('contador-carrito');
    const carritoPanel = document.getElementById('carrito-panel');
    const tablaCarrito = document.getElementById('tabla-carrito');
    const totalCarrito = document.getElementById('total-carrito');
    const alertContainer = document.getElementById('alert-container');
    const botonCarrito = document.getElementById('cart-button');
    const formPago = document.getElementById('form-pago');
    const inputsCarrito = document.getElementById('inputs-carrito');

    // Función para actualizar los inputs ocultos en el formulario
    function actualizarFormulario() {
        if (!inputsCarrito) return;
        inputsCarrito.innerHTML = ''; // Limpiamos inputs previos

        carrito.forEach(producto => {
            const inputId = document.createElement('input');
            inputId.type = 'hidden';
            inputId.name = 'item_ids[]';
            inputId.value = producto.id;
            inputsCarrito.appendChild(inputId);

            const inputCantidad = document.createElement('input');
            inputCantidad.type = 'hidden';
            inputCantidad.name = 'cantidad_[]';
            inputCantidad.value = producto.cantidad;
            inputsCarrito.appendChild(inputCantidad);
        });
    }

    // Se llama cada vez que el carrito se actualiza
    function actualizarCarrito() {
        localStorage.setItem('carrito', JSON.stringify(carrito));
        actualizarPanelCarrito();
        actualizarContador();
        actualizarFormulario();
    }

    function actualizarContador() {
        const totalCantidad = carrito.reduce((sum, producto) => sum + producto.cantidad, 0);
        contadorCarrito.textContent = totalCantidad;
    }

    function mostrarAlerta(mensaje) {
        alertContainer.innerHTML = '';
        const alerta = document.createElement('div');
        alerta.className = 'alert alert-info alert-dismissible fade show';
        alerta.setAttribute('role', 'alert');
        alerta.innerHTML = `
            <i class='bx bx-info-circle bx-tada'></i>&nbsp;<span>${mensaje}</span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        `;
        alertContainer.appendChild(alerta);

        setTimeout(() => {
            alerta.classList.remove('show');
            alerta.classList.add('fade');
            setTimeout(() => alerta.remove(), 500);
        }, 5000);
    }

    function manejarCambioCantidad(id, accion) {
        const index = carrito.findIndex(p => p.id === id);

        if (index !== -1) {
            const producto = carrito[index];

            if (accion === 'incrementar') {
                if (producto.cantidad < producto.stock) {
                    producto.cantidad++;
                } else {
                    mostrarAlerta(`No puedes agregar más unidades de "${producto.nombre}". Stock disponible: ${producto.stock}`);
                    return;
                }
            } else if (accion === 'disminuir') {
                if (producto.cantidad > 1) {
                    producto.cantidad--;
                } else {
                    carrito.splice(index, 1);
                }
            }

            actualizarCarrito();
        }
    }

    function calcularTotal() {
        return carrito.reduce((total, producto) => total + parseFloat(producto.precio) * parseInt(producto.cantidad), 0);
    }

    function actualizarPanelCarrito() {
        const tbody = tablaCarrito.querySelector('tbody');
        tbody.innerHTML = '';

        if (carrito.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">El carrito está vacío</td></tr>';
            totalCarrito.textContent = 'Total: $0';
            return;
        }

        carrito.forEach((producto, index) => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td><img src="${producto.imagen}" alt="Producto" style="width: 50px; height: 50px;"></td>
                <td>${producto.nombre}</td>
                <td>$${producto.precio}</td>
                <td>
                    <button class="btn btn-sm btn-secondary btn-disminuir">-</button>
                    <span>${producto.cantidad}</span>
                    <button class="btn btn-sm btn-secondary btn-incrementar">+</button>
                </td>
                <td>
                    <button class="btn btn-danger btn-sm btn-quitar" data-index="${index}">&times;</button>
                </td>
            `;
            tbody.appendChild(fila);
        });

        tbody.querySelectorAll('.btn-incrementar').forEach((btn, i) => {
            btn.addEventListener('click', () => manejarCambioCantidad(carrito[i].id, 'incrementar'));
        });

        tbody.querySelectorAll('.btn-disminuir').forEach((btn, i) => {
            btn.addEventListener('click', () => manejarCambioCantidad(carrito[i].id, 'disminuir'));
        });

        tbody.querySelectorAll('.btn-quitar').forEach((btn, i) => {
            btn.addEventListener('click', () => {
                carrito.splice(i, 1);
                actualizarCarrito();
            });
        });

        totalCarrito.textContent = `Total: $${calcularTotal().toFixed(2)}`;
        actualizarContador();
    }

    document.querySelectorAll('.btn-agregar-carrito').forEach(boton => {
        boton.addEventListener('click', (e) => {
            const productoId = e.target.dataset.productoId;
            const productoNombre = e.target.dataset.productoNombre;
            const productoPrecio = e.target.dataset.productoPrecio;
            const productoImagen = e.target.dataset.productoImagen;
            const productoStock = parseInt(e.target.dataset.productoStock);

            const productoExistente = carrito.find(p => p.id === productoId);
            if (productoExistente) {
                if (productoExistente.cantidad < productoStock) {
                    productoExistente.cantidad++;
                } else {
                    mostrarAlerta(`No puedes agregar más unidades de "${productoNombre}". Stock disponible: ${productoStock}`);
                    return;
                }
            } else {
                carrito.push({
                    id: productoId,
                    nombre: productoNombre,
                    precio: productoPrecio,
                    imagen: productoImagen,
                    cantidad: 1,
                    stock: productoStock
                });
            }

            actualizarCarrito();
        });
    });

    botonCarrito.addEventListener('click', () => {
        carritoPanel.classList.toggle('d-none');
        actualizarPanelCarrito();
    });

    document.getElementById('pagar').addEventListener('click', () => {
        fetch('/createAndRedirect', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(carrito),
        })
        .then(response => response.text())
        .then(url => {
            window.location.href = url;
        })
        .catch(error => console.error('Error:', error));
    });

    actualizarContador();
    actualizarFormulario();
});

