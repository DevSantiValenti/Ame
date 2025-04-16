package com.analistas.amesistema.amesistema.web.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.analistas.amesistema.amesistema.model.domain.Cliente;
import com.analistas.amesistema.amesistema.model.domain.LineaVenta;
import com.analistas.amesistema.amesistema.model.domain.MetodoPago;
import com.analistas.amesistema.amesistema.model.domain.Movimiento;
import com.analistas.amesistema.amesistema.model.domain.Pedido;
import com.analistas.amesistema.amesistema.model.domain.Producto;
import com.analistas.amesistema.amesistema.model.domain.Usuario;
import com.analistas.amesistema.amesistema.model.domain.Venta;
import com.analistas.amesistema.amesistema.model.service.IClienteService;
import com.analistas.amesistema.amesistema.model.service.IMetodoPagoService;
import com.analistas.amesistema.amesistema.model.service.IMovimientoService;
import com.analistas.amesistema.amesistema.model.service.IPedidoService;
import com.analistas.amesistema.amesistema.model.service.IProductoService;
import com.analistas.amesistema.amesistema.model.service.IUsuarioService;
import com.analistas.amesistema.amesistema.model.service.IVentaService;
import com.google.gson.GsonBuilder;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.MerchantOrder;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({ "venta", "pedido" })
public class MercadoPagoController {


	@Autowired
	IProductoService productoService;

	@Autowired
	IMovimientoService movimientoService;

	@Autowired
	IVentaService ventaService;

	@Autowired
	IMetodoPagoService metodoPagoService;

	@Autowired
	IClienteService clienteService;

	@Autowired
	IUsuarioService usuarioService;

	@Autowired
	IPedidoService pedidoService;

	// MercadoPagoController(ICategoriaRepository ICategoriaRepository) {
	// this.ICategoriaRepository = ICategoriaRepository;
	// }
	/*
	 * Datos para crear un pago de prueba, provistos por Mercado Pago
	 * La primera tarjeta de crédito es una prueba de 'Pago Aprobado'
	 * La segunda es una prueba de 'Pago rechazado'
	 * 
	 * La url para acceder a este método es 'localhost:8080/createAndRedirect'
	 *
	 * Sandbox Credit Card:
	 * 
	 * State: APROVED Type: Mastercard Number: 5031755734530604 CVV: 123 Expire at:
	 * 11/25 Holder: APRO GOMEZ DNI: 31256588 Email: apro_gomez@gmail.com
	 * --------------------------------- State: REJECTED Type: Mastercard Number:
	 * 5031755734530604 CVV: 123 Expire at: 11/25 Holder: EXPI GOMEZ DNI: 31256588
	 * Email: expi_gomez@gmail.com
	 */

