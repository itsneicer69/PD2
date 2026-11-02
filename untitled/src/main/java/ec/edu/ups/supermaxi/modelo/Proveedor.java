package ec.edu.ups.supermaxi.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "proveedores")
public class Proveedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruc", nullable = false, unique = true, length = 13)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial;

    @Column(name = "categoria_suministro", nullable = false, length = 60)
    private String categoriaSuministro; // Ej: Lácteos, Cárnicos, Abarrotes, Limpieza

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "correo", nullable = false, length = 100)
    private String correo;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // Relación 1:N con Producto (Un proveedor abastece varios productos al catálogo de Supermaxi)
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    // Relación 1:N con IngresoInventario (Un proveedor registra varias entregas en bodega)
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IngresoInventario> ingresosInventario = new ArrayList<>();

    // Constructor vacío obligatorio para JPA
    public Proveedor() {
    }

    public Proveedor(String ruc, String razonSocial, String categoriaSuministro,
                     String telefono, String correo, String direccion) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.categoriaSuministro = categoriaSuministro;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.activo = true;
    }

    // --- Métodos utilitarios para sincronizar relaciones bidireccionales ---
    public void agregarProducto(Producto producto) {
        productos.add(producto);
        producto.setProveedor(this);
    }

    public void agregarIngresoInventario(IngresoInventario ingreso) {
        ingresosInventario.add(ingreso);
        ingreso.setProveedor(this);
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCategoriaSuministro() {
        return categoriaSuministro;
    }

    public void setCategoriaSuministro(String categoriaSuministro) {
        this.categoriaSuministro = categoriaSuministro;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<IngresoInventario> getIngresosInventario() {
        return ingresosInventario;
    }

    public void setIngresosInventario(List<IngresoInventario> ingresosInventario) {
        this.ingresosInventario = ingresosInventario;
    }

    // --- equals y hashCode basados en el RUC (clave de negocio única) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return Objects.equals(ruc, proveedor.ruc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruc);
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", categoriaSuministro='" + categoriaSuministro + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", activo=" + activo +
                '}';
    }
}