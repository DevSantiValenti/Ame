package com.analistas.amesistema;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPago;

@SpringBootApplication
public class AmesistemaApplication  implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(AmesistemaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Credenciales de prueba (las pruebas sólo funcionan en un navegador que NO ESTÉ logueado en MP):
		// MercadoPago.SDK.setAccessToken(System.getenv("TEST-721755740151973-112211-7e9b4fce47c7c080f0e528b7c34f09cc-253339153"));	
		// MercadoPago.SDK.setUserToken("TEST-1d94182c-55da-4b1f-a79a-27ec1177136a");

		//Credenciales de produccion
		MercadoPago.SDK.setAccessToken("APP_USR-5235796344928310-030821-1845ae0648b268de2719139bc29e2a93-635436677");
		
		
		MercadoPago.SDK.setUserToken("APP_USR-7f7664dc-404b-45c8-914f-fdbbc506f08a");
		//Credenciales reales (crear aplicación en https://www.mercadopago.com.ar/developers/panel/app)
		// MercadoPago.SDK.setClientId("###############");
		// MercadoPago.SDK.setClientSecret("################################");

		MercadoPago.SDK.setClientId("5235796344928310");
		MercadoPago.SDK.setClientSecret("G71AYwvahukvKYCydXLs0ubKouxnPhSq");
	}

}