	@GetMapping("/createAndRedirect")
	public String createAndRecirect(@RequestParam("item_ids[]") List<String> itemIds,
			@RequestParam("cantidad_[]") List<String> cantidades, @Valid Venta venta, Model model, HttpSession session)
			throws MPException {

		Preference preference = new Preference();

		preference.setBackUrls(new BackUrls().setFailure("http://localhost:8081/error")
				.setPending("http://localhost:8081/pending").setSuccess("http://localhost:8081/success"));

		preference.setAutoReturn(Preference.AutoReturn.approved);

		// Aquíe se crea un item del pago, o una colección de
		// items, que se pueden enviar como parámetro del método createAndRedirect()...

		double total = 0;

		for (int i = 0; i < itemIds.size(); i++) {
			Long id = Long.parseLong(itemIds.get(i));
			int cant = Integer.parseInt(cantidades.get(i));

			Producto producto = productoService.buscarPorId(id);

			if (producto != null) {
				Item item = new Item();
				item.setTitle(producto.getDescripcion())
						.setQuantity(cant)
						.setUnitPrice((float) producto.getPrecioMin());

				// System.out.println(producto.getDescripcion());
				total += cant * producto.getPrecioMin();
				preference.appendItem(item);

				// Verificar de nuevo si hay stock
				if (cant > producto.getStock()) {
					model.addAttribute("titulo", "Nueva Venta");
					model.addAttribute("warning", "No hay stock suficiente...");
					return "ventas/form";
				}

				LineaVenta lineaVenta;
				lineaVenta = new LineaVenta();

				lineaVenta.setProducto(producto);
				lineaVenta.setPrecioActual(producto.getPrecioMin());
				lineaVenta.setCantidad(cant);

				// Importante: actualizar stock del producto...
				// producto.setStock(producto.getStock() - cant);

				venta.addLinea(lineaVenta);
			}

		}

		venta.setDescripcionGral("Compra Online");
		MetodoPago metodoPago = metodoPagoService.buscarPorId(2L);
		venta.setMetodoPago(metodoPago);
		venta.setFechaHora(LocalDateTime.now());
		venta.setTotal(total);

		Usuario usuario = usuarioService.buscarPorId(1L);
		venta.setUsuario(usuario);
		// Obtener el usuario que realizo la compra online para guardarla en la venta:
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails) {
				String username = ((UserDetails) principal).getUsername();

				Cliente cliente = clienteService.findByDni(username);
				if (cliente != null) {
					venta.setCliente(cliente);
					System.out.println(cliente);
				} else {
					model.addAttribute("error", "No se encontró el cliente");
					return "home/home";
				}
			}
		}

		// Una vez generado la venta y el movimiento solo queda generar el pedido online
		// Crear el pedido
		Pedido pedido = new Pedido();
		pedido.setCliente(venta.getCliente());

		Map<Producto, Integer> productosPedido = new HashMap<>();
		Producto producto;
		for (int i = 0; i < itemIds.size(); i++) {
			Long id = Long.parseLong(itemIds.get(i));
			int cantidad = Integer.parseInt(cantidades.get(i));

			producto = productoService.buscarPorId(id);
			if (producto != null) {
				productosPedido.put(producto, cantidad);
			}
		}

		// Asignar los productos al pedido
		pedido.setProductos(productosPedido);
		pedido.setTotal(total);

		// Item item = new Item();
		// item.setTitle("Cerveza Patagonia").setQuantity(1).setUnitPrice((float) 0.50);
		// preference.appendItem(item);

		var result = preference.save();

		model.addAttribute("venta", venta);
		// model.addAttribute("pedido", pedido);
		session.setAttribute("pedido", pedido);

		System.out.println(result.getInitPoint());
		return "redirect:" + result.getInitPoint();
	}

	@GetMapping("/success")
	public String success(@RequestParam(name = "payment_id", defaultValue = "0") Long paymentId,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "external_reference", required = false) String externalReference,
			@RequestParam(name = "merchant_order_id", required = false) String merchantOrderId,
			@RequestParam(name = "payment_type", required = false) String paymentType,
			@RequestParam(name = "preference_id", required = false) String preferenceId,
			@ModelAttribute("venta") Venta venta, @ModelAttribute("pedido") Pedido pedido,
			String merchantAccountId, Model model, SessionStatus sessionStatus, HttpSession session)
			throws MPException {

		model.addAttribute("titulo", "¡Muchas Gracias!");
		model.addAttribute("paymentId", paymentId);
		model.addAttribute("status", status);
		model.addAttribute("externalReference", externalReference);
		model.addAttribute("merchantOrderId", merchantOrderId);

		if (status.equals("approved")) {
			System.out.println("Guardando en la base de datos... ");

			// Descontar stock de los productos del pedido
			for (Map.Entry<Producto, Integer> entry : pedido.getProductos().entrySet()) {
				Producto producto = entry.getKey();
				int cantidadVendida = entry.getValue();

				if (producto.getStock() >= cantidadVendida) {
					producto.setStock(producto.getStock() - cantidadVendida);
					productoService.guardar(producto); // ✅ Guardar cambios en la base de datos
				} else {
					model.addAttribute("error", "Error: No hay suficiente stock para " + producto.getDescripcion());
					return "home/home";
				}
			}

			ventaService.guardar(venta);
			// Hasta acá se guardó las lineas de ventas en la venta, y tambien la linea de
			// venta
			// Ahora guardamos el movimiento tambien:

			Movimiento movimiento = new Movimiento();
			movimiento.setVenta(venta);
			movimientoService.guardar(movimiento);

			// Guardar el pedido en la base de datos
			pedido.setVenta(venta);
			pedidoService.guardar(pedido);
		}

		session.removeAttribute("pedido");
		sessionStatus.setComplete();

		return "success";
	}

	@GetMapping("/failure")
	public String failure(HttpServletRequest request, @RequestParam("collection_id") String collectionId,
			@RequestParam("collection_status") String collectionStatus,
			@RequestParam("external_reference") String externalReference,
			@RequestParam("payment_type") String paymentType, @RequestParam("merchant_order_id") String merchantOrderId,
			@RequestParam("preference_id") String preferenceId, @RequestParam("site_id") String siteId,
			@RequestParam("processing_mode") String processingMode,
			@RequestParam("merchant_account_id") String merchantAccountId, Model model,
			@RequestParam("item_ids[]") List<String> itemIds,
			@RequestParam("cantidad_[]") List<String> cantidades) throws MPException {
		model.addAttribute("preference_id", preferenceId);

		var preference = Preference.findById(preferenceId);
		var order = MerchantOrder.findById(merchantOrderId);
		var payment = com.mercadopago.resources.Payment.findById(collectionId);

		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(order));
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(payment));

		model.addAttribute("items", preference.getItems());
		model.addAttribute("payment", payment);
		return "error";
	}

	@GetMapping("/pending")
	public String pending(HttpServletRequest request, @RequestParam("collection_id") String collectionId,
			@RequestParam("collection_status") String collectionStatus,
			@RequestParam("external_reference") String externalReference,
			@RequestParam("payment_type") String paymentType, @RequestParam("merchant_order_id") String merchantOrderId,
			@RequestParam("preference_id") String preferenceId, @RequestParam("site_id") String siteId,
			@RequestParam("processing_mode") String processingMode,
			@RequestParam("merchant_account_id") String merchantAccountId, Model model) throws MPException {
		// Not implemented yet
		return "pending";
	}
}

