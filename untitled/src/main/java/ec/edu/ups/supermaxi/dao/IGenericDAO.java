package ec.edu.ups.supermaxi.dao;

import java.util.List;

/**
 * Interfaz genérica para las operaciones CRUD en JPA.
 *
 * @param <T>  Tipo de la Entidad JPA (ej. Proveedor, Producto)
 * @param <ID> Tipo de la clave primaria @Id (ej. Long, Integer, String)
 */
public interface IGenericDAO<T, ID> {

    // 1. CREATE: Persistir una nueva entidad en la base de datos
    void crear(T entidad);

    // 2. READ: Buscar una entidad por su clave primaria (@Id)
    T buscarPorId(ID id);

    // 2.1 READ: Listar todos los registros de la entidad
    List<T> listarTodos();

    // 3. UPDATE: Sincronizar y actualizar los datos de una entidad existente
    T actualizar(T entidad);

    // 4. DELETE: Eliminar físicamente un registro por su clave primaria
    void eliminar(ID id);
}