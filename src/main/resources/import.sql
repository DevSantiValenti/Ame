INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Mochilas');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Carteras');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Rinioneras');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Vasos Termicos');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Billeteras');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Bolsos');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Termos y Termolares');
INSERT INTO `sistemaame`.`categorias` (`nombre`) VALUES ('Varios');

INSERT INTO `sistemaame`.`permisos` (`nombre`, `descripcion`) VALUES ('ROLE_ADMIN', 'Administrador'), ('ROLE_CAJERO', 'Cajero'), ('ROLE_REPOSITOR', 'Repositor'), ('ROLE_CLIENTE', 'Cliente');

INSERT INTO `sistemaame`.`metodos_pago` (`tipo_pago`) VALUES ('Efectivo');
INSERT INTO `sistemaame`.`metodos_pago` (`tipo_pago`) VALUES ('Transferencia');
INSERT INTO `sistemaame`.`metodos_pago` (`tipo_pago`) VALUES ('Credito');
INSERT INTO `sistemaame`.`metodos_pago` (`tipo_pago`) VALUES ('Debito');

INSERT INTO `usuarios` (`activo`, `id_permiso`, `nombre`, `clave`, `email`) VALUES (1, 1,'santi','$2a$10$nmsnELze.Ca7dMnsbfGIuuczJlKMAk9SGCkgDczmosj91zCAMsFoO', 'santi@gmail.com')