// // Primero cargamos la linea de la venta en caso que sea exitoso el pago
// de/los
// // productos:
// LineaVenta lineaVenta; // Instanciamos esto
// Producto producto;

// Cargar las lineas en la nueva venta...
// for (int i = 0; i < itemIds.size(); i++) {

// lineaVenta = new LineaVenta();
// Long id = Long.parseLong(itemIds.get(i)); // pasa el id de la linea de la
// venta a tipo Long
// int cant = Integer.parseInt((cantidades.get(i)));

// producto = productoService.buscarPorId(id);

// lineaVenta.setProducto(producto);
// lineaVenta.setPrecioActual(producto.getPrecioMin());
// lineaVenta.setCantidad(cant);

// // Verificar de nuevo si hay stock
// if (cant > producto.getStock()) {
// model.addAttribute("titulo", "Nueva Venta");
// model.addAttribute("warning", "No hay stock suficiente...");
// return "ventas/form";
// }

// // Importante: actualizar stock del producto...
// producto.setStock(producto.getStock() - cant);

// venta.addLinea(lineaVenta);
// }
// venta.setDescripcionGral("Compra Online");
// MetodoPago metodoPago = metodoPagoService.buscarPorId(2L);
// venta.setMetodoPago(metodoPago);
// venta.setFechaHora(LocalDateTime.now());
// venta.setTotal(total);

// Usuario usuario = usuarioService.buscarPorId(1L);
// venta.setUsuario(usuario);
// // Obtener el usuario que realizo la compra online para guardarla en la
// venta:
// Authentication authentication =
// SecurityContextHolder.getContext().getAuthentication();
// if (authentication != null && authentication.isAuthenticated()) {
// Object principal = authentication.getPrincipal();

// if (principal instanceof UserDetails) {
// String username = ((UserDetails) principal).getUsername();

// Cliente cliente = clienteService.findByDni(username);
// if (cliente != null) {
// venta.setCliente(cliente);
// System.out.println(cliente);
// } else {
// model.addAttribute("error", "No se encontró el cliente");
// return "home/home";
// }
// }
// }

// ventaService.guardar(venta);
// // Hasta acá se guardó las lineas de ventas en la venta, y tambien la linea
// de
// // venta
// // Ahora guardamos el movimiento tambien:

// Movimiento movimiento = new Movimiento();
// movimiento.setVenta(venta);
// movimientoService.guardar(movimiento);

// // Una vez generado la venta y el movimiento solo queda generar el pedido
// online
// // Crear el pedido
// Pedido pedido = new Pedido();
// pedido.setCliente(venta.getCliente());

// Map<Producto, Integer> productosPedido = new HashMap<>();

// for (int i = 0; i < itemIds.size(); i++) {
// Long id = Long.parseLong(itemIds.get(i));
// int cantidad = Integer.parseInt(cantidades.get(i));

// producto = productoService.buscarPorId(id);
// if (producto != null) {
// productosPedido.put(producto, cantidad);
// }
// }

// // Asignar los productos al pedido
// pedido.setProductos(productosPedido);
// pedido.setTotal(total);
// // Guardar el pedido en la base de datos
// pedidoService.guardar(pedido);