package ec.edu.ups.supermaxi.controlador;

import ec.edu.ups.supermaxi.dao.ProveedorDAO;
import ec.edu.ups.supermaxi.modelo.Proveedor;
import java.util.List;

public class ProveedorControlador {

    private final ProveedorDAO proveedorDAO;

    // Inyección del DAO mediante el constructor
    public ProveedorControlador(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    // 1. CREATE: Validar y registrar un nuevo proveedor
    public void registrarProveedor(String ruc, String razonSocial, String categoriaSuministro,
                                   String telefono, String correo, String direccion) {

        // Validaciones de negocio
        validarDatosObligatorios(ruc, razonSocial, categoriaSuministro, correo);
        validarFormatoRuc(ruc);

        // Verificar que no exista otro proveedor con el mismo RUC en Supermaxi
        Proveedor existente = proveedorDAO.buscarPorRuc(ruc.trim());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un proveedor registrado con el RUC: " + ruc);
        }

        // Instanciar la entidad y persistirla
        Proveedor nuevoProveedor = new Proveedor(
                ruc.trim(),
                razonSocial.trim().toUpperCase(),
                categoriaSuministro.trim(),
                telefono != null ? telefono.trim() : "",
                correo.trim().toLowerCase(),
                direccion != null ? direccion.trim() : ""
        );

        proveedorDAO.crear(nuevoProveedor);
    }

    // 2. READ: Buscar proveedor por ID
    public Proveedor obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser un número válido mayor a cero.");
        }
        return proveedorDAO.buscarPorId(id);
    }

    // 2.1 READ: Buscar proveedor por RUC
    public Proveedor obtenerPorRuc(String ruc) {
        validarFormatoRuc(ruc);
        return proveedorDAO.buscarPorRuc(ruc.trim());
    }

    // 2.2 READ: Listar todos los proveedores activos
    public List<Proveedor> listarProveedoresActivos() {
        return proveedorDAO.listarActivos();
    }

    // 3. UPDATE: Validar y actualizar los datos de un proveedor existente
    public Proveedor actualizarProveedor(Long id, String ruc, String razonSocial, String categoriaSuministro,
                                         String telefono, String correo, String direccion, boolean activo) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Debe especificar un ID válido para actualizar el proveedor.");
        }

        Proveedor proveedorActual = proveedorDAO.buscarPorId(id);
        if (proveedorActual == null) {
            throw new IllegalArgumentException("No se encontró ningún proveedor con el ID: " + id);
        }

        validarDatosObligatorios(ruc, razonSocial, categoriaSuministro, correo);
        validarFormatoRuc(ruc);

        // Verificar si cambió el RUC y si el nuevo RUC ya pertenece a otro proveedor distinto
        Proveedor proveedorConMismoRuc = proveedorDAO.buscarPorRuc(ruc.trim());
        if (proveedorConMismoRuc != null && !proveedorConMismoRuc.getId().equals(id)) {
            throw new IllegalArgumentException("El RUC " + ruc + " ya está asignado a otro proveedor.");
        }

        // Actualizar los atributos de la entidad
        proveedorActual.setRuc(ruc.trim());
        proveedorActual.setRazonSocial(razonSocial.trim().toUpperCase());
        proveedorActual.setCategoriaSuministro(categoriaSuministro.trim());
        proveedorActual.setTelefono(telefono != null ? telefono.trim() : "");
        proveedorActual.setCorreo(correo.trim().toLowerCase());
        proveedorActual.setDireccion(direccion != null ? direccion.trim() : "");
        proveedorActual.setActivo(activo);

        return proveedorDAO.actualizar(proveedorActual);
    }

    // 4. DELETE (Borrado Lógico - Recomendado para no romper el historial de inventario)
    public void desactivarProveedor(Long id) {
        Proveedor proveedor = obtenerPorId(id);
        if (proveedor == null) {
            throw new IllegalArgumentException("No existe el proveedor con ID: " + id);
        }
        proveedor.setActivo(false);
        proveedorDAO.actualizar(proveedor);
    }

    // 4.1 DELETE (Borrado Físico en Base de Datos)
    public void eliminarProveedorFisico(Long id) {
        Proveedor proveedor = obtenerPorId(id);
        if (proveedor == null) {
            throw new IllegalArgumentException("No existe el proveedor con ID: " + id);
        }
        proveedorDAO.eliminar(id);
    }

    // --- MÉTODOS PRIVADOS DE VALIDACIÓN ---

    private void validarDatosObligatorios(String ruc, String razonSocial, String categoria, String correo) {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC del proveedor es obligatorio.");
        }
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La Razón Social es obligatoria.");
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría de suministro es obligatoria.");
        }
        if (correo == null || !correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("Debe ingresar un correo electrónico válido.");
        }
    }

    private void validarFormatoRuc(String ruc) {
        if (ruc == null || !ruc.trim().matches("\\d{13}")) {
            throw new IllegalArgumentException("El RUC debe contener exactamente 13 dígitos numéricos.");
        }
        if (!ruc.trim().endsWith("001")) {
            throw new IllegalArgumentException("El RUC de proveedor debe finalizar en '001'.");
        }
    }
}