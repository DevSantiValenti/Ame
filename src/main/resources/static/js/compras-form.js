let stock = {}; //Objeto que guardara el id y la cantidad en stock
const stocks = []; //Arreglo que contendra los objetos stock de las lineas agregadas 
// let esMayorista = false;

$(document).on('change', 'input.cantidad', function () {
    const id = $(this).data('id');
    const cantidad = parseInt($(this).val());
    const stockItem = stocks.find((item) => item.id == id);

    // if (stockItem) {
    //     const precioActual = esMayorista ? stockItem.precioMay : stockItem.precioMin;
    //     LineasUtil.calcularSubtotal(id, precioActual, cantidad);
    // }
});

// $('#mayorista').on('change', function () {
//     LineasUtil.toggleMayorista();
// });

$(function () {
    $('#buscar').autocomplete({
        source: (request, response) => {
            $.ajax({
                url: '/compras/buscar-productos/' + request.term, //forma clásica
                // url: `/ventas/buscar-productos/${request.term}`, //Con template String 
                dataType: 'json',
                data: {
                    term: request.term
                },
                success: (data) => {
                    response($.map(data, (item) => {

                        //Crear el objeto y el array stock
                        stock = { id: item.id, stock: item.stock, precioMin: item.precioMin, precioMay: item.precioMay };
                        stocks.push(stock);

                        return {
                            value: item.id,
                            label: `${item.descripcion} - Precio Mayorista: $${item.precioMay} - Precio Minorista: $${item.precioMin}`
                        }
                    }));
                }
            });
        },
        select: (event, ui) => {

            $('#buscar').autocomplete('close');
            $('#buscar').val('');

            //Crear una linea:
            let linea = $('#lineas').html();

            //Asignar los valores a las celdas de la tabla:
            let producto = ui.item.label;

            let descripcion = producto.split('-')[0];
            let precioMin = producto.split('-')[2].split('$')[1]; //Si pusiera 1, me traeria ""Precio Mayorista.....""
            // precioMin = precioMin.split('$')[1];

            let precioMay = producto.split('-')[1].split('$')[1]; //precio May
            // precioMay = precioMay.split('$')[1];

            let id = ui.item.value;

            // Verificar si el producto es repetido...
            if (LineasUtil.esRepetido(id)) {
                //Llamar a incrementarCantidad con el precio segun el estado del checkbox
                // const precio = $('#mayorista').is(':checked') ? precioMay : precioMin;
                LineasUtil.incrementarCantidad(id, precio);
                return false;
            }

            //Determinar el precio inicial segun el estado del checkbox
            const precioInicial = $('#mayorista').is(':checked') ? precioMay : precioMin;


            //Reemplazar los valores de la linea de la tabla auxiliar...
            linea = linea.replace(/{ID}/g, id);
            linea = linea.replace(/{DESCRIPCION}/g, descripcion);
            linea = linea.replace(/{PRECIOMAY}/g, precioMay);
            linea = linea.replace(/{PRECIOMIN}/g, precioInicial);

            //Calcular el subtotal inicial
            $('#tabla-productos tbody').append(linea);
            // LineasUtil.calcularSubtotal(id, precioInicial, 1);

            $('#buscar').val('');
            // $('#buscar').trigger('select');
            return false;
        }
    });
});

//Clase de utilidades de linea de venta
const LineasUtil = {

    incrementarCantidad: function (id, precioActual) {
        let cantidad = parseInt($(`#cantidad_${id}`).val()); //se pone el # por que es un ID
        $(`#cantidad_${id}`).val(++cantidad);

        //obtener el precio actualizado segun el estado del checkbox
        const stockItem = stocks.find((item) => item.id == id);
        precioActual = esMayorista ? stockItem.precioMay : stockItem.precioMin;

        this.calcularSubtotal(id, precioActual, cantidad);
    },
    esRepetido: (id) => {
        let result = false;
        $('input[name="item_id[]"]').each(function () {
            if (parseInt(id) === parseInt($(this).val())) {
                result = true;
            }
        });
        return result;
    },
    // calcularSubtotal: function (id, precioActual, cantidad) { //,mayorista

    //     const stockItem = stocks.find(i => i.id === id);
    //     // const precioActual = esMayorista ? stockItem.precioMay : stockItem.precioMin;

    //     if (!stockItem) {
    //         console.error(`Producto con ID ${ID} no encontrado en stocks.`);
    //         return;
    //     }

    //     if (cantidad > stockItem.stock) {
    //         Swal.fire({
    //             icon: 'error',
    //             title: 'Oooops...',
    //             text: 'No hay stock suficiente...'
    //         });
    //         $(`#cantidad_${id}`).val(stockItem.stock);
    //     } else {
    //         const subtotal = precioActual * cantidad;
    //         // $(`#subtotal_${id}`).html(parseInt(precioActual) * parseInt(cantidad));
    //         $(`#subtotal_${id}`).html(subtotal);
    //         this.calcularTotal();
    //     }
    // },
    // calcularTotal: function () {
    //     let total = 0;
    //     $('span[id^="subtotal_"]').each(function () {
    //         total += parseInt($(this).html());
    //     });
    //     $('#total').html(`$${total}`);
    //     $('#totalHidden').val(total); // Actualizar el valor del campo oculto
    // },
    borrarLinea: function (id) {
        $(`#fila_${id}`).remove();
        this.calcularTotal();
    }
    // toggleMayorista: function () {
    //     esMayorista = $('#mayorista').is(':checked');
    //     // esMayorista =!esMayorista;

    //     $('input[name="item_id[]"]').each(function () {
    //         const id = $(this).val();
    //         // const precioMin = stocks.find(i => i.id === id).precioMin;
    //         // const precioMay = stocks.find(i => i.id === id).precioMay;
    //         const stockItem = stocks.find((item) => item.id == id);

    //         if (stockItem) {
    //             //Cambiar precios en tiempo real(sin recargar)
    //             const precioActual = esMayorista ? stockItem.precioMay : stockItem.precioMin;
    //             const cantidad = $(`#cantidad_${id}`).val();

    //             //Actualizar el subtotal en la tabla
    //             $(`#subtotal_${id}`).html(parseInt(precioActual) * parseInt(cantidad));
    //         }



    //     });

    //     // this.calcularSubtotal();
    //     this.calcularTotal();
    // }
}